package nl.knaw.dans.easy.tools.task.dump;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.util.FileRemover;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.PropertyList;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.ObjectFields;

/**
 * Dumps EASY dataset metadata in a simple key-value-based format for comparison purposes.
 */
public class MetadataDumpTask extends AbstractTask {
    private class Fatal extends FatalTaskException {
        private static final long serialVersionUID = 7994210008473671395L;

        Fatal(final Throwable t, final String msg, final Object... args) {
            super(String.format(msg, args), t, MetadataDumpTask.this);
            logger.error(String.format(msg, args), t);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MetadataDumpTask.class);

    private static final String AIP_ID_KEY = "previous.collection-id";
    private final DisciplineFinder disciplineFinder = new DisciplineFinder();

    private boolean overwrite = true;
    private boolean dumpDcDataStream = false;
    private boolean useAipIdForOutputFileNames = true;
    private boolean includeDatasetPid = false;
    private boolean includeFilePids = false;
    private boolean includeFileSha1Hash = true;
    private boolean includeMimeType = true;
    private boolean includePermissionSequences = true;
    private boolean includeAdministrativeMetadata = true;
    private boolean includeDeletedDatasets = false;
    private File sidList;

    private File outputDir = new File("output");
    private File aipListOutputDir;
    private String outputEncoding = "UTF-8";
    private List<String> dcFields = Arrays.asList(new String[] {"Title", "Creator"});
    private int startSid = 0;
    private int stopSid = 1000;
    private int maxResults = 1000;
    private int maxTries = 3;
    private PrintWriter aipListWriter;

    @Override
    public void run(final JointMap taskMap) throws FatalTaskException {
        createOutputDir();
        createAipListWriter();
        DatasetFinder finder = sidList == null ? new RangeDatasetFinder().from(startSid).to(stopSid) : new ListDatasetFinder(sidList);
        EasyMetadataReader easyMetadataReader = new EasyMetadataReader(maxTries);
        AmdReader amdReader = new AmdReader();
        PermissionSequenceListReader permSeq = new PermissionSequenceListReader();
        FileItemMetadataReader fileItemMetadataReader = new FileItemMetadataReader().tries(maxTries) //
                .includeFilePids(includeFilePids) //
                .includeFileSha1Hash(includeFileSha1Hash) //
                .includeMimeType(includeMimeType);

        try {
            for (int i = 0; i < maxResults; ++i) {
                final ObjectFields objectFields = finder.next();

                if (objectFields == null) {
                    break;
                }

                final String sid = objectFields.getPid();
                logger.info(String.format("Processing EASY dataset: '%s' ...", sid));
                final AdministrativeMetadata amd = amdReader.read(sid);
                final List<LicenseData> licenses = new LicenseReader(sid).read();

                if (amd.getAdministrativeState() == DatasetState.DELETED && !includeDeletedDatasets) {
                    continue;
                }

                final EasyMetadata easyMetadata = easyMetadataReader.read(sid);
                final List<FileItemMetadataPrinter> files = fileItemMetadataReader.read(sid);
                final String outputFileName;

                if (useAipIdForOutputFileNames) {
                    outputFileName = getAipId(easyMetadata);

                    if (outputFileName == null) {
                        continue;
                    }
                } else {
                    outputFileName = sid;
                }

                aipListWriter.println(outputFileName);

                final File outputFile = createOutputFileName(outputFileName);
                final PrintWriter writer = createPrintWriter(outputFile);

                try {
                    if (dumpDcDataStream) {
                        final DublinCoreMetadata dcMetadata = findDublinCoreMetadata(sid);
                        writeDublicCoreMetatadata(dcMetadata, writer);
                    }

                    if (includeDatasetPid) {
                        writer.println(String.format("DATASET-PID=%s", sid));
                    }

                    writePermSeq(permSeq.read(sid), writer);
                    writeAmd(amd, writer);
                    writeEasyMetadata(easyMetadata, writer);
                    writeFileItemMetadataList(files, writer);
                    writeLicenseMetadata(licenses, writer);
                }
                finally {
                    closeWriter(writer);
                }

                new FileLineSorter(outputFile).sort();
            }
        }
        finally {
            closeWriter(aipListWriter);
        }
    }

    private void writeLicenseMetadata(List<LicenseData> licenses, PrintWriter writer) {
        writer.print(new LicensePrinter(licenses));
    }

    private void writeAmd(AdministrativeMetadata amd, PrintWriter writer) {
        if (!includeAdministrativeMetadata) {
            return;
        }

        writer.print(new AmdPrinter(amd));
    }

    private void writePermSeq(PermissionSequenceList perm, PrintWriter writer) {
        if (!includePermissionSequences) {
            return;
        }

        writer.print(new PermissionSequenceListPrinter(perm));
    }

    private void createAipListWriter() throws FatalTaskException {
        aipListWriter = createPrintWriter(new File(getAipListOutputDir(), "exported-aips.txt"));
    }

    private String getAipId(final EasyMetadata easyMetadata) {
        final List<PropertyList> propertyLists = easyMetadata.getEmdOther().getPropertyListCollection();

        if (propertyLists == null || propertyLists.isEmpty()) {
            return null;
        }

        final PropertyList list = propertyLists.get(0);
        final List<PropertyList.Property> properties = list.getProperties();

        return getProperty(properties, AIP_ID_KEY);
    }

    private String getProperty(final List<PropertyList.Property> properties, final String key) {
        for (final PropertyList.Property property : properties) {
            if (key.equals(property.getKey())) {
                return property.getValue();
            }
        }

        return null;
    }

    private void createOutputDir() throws FatalTaskException {
        logger.info(String.format("Creating output directory '%s' ...", outputDir));
        if (outputDir.exists()) {
            if (!overwrite) {
                logger.error(String.format("Output directory '%s' could not be created because it already exists.  Use overwrite property.", outputDir));
                throw new FatalTaskException(String.format("Directory already exists: %s", outputDir), this);
            }

            new FileRemover(outputDir).remove();
        }

        outputDir.mkdirs();
    }

    private DublinCoreMetadata findDublinCoreMetadata(final String pid) throws FatalTaskException {
        try {
            return Application.getFedora().getDatastreamAccessor().getDublinCoreMetadata(pid, null);
        }
        catch (final RepositoryException e) {
            throw new Fatal(e, "Could not read dublin core metadata");
        }
    }

    private File createOutputFileName(final String pid) {
        return new File(outputDir, pid);
    }

    private PrintWriter createPrintWriter(final File outputFile) throws FatalTaskException {
        try {
            return new PrintWriter(outputFile, outputEncoding);
        }
        catch (final FileNotFoundException e) {
            throw new Fatal(e, "Could not write to output file: '%s'", outputFile);
        }
        catch (final UnsupportedEncodingException e) {
            throw new Fatal(e, "Could write to output file '%s' because of unsupported encoding: '%s'", outputFile, e.getMessage());
        }
    }

    private void writeDublicCoreMetatadata(final DublinCoreMetadata dcMetadata, final PrintWriter writer) throws FatalTaskException {
        for (final String field : dcFields) {
            final PropertyName pn = PropertyName.valueOf(field);

            if (pn == null) {
                logger.error(String.format("Invalid property: %s", field));
                throw new FatalTaskException(String.format("Invalid property: %s", field), this);
            }

            new ItemPrinter(writer, "DC").printItems(field, dcMetadata.get(pn));
        }
    }

    private void writeEasyMetadata(final EasyMetadata emd, final PrintWriter writer) throws FatalTaskException {
        final ItemPrinter printer = new ItemPrinter(writer, "EMD");

        printer.printItems("title", emd.getEmdTitle().getValues());
        try {
            printer.printItems("audience", disciplineFinder.find(emd.getEmdAudience().getValues()));
        }
        catch (FatalException e) {
            throw new FatalTaskException(e, this);
        }
        printer.printItems("contributor", emd.getEmdContributor().getValues());
        printer.printItems("coverage", emd.getEmdCoverage().getValues());
        printer.printItems("creator", emd.getEmdCreator().getValues());
        printer.printItems("dateDc", basicDatesToStrings(emd.getEmdDate().getDcDate()));
        printer.printItems("dateCreated",
                takeFirstNonEmpty(basicDatesToStrings(emd.getEmdDate().getTermsCreated()), isoDatesToStrings(emd.getEmdDate().getEasCreated())));
        printer.printItems("dateSubmitted",
                takeFirstNonEmpty(basicDatesToStrings(emd.getEmdDate().getTermsDateSubmitted()), isoDatesToStrings(emd.getEmdDate().getEasDateSubmitted())));
        printer.printItems(
                "dateAvailable",
                takeFirstNonEmpty(basicDatesToStrings(emd.getEmdDate().getTermsAvailable()), isoDatesToStrings(emd.getEmdDate().getEasAvailable()),
                        dateTimeToSingeltonStringList(emd.getEmdDate().getDateAvailable())));
        printer.printItems("description", emd.getEmdDescription().getValues());
        printer.printItems("format", emd.getEmdFormat().getValues());
        printer.printItems("identifier", formatIdentifierStrings(emd.getEmdIdentifier()));
        printer.printItems("language", emd.getEmdLanguage().getValues());
        printer.printItems("publisher", emd.getEmdPublisher().getValues());
        printer.printItems("relation", emd.getEmdRelation().getValues());
        printer.printItems("rights", emd.getEmdRights().getValues());
        printer.printItems("source", emd.getEmdSource().getValues());
        printer.printItems("subject", emd.getEmdSubject().getValues());
        printer.printItems("type", emd.getEmdType().getValues());
        printer.printItems("remarks", new BasicRemarksReader(emd.getEmdOther().getEasRemarks()).getRemarks());
    }

    private List<String> takeFirstNonEmpty(List<String>... lists) {
        for (List<String> list : lists) {
            if (list != null && list.size() > 0 && list.get(0) != null) {
                return list;
            }
        }

        return null;
    }

    private List<String> basicDatesToStrings(List<BasicDate> dates) {
        List<String> result = new LinkedList<String>();

        for (BasicDate date : dates) {
            result.add(date.toString());
        }

        return result;
    }

    private List<String> isoDatesToStrings(List<IsoDate> dates) {
        List<String> result = new LinkedList<String>();

        for (IsoDate date : dates) {
            result.add(date.toString());
        }

        return result;
    }

    private List<String> dateTimeToSingeltonStringList(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return Arrays.asList(dateTime.toString("YYYY-MM-dd"));
    }

    private List<String> formatIdentifierStrings(final EmdIdentifier emdIdentifier) {
        final List<String> ids = new ArrayList<String>();

        for (final BasicIdentifier id : emdIdentifier.getDcIdentifier()) {
            if (EmdConstants.SCHEME_DMO_ID.equals(id.getScheme())) {
                continue;
            }

            ids.add(String.format("%s;%s", id.getScheme() == null ? "" : id.getScheme(), id.getValue()));
        }

        return ids;
    }

    private void writeFileItemMetadataList(final List<FileItemMetadataPrinter> files, final PrintWriter writer) {
        for (final FileItemMetadataPrinter file : files) {
            writer.print(file);
        }
    }

    private void closeWriter(final Writer writer) throws FatalTaskException {
        try {
            writer.close();
        }
        catch (final IOException e) {
            logger.error("Could not close writer");
            throw new FatalTaskException(e, this);
        }
    }

    public void setOverwrite(final boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void setOutputDir(final File outputDir) {
        this.outputDir = outputDir;
    }

    public void setOutputEncoding(final String encoding) {
        this.outputEncoding = encoding;
    }

    public void setDcFields(final List<String> fields) {
        this.dcFields = fields;
    }

    public void setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
    }

    public void setDumpDcDataStream(final boolean dumpDcDataStream) {
        this.dumpDcDataStream = dumpDcDataStream;
    }

    public void setUseAipIdForOutputFileNames(final boolean skipIfNoAipIdFound) {
        this.useAipIdForOutputFileNames = skipIfNoAipIdFound;
    }

    public void setIncludeDatasetPid(final boolean includePid) {
        this.includeDatasetPid = includePid;
    }

    public void setIncludeFilePids(final boolean includeFilePids) {
        this.includeFilePids = includeFilePids;
    }

    public void setIncludeFileSha1Hash(final boolean includeFileSha1Hash) {
        this.includeFileSha1Hash = includeFileSha1Hash;
    }

    public void setIncludeMimeType(boolean includeMimeType) {
        this.includeMimeType = includeMimeType;
    }

    private File getAipListOutputDir() {
        return aipListOutputDir == null ? outputDir : aipListOutputDir;
    }

    public void setAipListOutputDir(File aipListOutputDir) {
        this.aipListOutputDir = aipListOutputDir;
    }

    public void setStartSid(int startSid) {
        this.startSid = startSid;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public void setStopSid(int stopSid) {
        this.stopSid = stopSid;
    }

    public void setIncludePermissionSequences(boolean includePermissionSequences) {
        this.includePermissionSequences = includePermissionSequences;
    }

    public void setIncludeAdministrativeMetadata(boolean includeAdministrativeMetadata) {
        this.includeAdministrativeMetadata = includeAdministrativeMetadata;
    }

    public void setIncludeDeletedDatasets(boolean includeDeletedDatasets) {
        this.includeDeletedDatasets = includeDeletedDatasets;
    }

    public void setSidList(File sidList) {
        this.sidList = sidList;
    }
}
