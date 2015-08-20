package nl.knaw.dans.easy.task;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.task.am.dataset.AbstractDatasetTask;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

public abstract class AbstractCollectionAssignmentTask extends AbstractDatasetTask {

    protected final boolean inTestMode;
    private DmoCollection targetCollection;
    private DmoStoreId targetCollectionId;
    private boolean collectionIsOAISet;

    public AbstractCollectionAssignmentTask() {
        this(false);
    }

    AbstractCollectionAssignmentTask(boolean inTestMode) {
        this.inTestMode = inTestMode;
        RL.info(new Event(RL.GLOBAL, (inTestMode ? "Running in - TEST - mode!" : "Running 4REAL")));
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        initTargetCollection();
        Dataset dataset = joint.getDataset();

        boolean datasetShouldBeAssigned = shouldBeAssignedToCollection(dataset);
        boolean datasetIsPublished = (DatasetState.PUBLISHED.equals(dataset.getAdministrativeState()));
        boolean datasetIsCollectionMember = dataset.getRelations().isCollectionMember(targetCollectionId);
        boolean datasetIsOAISetMember = dataset.getRelations().isOAISetMember(targetCollectionId);
        String rule;
        String condition = new StringBuilder().append(datasetShouldBeAssigned ? "1" : "0").append(datasetIsPublished ? "1" : "0")
                .append(datasetIsCollectionMember ? "1" : "0").append(datasetIsOAISetMember ? "1" : "0").append(collectionIsOAISet ? "1" : "0").toString();

        if (datasetShouldBeAssigned && !datasetIsCollectionMember) {
            rule = "rule 1:" + condition;
            if (inTestMode) {
                RL.info(new Event(getTaskName(), "TEST", rule, "assign collectionMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            } else {
                dataset.getRelations().addCollectionMembership(targetCollectionId);
                joint.setCycleSubjectDirty(true);
                RL.info(new Event(getTaskName(), "4REAL", rule, "assign collectionMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            }
        }

        if (!datasetShouldBeAssigned && datasetIsCollectionMember) {
            rule = "rule 2:" + condition;
            if (inTestMode) {
                RL.info(new Event(getTaskName(), "TEST", rule, "revert collectionMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            } else {
                dataset.getRelations().removeCollectionMembership(targetCollectionId);
                joint.setCycleSubjectDirty(true);
                RL.info(new Event(getTaskName(), "4REAL", rule, "revert collectionMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            }
        }

        if (collectionIsOAISet && datasetShouldBeAssigned && datasetIsPublished && !datasetIsOAISetMember) {
            rule = "rule 3:" + condition;
            if (inTestMode) {
                RL.info(new Event(getTaskName(), "TEST", rule, "assign OAISetMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            } else {
                dataset.getRelations().addOAISetMembership(targetCollectionId);
                joint.setCycleSubjectDirty(true);
                RL.info(new Event(getTaskName(), "4REAL", rule, "assign OAISetMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            }
        }

        if ((!datasetShouldBeAssigned || !datasetIsPublished || !collectionIsOAISet) && datasetIsOAISetMember) {
            rule = "rule 4:" + condition;
            if (inTestMode) {
                RL.info(new Event(getTaskName(), "TEST", rule, "revert OAISetMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            } else {
                dataset.getRelations().removeOAISetMembership(targetCollectionId);
                joint.setCycleSubjectDirty(true);
                RL.info(new Event(getTaskName(), "4REAL", rule, "revert OAISetMembership", targetCollectionId.getStoreId(), dataset.getStoreId(), dataset
                        .getLabel()));
            }
        }

    }

    public abstract DmoStoreId getCollectionStoreId();

    protected abstract boolean shouldBeAssignedToCollection(Dataset dataset);

    private void initTargetCollection() throws FatalTaskException {
        if (targetCollection == null) {
            targetCollectionId = getCollectionStoreId();
            try {
                targetCollection = Services.getCollectionService().getCollection(targetCollectionId);
            }
            catch (ServiceException e) {
                throw new FatalTaskException("Unable to retrieve dmoCollection " + targetCollectionId, e, this);
            }
            collectionIsOAISet = targetCollection.isPublishedAsOAISet();
        }
    }
}
