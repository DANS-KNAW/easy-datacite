package nl.knaw.dans.easy.business.dataset;

import java.net.URL;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.annotations.MutatesDataset;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.servicelayer.MaintenanceNotification;
import nl.knaw.dans.easy.servicelayer.NewDepositorNotification;
import nl.knaw.dans.easy.servicelayer.OldDepositorNotification;
import nl.knaw.dans.easy.servicelayer.PublishNotification;
import nl.knaw.dans.easy.servicelayer.RepublishNotification;
import nl.knaw.dans.easy.servicelayer.UnpublishNotification;
import nl.knaw.dans.easy.servicelayer.UnsubmitNotification;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.xml.exc.XMLException;

import org.joda.time.DateTime;

public class DatasetWorkDispatcher {

    public DataModelObject getDataModelObject(EasyUser sessionUser, DmoStoreId dmoStoreId) throws ServiceException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        return worker.getDataModelObject(dmoStoreId);
    }

    public byte[] getObjectXml(EasyUser sessionUser, Dataset dataset) throws ServiceException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        return worker.getObjectXml(dataset.getDmoStoreId());
    }

    public Dataset cloneDataset(EasyUser sessionUser, Dataset dataset) throws ServiceException {
        DatasetImpl clonedDataset = null;
        EasyMetadata emd = dataset.getEasyMetadata();
        try {
            EasyMetadata clonedEmd = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(new EmdMarshaller(emd).getXmlByteArray());
            clonedEmd.getEmdIdentifier().removeIdentifier(EmdConstants.SCHEME_PID);
            clonedEmd.getEmdIdentifier().removeIdentifier(EmdConstants.SCHEME_OAI_ITEM_ID);
            clonedEmd.getEmdIdentifier().removeIdentifier(EmdConstants.SCHEME_DMO_ID);
            clonedEmd.getEmdIdentifier().removeIdentifier(EmdConstants.SCHEME_AIP_ID);

            clonedEmd.getEmdRights().getTermsLicense().clear();
            clonedEmd.getEmdDate().getEasDateSubmitted().clear();

            // property lists contain (a sort of) audit trail on the cloned dataset. remove them.
            clonedEmd.getEmdOther().getPropertyListCollection().clear();

            // single property dc.creator cannot be converted to multiple properties of eas,creator.
            // remove dc.creator
            clonedEmd.getEmdCreator().getDcCreator().clear();
            clonedEmd.getEmdContributor().getDcContributor().clear();

            clonedDataset = (DatasetImpl) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
            clonedDataset.setEasyMetadata(clonedEmd);
            clonedDataset.getAdministrativeMetadata().setDepositor(sessionUser);
        }
        catch (XMLException e) {
            throw new ServiceException(e);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
        return clonedDataset;
    }

    @MutatesDataset
    public void saveEasyMetadata(EasyUser sessionUser, Dataset dataset, WorkListener... workListeners) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.addWorkListeners(workListeners);
        worker.workSave(dataset);
    }

    @MutatesDataset
    public void saveAdministrativeMetadata(EasyUser sessionUser, Dataset dataset, WorkListener... workListeners) throws ServiceException,
            DataIntegrityException
    {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.addWorkListeners(workListeners);
        worker.workSave(dataset);
    }

    @MutatesDataset
    public void submitDataset(EasyUser sessionUser, Dataset dataset, DatasetSubmission submission, WorkListener... workListeners) throws ServiceException,
            DataIntegrityException
    {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.addWorkListeners(workListeners);
        worker.workSubmit(submission);
    }

    @MutatesDataset
    public void unsubmitDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.storeInState(dataset, DatasetState.DRAFT);
        if (mustNotifyDepositor) {
            new UnsubmitNotification(dataset).sendMail();
        }
    }

    @MutatesDataset
    public void publishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor, boolean mustIncludeLicense) throws ServiceException,
            DataIntegrityException
    {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.publishDataset(dataset, mustIncludeLicense);
        if (mustNotifyDepositor) {
            new PublishNotification(dataset).sendMail(mustIncludeLicense);
        }
    }

    @MutatesDataset
    public void republishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor, boolean mustIncludeLicense) throws ServiceException,
            DataIntegrityException
    {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.republishDataset(dataset, mustIncludeLicense);
        if (mustNotifyDepositor) {
            new RepublishNotification(dataset).sendMail(mustIncludeLicense);
        }
    }

    @MutatesDataset
    public void unpublishDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.unPublishDataset(dataset);
        if (mustNotifyDepositor) {
            new UnpublishNotification(dataset).sendMail();
        }
    }

    @MutatesDataset
    public void maintainDataset(EasyUser sessionUser, Dataset dataset, boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.maintainDataset(dataset);
        if (mustNotifyDepositor) {
            new MaintenanceNotification(dataset).sendMail();
        }
    }

    @MutatesDataset
    public void deleteDataset(EasyUser sessionUser, Dataset dataset) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.deleteDataset(dataset);
    }

    @MutatesDataset
    public void restoreDataset(EasyUser sessionUser, Dataset dataset) throws ServiceException, DataIntegrityException {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        worker.restoreDataset(dataset);
    }

    @MutatesDataset
    public void changeDepositor(EasyUser sessionUser, Dataset dataset, EasyUser newDepositor, boolean mustNotifyDepositor, boolean mustNotifyNewDepositor)
            throws ServiceException, DataIntegrityException
    {
        DatasetWorker worker = new DatasetWorker(sessionUser);
        EasyUser oldDepositor = dataset.getDepositor();
        worker.changeDepositor(dataset, newDepositor);
        if (mustNotifyDepositor) {
            new OldDepositorNotification(dataset, oldDepositor, newDepositor).sendMail();
        }
        if (mustNotifyNewDepositor) {
            new NewDepositorNotification(dataset, oldDepositor, newDepositor).sendMail();
        }
    }

    @MutatesDataset
    public void savePermissionRequest(EasyUser sessionUser, Dataset dataset, PermissionRequestModel requestModel, WorkListener[] workListeners)
            throws ServiceException
    {
        PermissionWorker worker = new PermissionWorker(sessionUser);
        worker.addWorkListeners(workListeners);
        worker.saveRequest(dataset, sessionUser, requestModel);
    }

    @MutatesDataset
    public void savePermissionReply(EasyUser sessionUser, Dataset dataset, PermissionReplyModel replyModel, WorkListener[] workListeners)
            throws ServiceException
    {
        PermissionWorker worker = new PermissionWorker(sessionUser);
        worker.addWorkListeners(workListeners);
        worker.saveReply(dataset, sessionUser, replyModel);
    }

    public DownloadHistory getDownloadHistoryFor(EasyUser sessionUser, Dataset dataset, DateTime date) throws ServiceException {
        String period = DownloadList.printPeriod(DownloadHistory.LIST_TYPE_DATASET, date);
        DownloadHistory dlh = null;
        try {
            dlh = Data.getEasyStore().findDownloadHistoryFor(dataset, period);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
        return dlh;
    }

    public URL getUnitMetadataURL(EasyUser sessionUser, Dataset dataset, UnitMetadata unitMetadata) throws ServiceException {
        URL url = Data.getEasyStore().getFileURL(dataset.getDmoStoreId(), new DsUnitId(unitMetadata.getId()), unitMetadata.getCreationDate());
        return url;
    }

    /*
     * This method currentyl has not security! TODO: FIX IT. And then un-ignore nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcherTest.testSecurity()
     */
    public URL getAdditionalLicenseURL(EasyUser sessionUser, Dataset dataset, UnitMetadata unitMetadata) throws ServiceException {
        URL url = Data.getEasyStore().getFileURL(dataset.getDmoStoreId(), new DsUnitId(unitMetadata.getId()), unitMetadata.getCreationDate());
        return url;
    }

    public void deleteAdditionalLicense(EasyUser sessionUser, DmoStoreId storeId, DsUnitId unitId, DateTime creationDate, String logMessage)
            throws ServiceException
    {
        try {
            Data.getEasyStore().purgeUnit(storeId, unitId, creationDate, logMessage);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

}
