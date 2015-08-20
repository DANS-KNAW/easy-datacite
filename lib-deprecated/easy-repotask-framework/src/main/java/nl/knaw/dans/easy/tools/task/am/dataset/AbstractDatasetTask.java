package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.PropertyList;

import org.joda.time.DateTime;

public abstract class AbstractDatasetTask extends AbstractTask {

    protected void setTaskStamp(JointMap joint) {
        EasyMetadata emd = joint.getDataset().getEasyMetadata();
        List<PropertyList> propertyLists = emd.getEmdOther().getPropertyListCollection();
        PropertyList propertyList;
        if (propertyLists == null || propertyLists.isEmpty()) {
            RL.warn(new Event(getTaskName(), "Trying to set a task stamp on a dataset that was not part of migration", joint.getDataset().getStoreId()));
            return;
        } else {
            propertyList = propertyLists.get(0);
        }
        propertyList.addProperty(this.getClass().getName(), new DateTime().toString());
    }

    protected boolean hasTaskStamp(JointMap joint) {
        boolean hasTaskStamp = false;
        EasyMetadata emd = joint.getDataset().getEasyMetadata();
        List<PropertyList> propertyLists = emd.getEmdOther().getPropertyListCollection();
        if (propertyLists != null && !propertyLists.isEmpty()) {
            hasTaskStamp = propertyLists.get(0).getValue(this.getClass().getName(), null) != null;
        }
        return hasTaskStamp;
    }

    protected void abbortIfNotMigration(JointMap joint) throws TaskCycleException {
        Dataset dataset = joint.getDataset();
        String aipId = dataset.getEasyMetadata().getEmdIdentifier().getAipId();
        String storeId = dataset.getStoreId();
        if (aipId == null) {
            RL.warn(new Event(getTaskName(), "Not a migration dataset", storeId, "datasetState=" + dataset.getAdministrativeState(), "depositor="
                    + dataset.getAdministrativeMetadata().getDepositorId()));
            throw new TaskCycleException("Not a migration dataset " + storeId, this);
        }
    }

}
