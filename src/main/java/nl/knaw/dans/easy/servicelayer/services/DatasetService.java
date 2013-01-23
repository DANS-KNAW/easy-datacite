package nl.knaw.dans.easy.servicelayer.services;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.CommonDataset;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;

import org.joda.time.DateTime;

/**
 * Service for {@link Dataset}s.
 * 
 * @author ecco
 */
public interface DatasetService extends EasyService
{

    //Method not used and no security with this signature possible
    CommonDataset getCommonDataset(DmoStoreId dmoStoreId) throws ServiceException;

    /**
     * Creates a new dataset object. Call this to create one.
     * 
     * @param mdFormat
     *        the format of the easy meatadata
     * @return a new dataset object
     * @throws ServiceException
     *         wrapper for exceptions
     */
    Dataset newDataset(ApplicationSpecific.MetadataFormat mdFormat) throws ServiceException;

    Dataset newDataset(EasyMetadata emd, AdministrativeMetadata amd) throws ServiceException;

    /**
     * Get the Dataset with the given id. If the parameter <code>storeId == null</code> a new (empty) Dataset will be
     * returned.
     * 
     * @param sessionUser
     *        the user that initiates this action, can be <code>null</code>
     * @param storeId
     *        the id of the Dataset, can be <code>null</code>
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @return the dataset with the given id
     * @throws ServiceException
     *         wrapper for exceptions
     */
    Dataset getDataset(EasyUser sessionUser, DmoStoreId dmoStoreId) throws ObjectNotAvailableException, CommonSecurityException, ServiceException;

    /**
     * Get the DataModelObject with the given id.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param storeId
     *        the id of the stored object
     * @return the DataModelObject with the given id
     * @throws ServiceException
     *         wrapper for exceptions
     */
    DataModelObject getDataModelObject(EasyUser sessionUser, DmoStoreId dmoStoreId) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    byte[] getObjectXml(EasyUser sessionUser, Dataset dataset) throws ObjectNotAvailableException, CommonSecurityException, ServiceException;

    boolean exists(DmoStoreId dmoStoreId) throws ServiceException;

    /**
     * Clone a dataset in such a way that it is fit for reuse.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset to clone
     * @return the cloned dataset
     * @throws ServiceException
     *         wrapper for exceptions
     */
    Dataset cloneDataset(final EasyUser sessionUser, final Dataset dataset) throws ServiceException;

    /**
     * Save the EasyMetadata unit of the dataset. Note there is a potential security hazard in the implementation,
     * because any component of the dataset that has changed will be saved.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset to save
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void saveEasyMetadata(EasyUser sessionUser, Dataset dataset, WorkListener... workListeners) throws ServiceException, DataIntegrityException;

    /**
     * Save the AdministrativeMetadata unit of the dataset. Note there is a potential security hazard in the
     * implementation, because any component of the dataset that has changed will be saved.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset to save
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void saveAdministrativeMetadata(EasyUser sessionUser, Dataset dataset, WorkListener... workListeners) throws ServiceException, DataIntegrityException;

    /**
     * Submit the dataset wrapped in <code>submission</code>.
     * 
     * @param submission
     *        transport vehicle for dataset and error messages
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void submitDataset(DatasetSubmission submission, WorkListener... workListeners) throws ServiceException, DataIntegrityException;

    /**
     * Change administrative state of given dataset to DRAFT.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to change
     * @param mustNotifyDepositor
     *        notify depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void unsubmitDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException;

    /**
     * Publish the given dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to publish
     * @param mustNotifyDepositor
     *        notify depositor
     * @param mustIncludeLicense
     *        include license in notification to depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void publishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor, boolean mustIncludeLicense) throws ServiceException,
            DataIntegrityException;

    /**
     * Change administrative state of given dataset to SUBMITTED.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to change
     * @param mustNotifyDepositor
     *        notify depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void unpublishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException;

    /**
     * Change administrative state of given dataset to MAINTANANCE.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to change
     * @param mustNotifyDepositor
     *        notify depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void maintainDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException;

    /**
     * Republish given dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to republish
     * @param mustNotifyDepositor
     *        notify depositor
     * @param mustIncludeLicense
     *        include license in notification to depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void republishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor, boolean mustIncludeLicense) throws ServiceException,
            DataIntegrityException;

    /**
     * Delete the given dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to delete
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void deleteDataset(EasyUser sessionUser, Dataset dataset) throws ServiceException;

    /**
     * Restore the given dataset to a previous state.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to delete
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void restoreDataset(EasyUser sessionUser, Dataset dataset) throws ServiceException, DataIntegrityException;

    /**
     * Change the depositor of the given dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        dataset to change
     * @param newDepositor
     *        the new depositor
     * @param mustNotifyDepositor
     *        inform old depositor
     * @param mustNotifyNewDepositor
     *        inform new depositor
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void changeDepositor(EasyUser sessionUser, Dataset dataset, EasyUser newDepositor, boolean mustNotifyDepositor, boolean mustNotifyNewDepositor)
            throws ServiceException, DataIntegrityException;

    /**
     * Save a request for permission. If the request is valid, an email is send to the depositor of the dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset for which permission is requested
     * @param requestModel
     *        wrapper for request contents
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @throws DataIntegrityException
     *         if the requestModel contains insufficient data
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void savePermissionRequest(EasyUser sessionUser, Dataset dataset, PermissionRequestModel requestModel, WorkListener... workListeners)
            throws ServiceException, DataIntegrityException;

    /**
     * Save a reply on a request for permission. If the reply is valid, an email is send to the requester of permission.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset for which permission was requested
     * @param replyModel
     *        wrapper for reply contents
     * @param workListeners
     *        listener(s) for events in the process, can be <code>null</code>
     * @throws DataIntegrityException
     *         if the replyModel contains insufficient data
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void savePermissionReply(EasyUser sessionUser, Dataset dataset, PermissionReplyModel replyModel, WorkListener... workListeners) throws ServiceException,
            DataIntegrityException;

    /**
     * Obtain a read-only instance of DownloadHistory for the given dataset and the period indicated by date.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset for which download history is to be obtained
     * @param date
     *        indicates the period of download history that is to be obtained
     * @return download history for the given dataset and period, or <code>null</code> if no history exists
     * @throws ServiceException
     *         wrapper for exceptions
     */
    DownloadHistory getDownloadHistoryFor(EasyUser sessionUser, Dataset dataset, DateTime date) throws ServiceException;

    List<UnitMetadata> getAdditionalLicenseVersions(final Dataset object) throws ServiceException;

    UnitMetadata getAdditionalLicense(Dataset dataset) throws ServiceException;

    List<UnitMetadata> getLicenseVersions(final Dataset object) throws ServiceException;

    void saveAdditionalLicense(EasyUser sessionUser, Dataset dataset, final WorkListener... workListeners) throws ServiceException, DataIntegrityException;

    void deleteAdditionalLicense(EasyUser sessionUser, DmoStoreId storeId, DsUnitId unitId, DateTime creationDate) throws ServiceException;

    URL getUnitMetadataURL(EasyUser sessionUser, Dataset dataset, UnitMetadata unitMetadata) throws ServiceException, CommonSecurityException;

    URL getAdditionalLicenseURL(Dataset dataset) throws ServiceException;

    //    /**
    //     * Obtain a list of read-only instances of DownloadHistory for the given dataset and time interval.
    //     * 
    //     * @param sessionUser
    //     *        the user that initiates this action
    //     * @param dataset
    //     *        the dataset for which download history is to be obtained
    //     * @param interval
    //     *        the time interval for which download history is to be obtained
    //     * @return a list of read-only instances of DownloadHistory
    //     * @throws ServiceException
    //     *         wrapper for exceptions
    //     */
    //    List<DownloadHistory> getDownloadHistoryFor(Dataset dataset, ReadableInterval interval)
    //            throws ServiceException;

}
