package nl.knaw.dans.easy.tools.imex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.xml.SchemaCache;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.common.lang.xml.XMLValidator;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import fedora.common.Constants;

public class DobIngestTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(DobIngestTask.class);

    private final String foxmlSchemaURL;
    private List<String> dobList = new ArrayList<String>();
    private boolean purgingBeforeIngest;

    private int ingestCount;
    private int purgeCount;

    public DobIngestTask() {
        foxmlSchemaURL = ResourceLocator.getURL("xsd/foxml1-1.xsd").toString();
    }

    public List<String> getDobList() {
        return dobList;
    }

    public void setDobList(List<String> dobList) {
        this.dobList = dobList;
    }

    public void setDobDirectory(String dobDirectory) {
        dobList.clear();
        dobList.add(dobDirectory);
    }

    public boolean isPurgingBeforeIngest() {
        return purgingBeforeIngest;
    }

    public void setPurgeBeforeIngest(boolean purgingBeforeIngest) {
        this.purgingBeforeIngest = purgingBeforeIngest;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        ingestCount = 0;
        purgeCount = 0;
        for (String filename : dobList) {
            File file = new File(filename);
            scanFile(file);
        }
        logger.info("ingestCount=" + ingestCount + " purgeCount=" + purgeCount);
    }

    private void scanFile(File file) throws FatalTaskException {
        if (file.isDirectory()) {
            for (File kid : file.listFiles()) {
                scanFile(kid);
            }
        } else {
            scanXmlFile(file);
        }
    }

    private void scanXmlFile(File file) throws FatalTaskException {
        if (!file.getName().endsWith(".xml")) {
            logger.info("File name does not end with '.xml'; not ingesting " + file.getName());
            return;
        } else {
            addDigitalObject(file);
        }
    }

    private void addDigitalObject(File file) throws FatalTaskException {
        try {
            validate(file);
        }
        catch (SchemaCreationException e) {
            String msg = "Could not create shema";
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
            return;
        }
        catch (IOException e) {
            String msg = "Could not read file " + file.getName();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
            return;
        }
        catch (SAXException e) {
            String msg = "Error during parsing of file " + file.getName();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
            return;
        }
        catch (ValidatorException e) {
            String msg = "Validation exception in file " + file.getName();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
            return;
        }

        if (purgingBeforeIngest) {
            try {
                purge(file);
            }
            catch (XMLDeserializationException e) {
                String msg = "Could not read object represented by " + file.getAbsolutePath();
                logger.error(msg, e);
                RL.error(new Event("Ingest dob", e, msg));
                return;
            }
            catch (RepositoryException e) {
                String msg = "Could not purge object represented by " + file.getAbsolutePath();
                logger.error(msg, e);
                RL.error(new Event("Ingest dob", e, msg));
                return;
            }
        }

        try {
            ingest(file);
        }
        catch (IOException e) {
            String msg = "Could not read file " + file.getAbsolutePath();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
        }
        catch (RepositoryException e) {
            String msg = "Could not ingest file " + file.getAbsolutePath();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
        }
        catch (XMLDeserializationException e) {
            String msg = "Could not deserialize file " + file.getAbsolutePath();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
        }
        catch (XMLSerializationException e) {
            String msg = "Could not serialize digital object from " + file.getAbsolutePath();
            logger.error(msg, e);
            RL.error(new Event("Ingest dob", e, msg));
        }
    }

    private void validate(File file) throws SchemaCreationException, IOException, SAXException, ValidatorException {
        Source xmlSource = new StreamSource(file);
        Schema schema = SchemaCache.getSchema(foxmlSchemaURL);
        XMLErrorHandler result = XMLValidator.validate(xmlSource, schema);
        if (!result.passed()) {
            throw new ValidatorException(result.getMessages());
        }
    }

    private void ingest(File file) throws IOException, ObjectExistsException, RepositoryException, XMLDeserializationException, XMLSerializationException {
        DigitalObject dob = (DigitalObject) JiBXObjectFactory.unmarshal(DigitalObject.class, file);
        removeDatastreamVersions(dob);
        byte[] objectXML = dob.asObjectXML(4);
        Application.getFedora().getObjectManager().ingest(objectXML, Constants.FOXML1_1.uri, "Ingested by " + this.getClass().getSimpleName());
        ingestCount++;
        logger.info(ingestCount + " Ingested " + file.getName());
    }

    /**
     * We have to remove older versions of datastreams, because the mechanism for deciding which version is the latest works with datastreamVersion@CREATED.
     * However, Fedora is replacing these dates with the actual date at ingest.
     * 
     * @param dob
     */
    private void removeDatastreamVersions(DigitalObject dob) {
        for (Datastream ds : dob.getDatastreams()) {
            DatastreamVersion latest = ds.getLatestVersion();
            logger.debug("Latest version of " + ds.getStreamId() + " is " + latest.getVersionId());
            List<String> versionIds = new ArrayList<String>();
            for (DatastreamVersion dsv : ds.getDatastreamVersions()) {
                versionIds.add(dsv.getVersionId());
            }
            for (String versionId : versionIds) {
                if (!versionId.equals(latest.getVersionId())) {
                    ds.removeDatastreamVersion(versionId);
                    logger.debug("Removed from " + ds.getStreamId() + " datastreamVersion " + versionId);
                }
            }
            logger.debug("Kept in " + ds.getStreamId() + " datastreamVersion " + latest.getVersionId());
        }
    }

    private void purge(File file) throws XMLDeserializationException, RepositoryException {
        DigitalObject dob = (DigitalObject) JiBXObjectFactory.unmarshal(DigitalObject.class, file);
        String sid = dob.getSid();
        if (StringUtils.isNotBlank(sid)) {
            try {
                Application.getFedora().getObjectManager().purgeObject(sid, false, "Purging before ingest, " + this.getClass().getSimpleName());
                purgeCount++;
                logger.info(purgeCount + " Purged " + file.getName());
            }
            catch (ObjectNotInStoreException e) {
                logger.info("No object with sid " + sid + " in store. Skipping purge before ingest.");
            }
        }
    }

}
