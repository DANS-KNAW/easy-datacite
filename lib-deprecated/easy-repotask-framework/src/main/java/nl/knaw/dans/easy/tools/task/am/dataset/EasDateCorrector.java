package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.Reporter;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdDate;
import nl.knaw.dans.pf.language.emd.PropertyList;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.joda.time.DateTime;

// issue: EMD:dateDc ontbreekt in E2 (1990 datasets)
//
// Voorbeeld van zo'n gemiste datum (natuurlijk weer uit archeology, die weer lekker hebben zitten
// rommelen)
//
// <dc:date>
// <value dcterms:W3CDTF="true" format="yyyy" scheme="" schemeLabel="">
// <stringValue>2003</stringValue>
// <date>2003-01-01T00:00:00+01:00</date>
// </value>
// <value dcterms:W3CDTF="true" format="yyyy-MM-dd" scheme="dateSubmitted" schemeLabel="Date submitted">
// <stringValue>2009-05-08</stringValue>
// <date>2009-05-08T00:00:00+02:00</date>
// </value>
// </dc:date>
//
// dateSubmitted is wel overgekomen, de datum daarboven niet.
//
// Uit de xslt:
// <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][not(@scheme)]">
//
// xslt pakt dus datums zonder 'scheme' om ze te coverteren naar eas:date. De bovenstaande mist ie, omdat
// scheme="".

public class EasDateCorrector extends AbstractTask {

    public static final String METADATA_DIR = "metadata";
    public static final String DC_SIMPLE = "dc-simple";
    public static final String DC_ARCH = "dc-arch";
    public static final String METADATA_FILE = "data.xml";
    public static final String XPATH_DCDATE = "/EasyMetadata/dc:date/value[@dcterms:W3CDTF='true'][@scheme='']";

    public static final String XPATH_DCDATE_TEST = "/EasyMetadata/dc:date";

    private static final String DATE_DC_AIPS = "data/migration/eas-date-corrector/date_dc_aips.txt";

    private final File baseDir;
    private final Set<String> aipIdSet = new LinkedHashSet<String>();

    private int correctedCount;
    private String currentStoreId;

    /**
     * @param basePath
     *        path to easy1 data: i.e. /mnt/sara1022/aipstore/data/, known as 'aipstore.data.directory' in the application.properties.
     * @throws IOException
     */
    public EasDateCorrector(String basePath) throws IOException {
        baseDir = new File(basePath);
        readAipIdSet();

    }

    private void readAipIdSet() throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(DATE_DC_AIPS, "r");
            String aipId;
            while ((aipId = raf.readLine()) != null) {
                aipIdSet.add(aipId);
            }
        }
        finally {
            if (raf != null) {
                raf.close();
            }
        }

    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        RL.info(new Event("count", "number of aipId's to process", "" + aipIdSet.size()));
        for (String aipId : aipIdSet) {
            IdMap idMap = getMostRecentIdMap(aipId);
            if (idMap == null) {
                RL.error(new Event("no idMap", aipId));
            } else {
                process(idMap);
            }
        }
        Reporter.closeAllFiles();
        RL.info(new Event("count", "count of corrected", "" + correctedCount));
    }

    private void process(IdMap idMap) throws FatalTaskException {

        Dataset dataset;
        String storeId = idMap.getStoreId();
        try {
            dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));

        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("dataset not found", storeId));
            return;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        boolean dirty = false;
        EasyMetadata emd = dataset.getEasyMetadata();
        EmdDate dateContainer = emd.getEmdDate();
        List<Node> nodes = getMetadataReader(idMap.getAipId()).getNodes(XPATH_DCDATE_TEST);
        try {
            Reporter.appendReport("test-date.txt", "---------------------------------------------------------\n");
            Reporter.appendReport("test-date.txt", storeId + " " + idMap.getAipId() + "\n");
            for (Node node : nodes) {
                Reporter.appendReport("test-date.txt", node.asXML() + "\n");
            }
        }
        catch (IOException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void save(Dataset dataset) throws FatalTaskException {
        List<PropertyList> propertyLists = dataset.getEasyMetadata().getEmdOther().getPropertyListCollection();
        PropertyList propertyList;
        if (propertyLists == null || propertyLists.isEmpty()) {
            RL.warn(new Event(getTaskName(), "Trying to set a task stamp on a dataset that was not part of migration", dataset.getStoreId()));
            return;
        } else {
            propertyList = propertyLists.get(0);
        }
        propertyList.addProperty(this.getClass().getName(), new DateTime().toString());
        try {
            Data.getEasyStore().update(dataset, getTaskName());
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private IdMap getMostRecentIdMap(String aipId) throws FatalTaskException {
        try {
            IdMap idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
            return idMap;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
    }

    protected Dom4jReader getMetadataReader(String aipId) throws TaskException {
        Dom4jReader reader;
        File metadataFile = getMetadataFile(aipId);

        try {
            reader = new Dom4jReader(metadataFile);
        }
        catch (DocumentException e) {
            RL.error(new Event(getTaskName(), e, "Cannot read metadatadata", currentStoreId, aipId));
            throw new TaskException(e, this);
        }

        return reader;
    }

    protected File getMetadataFile(String aipId) throws TaskException {
        File datasetDir = getAipDirectory(aipId);

        File metadataDir = new File(datasetDir, METADATA_DIR);
        if (!metadataDir.exists()) {
            RL.error(new Event(getTaskName(), "metadata directory not found", currentStoreId, metadataDir.getPath()));
            throw new TaskException("metadata directory not found: " + metadataDir.getPath(), this);
        }

        File subDir = new File(metadataDir, DC_SIMPLE);
        if (!subDir.exists()) {
            subDir = new File(metadataDir, DC_ARCH);
        }
        if (!subDir.exists()) {
            RL.error(new Event(getTaskName(), "metadata subdirectory not found", currentStoreId, subDir.getPath()));
            throw new TaskException("metadata subdirectory not found: " + subDir.getPath(), this);
        }

        File metadataFile = new File(subDir, METADATA_FILE);
        if (!metadataFile.exists()) {
            RL.error(new Event(getTaskName(), "data file not found", currentStoreId, metadataFile.getPath()));
            throw new TaskException("data file not found: " + metadataFile.getPath(), this);
        }
        return metadataFile;
    }

    protected File getAipDirectory(String aipId) throws TaskException {
        File datasetDir = new File(baseDir, aipId);
        if (!datasetDir.exists()) {
            RL.error(new Event(getTaskName(), "Twips directory not found", currentStoreId, datasetDir.getPath()));
            throw new TaskException("Twips directory not found: " + datasetDir.getPath(), this);
        }
        return datasetDir;
    }

}
