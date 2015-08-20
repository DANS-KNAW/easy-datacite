package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.File;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdRights;

import org.dom4j.DocumentException;

public class EmdCorrectorTask extends AbstractDatasetTask {

    public static final String XPATH_LICENSE = "EasyMetadata/licenseAccepted/value/optionValue/text()";
    public static final String METADATA_DIR = "metadata";
    public static final String DC_SIMPLE = "dc-simple";
    public static final String DC_ARCH = "dc-arch";
    public static final String METADATA_FILE = "data.xml";

    private final File baseDir;
    private String currentStoreId;
    private Dataset currentDataset;

    /**
     * @param basePath
     *        path to easy1 data: i.e. /mnt/sara1022/aipstore/data/, known as 'aipstore.data.directory' in the application.properties.
     */
    public EmdCorrectorTask(String basePath) {
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

        EasyMetadata emd = currentDataset.getEasyMetadata();
        EmdRights emdR = emd.getEmdRights();
        if (!emdR.hasAcceptedLicense()) {
            String aipId = getAipId(currentDataset.getStoreId());
            boolean hasAccepted = containsLicenseAccepted(aipId);
            if (hasAccepted) {
                emdR.setAcceptedLicense(true, EmdRights.SCHEME_LICENSE_ACCEPT_E1V1);
                joint.setCycleSubjectDirty(true);
                setTaskStamp(joint);
                RL.info(new Event(getTaskName(), "Corrected licenseAccepted", currentStoreId, aipId, getState()));
            } else {
                RL.info(new Event(getTaskName(), "No accept license found", currentStoreId, aipId, getState(), getAccessCategoryString()));
            }
        }
    }

    private String getAccessCategoryString() {
        AccessCategory ac = currentDataset.getAccessCategory();
        return ac == null ? "No AccessCategory" : ac.toString();
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

    private String getAipId(String storeId) throws FatalTaskException {
        String aipId;
        try {
            IdMap idMap = Data.getMigrationRepo().findById(storeId);
            aipId = idMap.getAipId();
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event(getTaskName(), e, "Not in migrationRepo", storeId));
            throw new TaskException("Not in migrationRepo: " + storeId, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        return aipId;
    }

    private boolean containsLicenseAccepted(String aipId) throws TaskException {
        boolean licenseAccepted = false;
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
            licenseAccepted = reader.getList(XPATH_LICENSE).contains("accepted");
        }
        catch (DocumentException e) {
            RL.error(new Event(getTaskName(), e, "Cannot read metadatadata", currentStoreId, aipId));
            throw new TaskException(e, this);
        }

        return licenseAccepted;
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
