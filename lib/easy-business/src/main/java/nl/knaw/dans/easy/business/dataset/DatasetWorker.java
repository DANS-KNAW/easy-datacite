package nl.knaw.dans.easy.business.dataset;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore.RepositoryState;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.dataset.DatasetSpecification;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;
import nl.knaw.dans.easy.servicelayer.LicenseCreatorWrapper;
import nl.knaw.dans.easy.servicelayer.LicenseCreatorWrapper.LicenseCreatorWrapperException;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetWorker extends AbstractWorker {

    private static final Logger logger = LoggerFactory.getLogger(DatasetWorker.class);

    public static final DmoStoreId CLARIN_COLLECTION_STORE_ID = DmoStoreId.newDmoStoreId("easy-collection:5");
    private static final BasicString CMDI_MIME = new BasicString("application/x-cmdi+xml");

    protected DatasetWorker(EasyUser sessionUser) {
        super(sessionUser);
    }

    protected DatasetWorker(UnitOfWork uow) {
        super(uow);
    }

    protected DataModelObject getDataModelObject(DmoStoreId dmoStoreId) throws ServiceException {
        try {
            return Data.getEasyStore().retrieve(dmoStoreId);
        }
        catch (ObjectNotInStoreException e) {
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    protected byte[] getObjectXml(DmoStoreId dmoStoreId) throws ServiceException {
        try {
            return Data.getEasyStore().getObjectXML(dmoStoreId);
        }
        catch (ObjectNotInStoreException e) {
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    protected void workSave(Dataset dataset) throws ServiceException, DataIntegrityException {
        DatasetSpecification.evaluate(dataset);
        DatasetState previousState = dataset.getAdministrativeState();
        try {
            getUnitOfWork().attach(dataset);
            getUnitOfWork().commit();
        }
        catch (UnitOfWorkInterruptException e) {
            dataset.getAdministrativeMetadata().setAdministrativeState(previousState);
            // rollBack(e.getMessage());
            throw new UnsupportedOperationException("Rollback not implemented");
        }
        catch (RepositoryException e) {
            dataset.getAdministrativeMetadata().setAdministrativeState(previousState);
            throw new ServiceException(e);
        }
        finally {
            getUnitOfWork().close();
        }
    }

    protected void workSubmit(DatasetSubmission submission) throws ServiceException, DataIntegrityException {
        DatasetSpecification.evaluate(submission.getDataset());
        SubmissionDispatcher dispatcher = SubmissionDispatcherFactory.newSubmissionDispatcher();
        dispatcher.process((DatasetSubmissionImpl) submission);
    }

    protected void publishDataset(Dataset dataset, boolean mustIncludeLicense) throws ServiceException, DataIntegrityException {
        publish(dataset, mustIncludeLicense);
    }

    protected void unPublishDataset(Dataset dataset) throws ServiceException, DataIntegrityException {
        unPublishAsOAIItem(dataset);
        dataset.getRelations().removeDAIRelations();
        storeInState(dataset, DatasetState.SUBMITTED);
    }

    protected void maintainDataset(Dataset dataset) throws ServiceException, DataIntegrityException {
        unPublishAsOAIItem(dataset);
        dataset.getRelations().removeDAIRelations();
        storeInState(dataset, DatasetState.MAINTENANCE);
    }

    protected void republishDataset(Dataset dataset, boolean mustIncludeLicense) throws ServiceException, DataIntegrityException {
        publish(dataset, mustIncludeLicense);
    }

    private void publish(Dataset dataset, boolean mustIncludeLicense) throws ServiceException, DataIntegrityException {
        if (mustIncludeLicense) {
            DatasetWorker.createLicense(dataset);
        }

        addClarinCollection(dataset);
        publishAsOAIItem(dataset);
        dataset.getRelations().addDAIRelations();
        storeInState(dataset, DatasetState.PUBLISHED);

        // prevent repeated update attempt if followed by unpublish or whatever in the same session
        dataset.setLicenseContent(null);
    }

    protected void deleteDataset(Dataset dataset) throws ServiceException, DataIntegrityException {
        unPublishAsOAIItem(dataset);
        dataset.setState(RepositoryState.Deleted.code);
        storeInState(dataset, DatasetState.DELETED);
    }

    protected void restoreDataset(Dataset dataset) throws ServiceException, DataIntegrityException {
        DatasetState changeTo = DatasetState.DRAFT;
        if (DatasetState.isPassedSubmission(dataset.getAdministrativeMetadata().getPreviousAdministrativeState())) {
            changeTo = DatasetState.SUBMITTED;
        }
        dataset.setState(RepositoryState.Inactive.code);
        storeInState(dataset, changeTo);
    }

    private void addClarinCollection(Dataset dataset) {
        DatasetRelations relations = dataset.getRelations();
        boolean hasCmdi = dataset.getEasyMetadata().getEmdFormat().getDcFormat().contains(CMDI_MIME);
        if (hasCmdi) {
            if (!relations.isCollectionMember(CLARIN_COLLECTION_STORE_ID)) {
                relations.addCollectionMembership(CLARIN_COLLECTION_STORE_ID);
            }
        } else {
            if (relations.isCollectionMember(CLARIN_COLLECTION_STORE_ID)) {
                relations.removeCollectionMembership(CLARIN_COLLECTION_STORE_ID);
            }
        }
    }

    public static void publishAsOAIItem(Dataset dataset) throws ServiceException {
        DatasetRelations relations = (DatasetRelations) dataset.getRelations();
        relations.addOAIIdentifier();

        try {
            // discipline sets
            for (DisciplineContainer dc : dataset.getLeafDisciplines()) {
                relations.addOAISetMembership(dc.getDmoStoreId());
            }

            // driver set
            if (AccessCategory.isOpen(dataset.getAccessCategory()) && !dataset.isUnderEmbargo()) {
                relations.addOAISetMembership(Constants.OAI_DRIVER_SET_DMO_ID);
            }

            // dmoCollections
            Iterator<ECollection> iter = ECollection.iterator();
            while (iter.hasNext()) {
                ECollection eColl = iter.next();
                Set<DmoStoreId> memberIds = relations.getCollectionMemberships(eColl.namespace);
                Set<DmoStoreId> oaiEndNodes = Data.getCollectionAccess().filterOAIEndNodes(memberIds);
                for (DmoStoreId collectionId : oaiEndNodes) {
                    relations.addOAISetMembership(collectionId);
                }
            }
        }
        catch (ObjectNotFoundException e) {
            throw new ServiceException(e);
        }
        catch (DomainException e) {
            throw new ServiceException(e);
        }
        catch (CollectionsException e) {
            throw new ServiceException(e);
        }

        dataset.setState(RepositoryState.Active.code);
    }

    public static void unPublishAsOAIItem(Dataset dataset) throws ServiceException {
        DatasetRelations relations = (DatasetRelations) dataset.getRelations();
        // do not remove OAIIdentifier.
        relations.removeOAISetMembership();
        dataset.setState(RepositoryState.Inactive.code);
    }

    protected void storeInState(Dataset dataset, DatasetState newState) throws ServiceException, DataIntegrityException {
        DatasetSpecification.evaluate(dataset);
        DatasetState previousState = dataset.getAdministrativeState();
        try {
            getUnitOfWork().attach(dataset);
            dataset.getAdministrativeMetadata().setAdministrativeState(newState);
            getUnitOfWork().commit();
        }
        catch (UnitOfWorkInterruptException e) {
            // rollBack(e.getMessage());
            throw new UnsupportedOperationException("Rollback not implemented");
        }
        catch (ObjectNotInStoreException e) {
            logger.error("Could not retreive dataset: ", e);
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e) {
            dataset.getAdministrativeMetadata().setAdministrativeState(previousState);
            logger.error("Could not change status of dataset: ", e);
            throw new ServiceException(e);
        }
        finally {
            getUnitOfWork().close();
        }
    }

    protected void changeDepositor(Dataset dataset, EasyUser newDepositor) throws ServiceException, DataIntegrityException {
        DatasetSpecification.evaluate(dataset);
        String previousDepositorId = dataset.getAdministrativeMetadata().getDepositorId();
        try {
            getUnitOfWork().attach(dataset);
            dataset.getAdministrativeMetadata().setDepositor(newDepositor);
            getUnitOfWork().commit();
        }
        catch (UnitOfWorkInterruptException e) {
            // rollBack(e.getMessage());
            throw new UnsupportedOperationException("Rollback not implemented");
        }
        catch (RepositoryException e) {
            dataset.getAdministrativeMetadata().setDepositorId(previousDepositorId);
            logger.error("Could not change depositor of dataset: ", e);
            throw new ServiceException(e);
        }
        finally {
            getUnitOfWork().close();
        }
    }

    static boolean createLicense(final Dataset dataset) throws ServiceException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(LicenseCreatorWrapper.ESTIMATED_PDF_SIZE);
        try {
            new LicenseCreatorWrapper(dataset.getDepositor(), dataset, false).createPdf(outputStream);
        }
        catch (final LicenseCreatorWrapperException exception) {
            throw new ServiceException(exception.getMessage(), exception);
        }

        dataset.setLicenseContent(outputStream.toByteArray());
        return true;
    }
}
