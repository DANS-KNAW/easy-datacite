package nl.knaw.dans.easy.ebiu.task;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.xml.ResourceMetadataList;
import nl.knaw.dans.easy.xml.ResourceMetadataListValidator;

import org.xml.sax.SAXException;

/**
 * Collects resource metadata (optional step).
 */
public class ResourceMetadataCollector extends AbstractTask {

    public static final String DEFAULT_RELATIVE_PATH = "metadata/resource-metadata-list.xml";

    private final String relativePath;

    public ResourceMetadataCollector() {
        this(DEFAULT_RELATIVE_PATH);
    }

    public ResourceMetadataCollector(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        File mdFile = validateFile(joint);
        if (mdFile != null) {
            validateXml(mdFile);
            ResourceMetadataList rml = readFile(mdFile);
            joint.setResourceMetadataList(rml);
        }
    }

    private ResourceMetadataList readFile(File mdFile) throws TaskCycleException {
        ResourceMetadataList rml;
        try {
            rml = (ResourceMetadataList) JiBXObjectFactory.unmarshal(ResourceMetadataList.class, mdFile);
        }
        catch (XMLDeserializationException e) {
            RL.error(new Event("Unparsable resource metadata", e, e.getMessage()));
            throw new TaskCycleException("Unparsable resource metadata", e, this);
        }
        catch (Exception e) {
            RL.error(new Event("Failure reading resource metadata", e, e.getMessage()));
            throw new TaskCycleException("Failure reading resource metadata", e, this);
        }
        return rml;
    }

    private void validateXml(File mdFile) throws TaskCycleException, FatalTaskException {
        try {
            XMLErrorHandler handler = ResourceMetadataListValidator.instance().validate(mdFile, null);
            if (!handler.passed()) {
                RL.error(new Event("Invalid resource metadata", handler.getMessages()));
                throw new TaskCycleException("Invalid resource metadata", this);
            }
        }
        catch (ValidatorException e) {
            throw new TaskCycleException(e, this);
        }
        catch (SAXException e) {
            throw new TaskCycleException(e, this);
        }
        catch (SchemaCreationException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private File validateFile(JointMap joint) throws TaskCycleException {
        File currentFile = joint.getCurrentDirectory();
        File mdFile = new File(currentFile, relativePath);
        if (!mdFile.exists()) {
            String msg = "Not found " + mdFile.getPath();
            RL.info(new Event("No resource metadata", msg));
            return null;
        }
        if (!mdFile.canRead()) {
            String msg = "Cannot read " + mdFile.getPath();
            RL.error(new Event("No access to resource metadata", msg));
            throw new TaskCycleException(msg, this);
        }
        return mdFile;
    }

}
