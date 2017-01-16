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
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import java.util.List;

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

        boolean datasetShouldBeAssignedWithFormatCheck = shouldBeAssignedToCollectionWithFormatCheck(dataset);
        boolean datasetShouldBeAssigned = shouldBeAssignedToCollection(dataset);
        boolean datasetIsPublished = (DatasetState.PUBLISHED.equals(dataset.getAdministrativeState()));
        boolean datasetIsCollectionMember = dataset.getRelations().isCollectionMember(targetCollectionId);
        boolean datasetIsOAISetMember = dataset.getRelations().isOAISetMember(targetCollectionId);
        String rule;
        String condition = new StringBuilder().append(datasetShouldBeAssigned ? "1" : "0").append(datasetIsPublished ? "1" : "0")
                .append(datasetIsCollectionMember ? "1" : "0").append(datasetIsOAISetMember ? "1" : "0").append(collectionIsOAISet ? "1" : "0").toString();

        if (datasetShouldBeAssigned != datasetShouldBeAssignedWithFormatCheck) {
            rule = "rule 1:" + condition;
            RL.warn(new Event(getTaskName(), "CONDITION 'should be assigned' CHANGED: new=" + datasetShouldBeAssigned + " old="
                    + datasetShouldBeAssignedWithFormatCheck));
        }

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

    protected abstract boolean shouldBeAssignedToCollectionWithFormatCheck(Dataset dataset);

    protected abstract boolean shouldBeAssignedToCollection(Dataset dataset);

    final boolean hasArchaeologyAsAudience(Dataset dataset) {
        final String ARCHAEOLOGY_DISCIPLINE_ID = "easy-discipline:2";
        EmdAudience emdAudience = dataset.getEasyMetadata().getEmdAudience();

        List<BasicString> disciplines = emdAudience.getDisciplines();
        for (BasicString discipline : disciplines) {
            String value = discipline.getValue();
            if (value.contentEquals(ARCHAEOLOGY_DISCIPLINE_ID))
                return true; // found so done
        }
        // not found

        return false;
    }

    final boolean hasArchaeologyAsMetadataFormat(Dataset dataset) {
        EasyMetadata emd = dataset.getEasyMetadata();
        ApplicationSpecific.MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();

        return (ApplicationSpecific.MetadataFormat.ARCHAEOLOGY.equals(mdFormat));
    }

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
