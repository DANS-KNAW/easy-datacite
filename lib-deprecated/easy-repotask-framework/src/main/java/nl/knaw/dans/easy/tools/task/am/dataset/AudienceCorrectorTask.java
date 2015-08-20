package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.easy.business.dataset.DatasetWorker;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.task.adhoc.Easy1;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.dom4j.DocumentException;

public class AudienceCorrectorTask extends AbstractDatasetTask {

    public static final String CUSTOM_DISCIPLINES = "custom.disciplines";
    public static final String DISCIPLINE_ID_ARCHAEOLOGY = "easy-discipline:2";

    public static final String XPATH_DEPOSITOR_ASSIGNED_AUDIENCES = "/EasyMetadata/dcterms:audience/value/optionValue";
    public static final String METADATA_DIR = "metadata";
    public static final String DC_SIMPLE = "dc-simple";
    public static final String DC_ARCH = "dc-arch";
    public static final String METADATA_FILE = "data.xml";

    public static final String XPATH_ARCHIVIST_ASSIGNED_CATEGORIES = "/mgmData/applicationData/payLoad/mgmDataWorkFlow/workflowContentsPane/assignedCategories/as_cat_option/assignedCategoryId";
    public static final String MGMDATA_DIR = "mgmdata";
    public static final String MGMDATA_FILE = "mgmdata.xml";

    private final File baseDir;

    private String currentStoreId;
    private Dataset currentDataset;

    /**
     * @param basePath
     *        path to easy1 data: i.e. /mnt/sara1022/aipstore/data/, known as 'aipstore.data.directory' in the application.properties.
     */
    public AudienceCorrectorTask(String basePath) {
        baseDir = new File(basePath);
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        if (hasTaskStamp(joint)) {
            return; // previously corrected by this task.
        }

        currentDataset = joint.getDataset();
        currentStoreId = currentDataset.getStoreId();

        String aipId = currentDataset.getEasyMetadata().getEmdIdentifier().getAipId();
        if (aipId == null) {
            RL.info(new Event(getTaskName(), "Not a migration dataset", currentStoreId));
            return;
        }

        joint.setCycleSubjectDirty(true);
        setTaskStamp(joint);

        clearAudienceAndRelations();

        List<String> archivistAssignedCategories = getArchivistAssignedCategories(aipId);
        List<DisciplineContainer> archivistAssignedDisciplines = disciplinesForBranchIds(archivistAssignedCategories);

        if (!archivistAssignedDisciplines.isEmpty()) {
            setArchivistAssignedDisciplines(archivistAssignedDisciplines);
        } else {
            List<String> depositorAssignedAudiences = getDepositorAssignedAudiences(aipId);
            List<DisciplineContainer> depositorAssignedDisciplines = disciplinesForOICodes(depositorAssignedAudiences);
            setDepositorAssignedDisciplines(depositorAssignedDisciplines);
        }

        checkDisciplineArchaeology();

        EmdAudience audienceContainer = currentDataset.getEasyMetadata().getEmdAudience();
        int count = audienceContainer.getDisciplines().size();
        RL.info(new Event(getTaskName(), "total disciplines", currentStoreId, getState(), "count=" + count));

        try {
            if (DatasetState.PUBLISHED.equals(currentDataset.getAdministrativeMetadata().getAdministrativeState())) {
                DatasetWorker.publishAsOAIItem(currentDataset);
            } else {
                DatasetWorker.unPublishAsOAIItem(currentDataset);
            }
        }
        catch (ServiceException e) {
            throw new TaskException(e, this);
        }
    }

    private void checkDisciplineArchaeology() {
        MetadataFormat metadataFormat = currentDataset.getEasyMetadata().getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        if (metadataFormat == null || MetadataFormat.UNSPECIFIED.equals(metadataFormat)) {
            RL.warn(new Event(getTaskName(), "Unknown metadata format", currentStoreId, metadataFormat == null ? "null" : metadataFormat.toString()));
        }

        if (MetadataFormat.ARCHAEOLOGY.equals(metadataFormat)) {
            EmdAudience audienceContainer = currentDataset.getEasyMetadata().getEmdAudience();
            if (!audienceContainer.containsDiscipline(DISCIPLINE_ID_ARCHAEOLOGY)) {
                BasicString bs = new BasicString(DISCIPLINE_ID_ARCHAEOLOGY);
                bs.setSchemeId(CUSTOM_DISCIPLINES);
                audienceContainer.getTermsAudience().add(bs);
                RL.info(new Event(getTaskName(), "Added archeology because metadata format is archeology", currentStoreId, getState()));
            }
        }

    }

    private void setDepositorAssignedDisciplines(List<DisciplineContainer> depositorAssignedDisciplines) {
        EmdAudience audienceContainer = currentDataset.getEasyMetadata().getEmdAudience();
        for (DisciplineContainer container : depositorAssignedDisciplines) {
            BasicString bs = new BasicString(container.getStoreId());
            bs.setSchemeId(CUSTOM_DISCIPLINES);
            audienceContainer.getTermsAudience().add(bs);
        }
        RL.info(new Event(getTaskName(), "depositor assigned", currentStoreId, getState(), "count=" + depositorAssignedDisciplines.size()));
    }

    private void setArchivistAssignedDisciplines(List<DisciplineContainer> archivistAssignedDisciplines) {
        // <dcterms:audience eas:schemeId="custom.disciplines">easy-discipline:8</dcterms:audience>
        EmdAudience audienceContainer = currentDataset.getEasyMetadata().getEmdAudience();
        for (DisciplineContainer container : archivistAssignedDisciplines) {
            BasicString bs = new BasicString(container.getStoreId());
            bs.setSchemeId(CUSTOM_DISCIPLINES);
            audienceContainer.getTermsAudience().add(bs);
        }
        RL.info(new Event(getTaskName(), "archivist assigned", currentStoreId, getState(), "count=" + archivistAssignedDisciplines.size()));
    }

    private String getState() {
        DatasetState state = currentDataset.getAdministrativeState();
        if (state == null) {
            RL.warn(new Event(getTaskName(), "No dataset state", currentStoreId));
            return "";
        } else {
            return state.toString();
        }
    }

    protected List<DisciplineContainer> disciplinesForBranchIds(List<String> branchIds) throws FatalTaskException {
        List<DisciplineContainer> disciplines = new ArrayList<DisciplineContainer>();
        try {
            for (String branchId : branchIds) {
                DisciplineContainer container = Easy1.getCatIdDisciplineMap().get(branchId);
                if (container == null) {
                    RL.warn(new Event(getTaskName(), "branchId not found", currentStoreId, getState(), "branchId=" + branchId));
                } else {
                    disciplines.add(container);
                }
            }
        }
        catch (FatalException e) {
            throw new FatalTaskException(e, this);
        }
        return disciplines;
    }

    private List<DisciplineContainer> disciplinesForOICodes(List<String> depositorAssignedAudiences) throws FatalTaskException {
        List<DisciplineContainer> disciplines = new ArrayList<DisciplineContainer>();
        try {
            for (String oiCode : depositorAssignedAudiences) {
                DisciplineContainer container = Easy1.getOIDisciplineMap().get(oiCode);
                if (container == null) {
                    RL.warn(new Event(getTaskName(), "oi-code not found", currentStoreId, "oi-code=" + oiCode));
                } else {
                    disciplines.add(container);
                }
            }
        }
        catch (FatalException e) {
            throw new FatalTaskException(e, this);
        }
        return disciplines;
    }

    protected void clearAudienceAndRelations() {
        // remove audiences
        currentDataset.getEasyMetadata().getEmdAudience().removeAllDisciplines();

        // remove OAI-membership relations
        DatasetRelations relations = (DatasetRelations) currentDataset.getRelations();
        relations.removeOAISetMembership();

        // nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations.setParents(Collection<String>)
        // removes all RelsConstants.DANS_NS.IS_MEMBER_OF before setting the new collection.
    }

    private List<String> getArchivistAssignedCategories(String aipId) throws TaskException {
        List<String> assignedCategories;
        File datasetDir = getAipDirectory(aipId);

        File mgmDataDir = new File(datasetDir, MGMDATA_DIR);
        if (!mgmDataDir.exists()) {
            RL.error(new Event(getTaskName(), "mgmdata directory not found", currentStoreId, mgmDataDir.getPath()));
            throw new TaskException("mgmdata directory not found: " + mgmDataDir.getPath(), this);
        }

        File mgmdataFile = new File(mgmDataDir, MGMDATA_FILE);
        if (!mgmdataFile.exists()) {
            RL.error(new Event(getTaskName(), "mgmdata file not found", currentStoreId, mgmdataFile.getPath()));
            throw new TaskException("mgmdata file not found: " + mgmdataFile.getPath(), this);
        }

        try {
            Dom4jReader reader = new Dom4jReader(mgmdataFile);

            // the values under assignedCategories correspond to the Easy1BranchID of DisciplineMetadata
            // these categories were set by the archivist.
            assignedCategories = reader.getList(XPATH_ARCHIVIST_ASSIGNED_CATEGORIES);
        }
        catch (DocumentException e) {
            RL.error(new Event(getTaskName(), e, "Cannot read mgmdata", currentStoreId, aipId));
            throw new TaskException(e, this);
        }

        return assignedCategories;
    }

    protected File getAipDirectory(String aipId) throws TaskException {
        File datasetDir = new File(baseDir, aipId);
        if (!datasetDir.exists()) {
            RL.error(new Event(getTaskName(), "Twips directory not found", currentStoreId, datasetDir.getPath()));
            throw new TaskException("Twips directory not found: " + datasetDir.getPath(), this);
        }
        return datasetDir;
    }

    private List<String> getDepositorAssignedAudiences(String aipId) throws TaskException {
        List<String> audiences;
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

        try {
            Dom4jReader reader = new Dom4jReader(metadataFile);
            audiences = reader.getList(XPATH_DEPOSITOR_ASSIGNED_AUDIENCES);
        }
        catch (DocumentException e) {
            RL.error(new Event(getTaskName(), e, "Cannot read metadatadata", currentStoreId, aipId));
            throw new TaskException(e, this);
        }

        return audiences;
    }

}
