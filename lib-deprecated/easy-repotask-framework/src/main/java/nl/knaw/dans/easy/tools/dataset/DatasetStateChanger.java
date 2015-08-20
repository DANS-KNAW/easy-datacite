package nl.knaw.dans.easy.tools.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.util.RepoUtil;

/**
 * Change the state of datasets.
 */
public class DatasetStateChanger {
    private static final Logger logger = LoggerFactory.getLogger(DatasetStateChanger.class);

    private final DatasetStateChangerListener listener;
    private final EasyUser user;

    public DatasetStateChanger(EasyUser user) throws NoListenerException {
        this(user, new DefaultDatasetStateChangerListener());
    }

    public DatasetStateChanger(EasyUser user, DatasetStateChangerListener listener) throws NoListenerException {
        this.user = user;
        this.listener = listener;
        RepoUtil.checkListenersActive();
    }

    public void changeState(String storeId, DatasetState newState) throws RepositoryException, ServiceException, DataIntegrityException {
        try {
            Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            changeState(dataset, newState);
        }
        catch (ObjectNotInStoreException e) {
            logger.info("A dataset with storeId " + storeId + " was not found");
            listener.onDatasetNotFound(storeId);
        }
    }

    public void changeState(Dataset dataset, DatasetState newState) throws ServiceException, DataIntegrityException {
        DatasetState oldState = dataset.getAdministrativeState();

        String oldDobState = dataset.getState();

        switch (newState.ordinal()) {
        case 0: // DRAFT
            Services.getDatasetService().unsubmitDataset(user, dataset, false);
            break;
        case 1: // SUBMITTED
            throw new ServiceException("Change to state " + newState + " not supported");

        case 2: // PUBLISHED
            Services.getDatasetService().publishDataset(user, dataset, false, false);
            break;
        case 3: // MAINTENANCE
            Services.getDatasetService().maintainDataset(user, dataset, false);
            break;
        case 4: // DELETED
            Services.getDatasetService().deleteDataset(user, dataset);
            break;
        default:
            throw new ServiceException("Unknown dataset state: " + newState);
        }

        String newDobState = dataset.getState();
        listener.onDatasetStateChanged(dataset, oldState, newState, oldDobState, newDobState);

    }

}
