package nl.knaw.dans.easy.tools.task.adhoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLTransformer;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dataset.DatasetIterator;
import nl.knaw.dans.easy.tools.dataset.MetadataFormatFilter;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalRuntimeException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;
import nl.knaw.dans.easy.tools.util.RepoUtil;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcheologyMetadataMigration extends AbstractTask {

    private static final String DISC_ARCHEO_ID = "easy-discipline:2";

    private static final String SCHEME_ID = ChoiceListGetter.CHOICELIST_CUSTOM_PREFIX + ChoiceListGetter.CHOICELIST_DISCIPLINES_POSTFIX;

    public static final String DATAFILE_POSTFIX = "/metadata/dc-arch/data.xml";

    public static final URI ARCHIS_URI = URI.create("http://archis2.archis.nl");

    private static final Logger logger = LoggerFactory.getLogger(ArcheologyMetadataMigration.class);

    private final XMLTransformer xmlTransformer;
    private final File aipDataDir;

    private int archDatasetCounter;
    private int convertedCounter;

    public ArcheologyMetadataMigration(String aipDataDirName, String xsltFileName, String transformerFactoryName) throws FatalTaskException {
        try {
            xmlTransformer = new XMLTransformer(xsltFileName, transformerFactoryName);
        }
        catch (TransformerConfigurationException e) {
            throw new FatalTaskException(e, this);
        }
        catch (TransformerFactoryConfigurationError e) {
            throw new FatalTaskException(e, this);
        }
        aipDataDir = new File(aipDataDirName);
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        try {
            RepoUtil.checkListenersActive();
        }
        catch (NoListenerException e) {
            throw new FatalTaskException("No searchEngine", e, this);
        }

        // try the Easy1 thing
        try {
            Easy1.getCatIdDisciplineMap();
            Easy1.getOIDisciplineMap();
        }
        catch (FatalException e) {
            throw new FatalTaskException(e, this);
        }

        DatasetIterator diter = new DatasetIterator(new MetadataFormatFilter(MetadataFormat.ARCHAEOLOGY));
        try {
            while (diter.hasNext()) {
                archDatasetCounter++;
                process(diter.next());
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        finally {
            logger.info("Converted " + convertedCounter + " datasets of " + archDatasetCounter);
        }
    }

    private void process(Dataset dataset) {
        try {
            EasyMetadata emd = processMetadata(dataset);
            ((DatasetImpl) dataset).setEasyMetadata(emd);
            saveMetadata(dataset);

            RL.info(new Event("converted", dataset.getStoreId()));
            logger.info("Converted " + dataset.getStoreId());
            convertedCounter++;
        }
        catch (TaskExecutionException e) {
            logger.error("Exception while converting " + dataset.getStoreId(), e);
        }
    }

    private void saveMetadata(Dataset dataset) throws TaskExecutionException {
        try {
            Data.getEasyStore().update(dataset, getTaskName());
        }
        catch (RepositoryException e) {
            throw new TaskExecutionException("Could not update", e);
        }

    }

    private EasyMetadata processMetadata(Dataset dataset) throws TaskExecutionException {
        String aipId = dataset.getEasyMetadata().getEmdIdentifier().getAipId();
        if (aipId == null) {
            RL.error(new Event("No AipId", dataset.getStoreId(), "datasetState=" + dataset.getAdministrativeState(), "depositor="
                    + dataset.getAdministrativeMetadata().getDepositorId()));
            throw new TaskExecutionException("No aipId.");
        }
        Properties.setCollectionId(aipId);
        File dataFile = new File(aipDataDir, aipId + DATAFILE_POSTFIX);
        if (!dataFile.exists()) {
            throw new TaskExecutionException("No dataFile.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            xmlTransformer.transform(dataFile, out);
        }
        catch (TransformerException e) {
            throw new TaskExecutionException("Could not transform", e);
        }
        catch (IOException e) {
            throw new TaskExecutionException("IOException", e);
        }

        String storeId = dataset.getStoreId();
        EasyMetadata emd;
        try {
            emd = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, out.toByteArray());

            splitArchisOMN(storeId, emd);
            refine(storeId, emd);
        }
        catch (XMLDeserializationException e) {
            throw new TaskExecutionException("xml exception", e);
        }
        return emd;

    }

    private void refine(String storeId, EasyMetadata emd) {
        BasicIdentifier bi = new BasicIdentifier(storeId);
        bi.setScheme(EmdConstants.SCHEME_DMO_ID);
        emd.getEmdIdentifier().add(bi);

        try {
            List<BasicString> termsAudience = new ArrayList<BasicString>();
            for (BasicString bs : emd.getEmdAudience().getTermsAudience()) {
                if ("OI-KNAW".equals(bs.getScheme())) {
                    DisciplineContainer dc = Easy1.getOIDisciplineMap().get(bs.getValue());
                    if (dc == null) {
                        logger.warn("unknown OI-code", bs.getValue());
                        RL.info(new Event("unknown OI-code", storeId, bs.getValue()));
                    } else {
                        termsAudience.add(bs);
                    }
                } else {
                    termsAudience.add(bs);
                }
            }
            emd.getEmdAudience().setTermsAudience(termsAudience);

            // convert audience to discipline
            for (BasicString bs : emd.getEmdAudience().getTermsAudience()) {
                if ("OI-KNAW".equals(bs.getScheme())) {
                    DisciplineContainer dc = Easy1.getOIDisciplineMap().get(bs.getValue());

                    bs.setValue(dc.getStoreId());
                    bs.setSchemeId(SCHEME_ID);
                    bs.setScheme(null);
                }
            }

            // now check if discipline archaeology is there.
            // <emd:audience><dcterms:audience
            // eas:schemeId="custom.disciplines">easy-discipline:2</dcterms:audience></emd:audience>
            boolean found = false;
            for (BasicString bs : emd.getEmdAudience().getTermsAudience()) {
                if (SCHEME_ID.equals(bs.getSchemeId()) && DISC_ARCHEO_ID.equals(bs.getValue())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                BasicString dcTermsAudience = new BasicString(DISC_ARCHEO_ID);
                dcTermsAudience.setSchemeId(SCHEME_ID);
                emd.getEmdAudience().getTermsAudience().add(dcTermsAudience);
                RL.info(new Event("archaeolgy discipline added", storeId, "because metadataformat is archaeology"));
            }
        }
        catch (FatalException e) {
            throw new FatalRuntimeException(e);
        }
    }

    private void splitArchisOMN(String storeId, EasyMetadata emd) {
        List<BasicIdentifier> biList = emd.getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);

        for (BasicIdentifier bi : biList) {
            String multipleAomn = bi.getValue();
            String[] aomns = multipleAomn.split(";");
            if (aomns.length > 0) {
                RL.info(new Event("Found archis omn", storeId, multipleAomn));
            }
            // <dc:identifier eas:scheme="Archis_onderzoek_m_nr" eas:schemeId="archaeology.dc.identifier"
            // eas:identification-system="http://archis2.archis.nl">33264</dc:identifier>
            for (String aomn : aomns) {
                BasicIdentifier newbi = new BasicIdentifier(aomn.trim());
                newbi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
                newbi.setSchemeId("archaeology.dc.identifier");
                newbi.setIdentificationSystem(ARCHIS_URI);
                emd.getEmdIdentifier().add(newbi);
            }
        }

    }

}
