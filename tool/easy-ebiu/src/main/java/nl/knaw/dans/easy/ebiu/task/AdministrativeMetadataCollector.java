package nl.knaw.dans.easy.ebiu.task;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataValidator;
import nl.knaw.dans.easy.domain.dataset.WorkflowDataImpl;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.workflow.WorkflowFactory;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;

import org.xml.sax.SAXException;

public class AdministrativeMetadataCollector extends AbstractTask {

    public static final String DEFAULT_RELATIVE_PATH = "metadata/administrative-metadata.xml";

    private final String relativePath;

    public AdministrativeMetadataCollector() {
        this(DEFAULT_RELATIVE_PATH);
    }

    public AdministrativeMetadataCollector(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        File amdFile = validateFile(joint);
        validateXml(amdFile);
        AdministrativeMetadata amd = readAdministrativeMetadata(amdFile);
        amd = normalize(amd);
        joint.setAdministrativeMetadata(amd);
        RL.info(new Event(getTaskName(), "Collected administrative metadata from " + amdFile.getPath()));
    }

    private AdministrativeMetadata normalize(AdministrativeMetadata amd) {
        AdministrativeMetadata normalizedAmd = amd;

        WorkflowStep rawWfs = amd.getWorkflowData().getWorkflow();
        WorkflowStep normalizedWfs = WorkflowFactory.newDatasetWorkflow();
        normalizeWorkflow(normalizedWfs, rawWfs);
        ((WorkflowDataImpl) normalizedAmd.getWorkflowData()).setWorkflow(normalizedWfs);

        return normalizedAmd;
    }

    private void normalizeWorkflow(WorkflowStep normalizedWfs, WorkflowStep rawWfs) {
        String id = rawWfs.getId();
        WorkflowStep step = normalizedWfs.getStep(id);
        if (step == null) {
            RL.warn(new Event(getTaskName(), "Unknown workflow step: " + id + ". Skipping this step."));
        } else {
            step.copyValues(rawWfs);
        }

        for (WorkflowStep kidStep : rawWfs.getSteps()) {
            normalizeWorkflow(normalizedWfs, kidStep);
        }
    }

    private AdministrativeMetadata readAdministrativeMetadata(File amdFile) throws TaskException, TaskCycleException {
        AdministrativeMetadata amd;
        try {
            amd = (AdministrativeMetadata) JiBXObjectFactory.unmarshal(AdministrativeMetadataImpl.class, amdFile);
        }
        catch (XMLDeserializationException e) {
            RL.error(new Event(getTaskName(), e, "Unparsable administrative metadata", e.getMessage()));
            throw new TaskException("Unparsable administrative metadata", e, this);
        }
        catch (Exception e) {
            RL.error(new Event(getTaskName(), e, "Failure reading administrative metadata", e.getMessage()));
            throw new TaskCycleException("Failure reading administrative metadata", e, this);
        }
        return amd;
    }

    private void validateXml(File amdFile) throws TaskCycleException, FatalTaskException {
        try {
            XMLErrorHandler handler = AdministrativeMetadataValidator.instance().validate(amdFile, AdministrativeMetadataImpl.VERSION);
            if (!handler.passed()) {
                RL.error(new Event(getTaskName(), "Invalid administrative metadata", handler.getMessages()));
                throw new TaskException("Invalid administrative metadata", this);
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
        File amdFile = new File(currentFile, relativePath);
        if (!amdFile.exists()) {
            String msg = "Not found " + amdFile.getPath();
            RL.error(new Event(getTaskName(), "No administrative metadata", msg));
            throw new TaskException(msg, this);
        }
        if (!amdFile.canRead()) {
            String msg = "Cannot read " + amdFile.getPath();
            RL.error(new Event(getTaskName(), "No access to administrative metadata", msg));
            throw new TaskException(msg, this);
        }
        return amdFile;
    }

}
