package nl.knaw.dans.easy.ebiu.task;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.md.amd.ReplaceAdditionalMetadataStrategy;
import nl.knaw.dans.easy.domain.worker.DefaultWorkListener;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

public class FileMetadataUpdater extends AbstractTask {

    private final AdditionalMetadataUpdateStrategy updateStrategy;

    /**
     * Default constructor injects a {@link ReplaceAdditionalMetadataStrategy} for updating the additional metadata.
     */
    public FileMetadataUpdater() {
        this(new ReplaceAdditionalMetadataStrategy());
    }

    /**
     * Constructor that injects the given {@link AdditionalMetadataUpdateStrategy}.
     * 
     * @param updateStrategy
     *        class that implements the update strategy
     */
    public FileMetadataUpdater(AdditionalMetadataUpdateStrategy updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        ResourceMetadataList rmdList = joint.getResourceMetadataList();
        if (rmdList != null) {
            updateFileItemMetadata(joint, rmdList);
        } else {
            RL.warn(new Event("Not found: resource metadata list."));
        }
    }

    protected void updateFileItemMetadata(JointMap joint, ResourceMetadataList rmdList) throws FatalTaskException {
        try {
            Services.getItemService().updateFileItemMetadata(joint.getEasyUser(), joint.getDataset(), rmdList, updateStrategy, new MyWorkListener());
            joint.setCycleProcessingCompleted(isFinalStepInCycle());
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private class MyWorkListener extends DefaultWorkListener {

        @Override
        public void afterUpdate(DataModelObject dmo) {
            RL.info(new Event("Updated object", dmo.getStoreId()));
        }

        @Override
        public void afterUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit) {
            RL.info(new Event("Updated unit", dmo.getStoreId(), mdUnit.getUnitId(), mdUnit.getUnitLabel()));
        }

        @Override
        public void onException(Throwable t) {
            RL.error(new Event("While updating metadata", t, t.getMessage()));
        }

        @Override
        public boolean onWorkStart() {
            RL.info(new Event("Start metadata update"));
            return false;
        }

        @Override
        public void onWorkEnd() {
            RL.info(new Event("End metadata update"));
        }

    }
}
