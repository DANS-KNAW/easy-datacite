package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

public class GroupCorrectorTask extends AbstractDatasetTask {

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        if (hasTaskStamp(joint)) {
            return; // already did this one
        }

        Dataset dataset = joint.getDataset();
        String aipId = dataset.getEasyMetadata().getEmdIdentifier().getAipId();
        if (aipId == null) {
            RL.info(new Event(getTaskName(), "Not a migration dataset", dataset.getStoreId()));
            return;
        }

        EasyMetadata emd = dataset.getEasyMetadata();
        MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        if (MetadataFormat.ARCHAEOLOGY.equals(mdFormat) && !dataset.getAdministrativeMetadata().getGroupIds().contains(Group.ID_ARCHEOLOGY)) {
            joint.setCycleSubjectDirty(true);
            setTaskStamp(joint);
            dataset.getAdministrativeMetadata().addGroupId(Group.ID_ARCHEOLOGY);
            RL.info(new Event(getTaskName(), "Added group ARCHEOLOGY", dataset.getStoreId()));
        }

    }

}
