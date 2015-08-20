package nl.knaw.dans.easy.ebiu.task;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;
import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;

import org.xml.sax.SAXException;

public class EasyMetadataCollector extends AbstractTask {

    public static final String DEFAULT_RELATIVE_PATH = "metadata/easymetadata.xml";

    private final String relativePath;

    public EasyMetadataCollector() {
        this(DEFAULT_RELATIVE_PATH);
    }

    public EasyMetadataCollector(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        File emdFile = validateFile(joint);
        validateXml(emdFile);
        EasyMetadata emd = readEasyMetadata(emdFile);
        joint.setEasyMetadata(emd);
        RL.info(new Event(getTaskName(), "Collected easymetadata from " + emdFile.getPath()));
    }

    private EasyMetadata readEasyMetadata(File emdFile) throws FatalTaskException {
        EasyMetadata emd;
        try {
            emd = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, emdFile);
        }
        catch (XMLDeserializationException e) {
            RL.error(new Event(getTaskName(), e, "Unparsable easymetadata", e.getMessage()));
            throw new TaskException("Unparsable easymetadata", e, this);
        }
        catch (Exception e) {
            RL.error(new Event(getTaskName(), e, "Failure reading easymetadata", e.getMessage()));
            throw new TaskCycleException("Failure reading easymetadata", e, this);
        }
        return emd;
    }

    private void validateXml(File emdFile) throws FatalTaskException {
        try {
            XMLErrorHandler handler = EMDValidator.instance().validate(emdFile, EasyMetadataImpl.EMD_VERSION);
            if (!handler.passed()) {
                RL.error(new Event(getTaskName(), "Invalid easymetadata", handler.getMessages()));
                throw new TaskException("Invalid easymetadata", this);
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

    private File validateFile(JointMap joint) throws TaskException {
        File currentFile = joint.getCurrentDirectory();
        File emdFile = new File(currentFile, relativePath);
        if (!emdFile.exists()) {
            String msg = "Not found " + emdFile.getPath();
            RL.error(new Event(getTaskName(), "No easymetadata", msg));
            throw new TaskException(msg, this);
        }
        if (!emdFile.canRead()) {
            String msg = "Cannot read " + emdFile.getPath();
            RL.error(new Event(getTaskName(), "No access to easymetadata", msg));
            throw new TaskException(msg, this);
        }
        return emdFile;
    }

}
