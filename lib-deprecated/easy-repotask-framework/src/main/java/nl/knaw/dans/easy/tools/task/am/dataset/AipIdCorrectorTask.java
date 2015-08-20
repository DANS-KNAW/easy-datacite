package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.PropertyList;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

public class AipIdCorrectorTask extends AbstractDatasetTask {

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        Dataset dataset = joint.getDataset();

        String storeId = dataset.getStoreId();
        EasyMetadata emd = dataset.getEasyMetadata();
        String aipIdemd = emd.getEmdIdentifier().getAipId();

        String previousCollectionId = null;
        String conversionDate = null;
        String comment = null;
        List<PropertyList> propertyLists = emd.getEmdOther().getPropertyListCollection();
        if (propertyLists == null || propertyLists.isEmpty()) {
            RL.info(new Event(getTaskName(), "No propertyList", storeId, "datasetState=" + dataset.getAdministrativeState(), "depositor="
                    + dataset.getAdministrativeMetadata().getDepositorId(), "aipId in emd", aipIdemd));
        } else {
            PropertyList propertyList = propertyLists.get(0);
            comment = propertyList.getComment();
            previousCollectionId = propertyList.getValue("previous.collection-id", null);
            conversionDate = propertyList.getValue("conversion.date", null);
        }

        String aipId;
        try {
            IdMap idMap = Data.getMigrationRepo().findById(storeId);
            aipId = idMap.getAipId();
        }
        catch (ObjectNotInStoreException e) {
            RL.info(new Event(getTaskName(), "Not in migrationRepo", storeId, "datasetState=" + dataset.getAdministrativeState(), "depositor="
                    + dataset.getAdministrativeMetadata().getDepositorId(), "aipId in emd", aipIdemd, "previous.collection-id", previousCollectionId,
                    "conversion.date", conversionDate, "comment", comment));
            joint.setFitForSave(false);
            //
            // this is not a dataset from migration.

            // throw new FatalTaskCycleException("Not in migrationRepo", e, this);

            // but we don't want to make too much fuzz about it.
            return;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        if (aipIdemd == null) {
            RL.info(new Event(getTaskName(), "No aipId in metadata", storeId));
            updateMetadata(joint, aipId);
        } else if (!aipIdemd.equals(aipId)) {
            RL.info(new Event(getTaskName(), "Unequal aipId", storeId, "emd=" + aipIdemd, "disc=" + aipId));
            emd.getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_AIP_ID);
            updateMetadata(joint, aipId);
        }
    }

    private void updateMetadata(JointMap joint, String aipId) throws FatalTaskException {
        Dataset dataset = joint.getDataset();
        EasyMetadata emd = dataset.getEasyMetadata();

        joint.setCycleSubjectDirty(true);
        setTaskStamp(joint);

        BasicIdentifier bi = new BasicIdentifier(aipId);
        bi.setScheme(EmdConstants.SCHEME_AIP_ID);
        emd.getEmdIdentifier().add(bi);
    }

}
