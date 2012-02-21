package nl.knaw.dans.easy.business.services;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.CommonDataset;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.AdditionalLicenseUnit;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.dataset.LicenseUnit;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollection;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.security.authz.AuthzStrategyProvider;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.util.MemoryLane;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for handling datasets.
 * 
 * @author ecco
 */
public class EasyDatasetService extends AbstractEasyService implements DatasetService
{

    /**
     * Logger for logging.
     */
    private static final Logger   LOGGER = LoggerFactory.getLogger(EasyDatasetService.class);

    private DatasetWorkDispatcher datasetWorkDispatcher;

    private final DisciplineCollection  disciplineCollection;

    public EasyDatasetService()
    {
        this(DisciplineCollectionImpl.getInstance());
    }

    // used for unit testing
    protected EasyDatasetService(final DisciplineCollection disciplineCollection)
    {
        this.disciplineCollection = disciplineCollection;
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException
    {
        MemoryLane.printMemory("at startup");
    }

    /**
     * Get a short description of this service.
     * 
     * @return a short description of this service
     */
    @Override
    public String getServiceDescription()
    {
        return "Service for handling datasets.";
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ServiceException
     */
    public Dataset newDataset(final MetadataFormat mdFormat) throws ServiceException
    {
        try
        {
            final Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
            dataset.getEasyMetadata().getEmdOther().getEasApplicationSpecific().setMetadataFormat(mdFormat);
            prepareDataset(dataset);

            return dataset;
        }
        catch (final RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public Dataset newDataset(EasyMetadata emd, AdministrativeMetadata amd) throws ServiceException
    {
        DatasetImpl dataset;
        try
        {
            dataset = (DatasetImpl) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
            dataset.setEasyMetadata(emd);
            dataset.setAdministrativeMetadata(amd);
            prepareDataset(dataset);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        
        return dataset;
    }

    private void prepareDataset(final Dataset dataset)
    {
        final EasyMetadata easyMetadata = dataset.getEasyMetadata();
        MetadataFormat mdFormat = easyMetadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        easyMetadata.getEmdIdentifier().setDatasetId(dataset.getStoreId());

        // set standard settings for ARCHAEOLOGY metadata
        if (mdFormat.equals(MetadataFormat.ARCHAEOLOGY))
        {
            DisciplineContainer arch;
            try
            {
                arch = disciplineCollection.getDisciplineByName(MetadataFormat.ARCHAEOLOGY.name());
            }
            catch (final ObjectNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (final DomainException e)
            {
                throw new ApplicationException(e);
            }

            final BasicString archAudience = new BasicString();
            archAudience.setSchemeId(ChoiceListGetter.CHOICELIST_CUSTOM_PREFIX
                    + ChoiceListGetter.CHOICELIST_DISCIPLINES_POSTFIX);
            archAudience.setValue(arch.getStoreId());
            easyMetadata.getEmdAudience().getTermsAudience().add(archAudience);
            
            easyMetadata.getEmdRights().setAccessCategory(AccessCategory.OPEN_ACCESS, EmdConstants.SCHEME_ID_ARCHAEOLOGY_ACCESSRIGHTS);
        }
        else // set default metadata settings
        {
            easyMetadata.getEmdRights().setAccessCategory(AccessCategory.OPEN_ACCESS);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Dataset getDataset(final EasyUser sessionUser, final DmoStoreId dmoStoreId) throws ServiceException
    {
        Dataset dataset = (Dataset) getDataModelObject(sessionUser, dmoStoreId);
        String name = dataset.getAutzStrategyName();
        AuthzStrategy strategy = AuthzStrategyProvider.newAuthzStrategy(name, sessionUser, dataset, dataset);
        dataset.setAuthzStrategy(strategy);
        return dataset;
    }

    /**
     * {@inheritDoc}
     */
    public DataModelObject getDataModelObject(final EasyUser sessionUser, final DmoStoreId dmoStoreId) throws ServiceException
    {
        final DataModelObject dmo = getDatasetWorkDispatcher().getDataModelObject(sessionUser, dmoStoreId);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' retrieved object " + getStoreId(dmo));
        }
        return dmo;
    }
    
    @Override
    public byte[] getObjectXml(EasyUser sessionUser, Dataset dataset) throws ObjectNotAvailableException, CommonSecurityException, ServiceException
    {
        byte[] objectXml = getDatasetWorkDispatcher().getObjectXml(sessionUser, dataset);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' retrieved object xml " + getStoreId(dataset));
        }
        return objectXml;
    }
    
    public boolean exists(DmoStoreId storeId) throws ServiceException
    {
        try
        {
            return Data.getEasyStore().exists(storeId);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Dataset cloneDataset(final EasyUser sessionUser, final Dataset dataset) throws ServiceException
    {
        final Dataset clonedDataset = getDatasetWorkDispatcher().cloneDataset(sessionUser, dataset);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' cloned dataset " + getStoreId(dataset));
        }
        return clonedDataset;
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void saveEasyMetadata(final EasyUser sessionUser, final Dataset dataset, final WorkListener... workListeners)
            throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().saveEasyMetadata(sessionUser, dataset, workListeners);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' saved EasyMetadata of dataset " + getStoreId(dataset));
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void saveAdministrativeMetadata(final EasyUser sessionUser, final Dataset dataset, final WorkListener... workListeners)
            throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().saveAdministrativeMetadata(sessionUser, dataset, workListeners);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' saved AdministrativeMetadata of dataset "
                    + getStoreId(dataset));
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void submitDataset(final DatasetSubmission submission, final WorkListener... workListeners) throws ServiceException, DataIntegrityException
    {
        final Dataset dataset = submission.getDataset();
        final EasyUser sessionUser = submission.getSessionUser();

        getDatasetWorkDispatcher().submitDataset(sessionUser, dataset, submission, workListeners);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(submission.getState());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void unsubmitDataset(final EasyUser sessionUser, final Dataset dataset, final boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().unsubmitDataset(sessionUser, dataset, mustNotifyDepositor);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Unsubmitted dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void publishDataset(final EasyUser sessionUser, final Dataset dataset, final boolean mustNotifyDepositor,
            final boolean mustIncludeLicense) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().publishDataset(sessionUser, dataset, mustNotifyDepositor, mustIncludeLicense);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Published dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void unpublishDataset(final EasyUser sessionUser, final Dataset dataset, final boolean mustNotifyDepositor)
            throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().unpublishDataset(sessionUser, dataset, mustNotifyDepositor);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Unpublished dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void maintainDataset(final EasyUser sessionUser, final Dataset dataset, final boolean mustNotifyDepositor) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().maintainDataset(sessionUser, dataset, mustNotifyDepositor);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Maintained dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void republishDataset(final EasyUser sessionUser, final Dataset dataset, final boolean mustNotifyDepositor,
            final boolean mustIncludeLicense) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().republishDataset(sessionUser, dataset, mustNotifyDepositor, mustIncludeLicense);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Republished dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteDataset(final EasyUser sessionUser, final Dataset dataset) throws ServiceException
    {
        try
        {
            getDatasetWorkDispatcher().deleteDataset(sessionUser, dataset);
        }
        catch (DataIntegrityException e)
        {
            LOGGER.warn("Dataset not valid: ", e);
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Deleted dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void restoreDataset(final EasyUser sessionUser, final Dataset dataset) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().restoreDataset(sessionUser, dataset);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Restored dataset with sid " + dataset.getStoreId());
        }
    }

    /**
     * {@inheritDoc}
     * @throws DataIntegrityException 
     */
    public void changeDepositor(final EasyUser sessionUser, final Dataset dataset, final EasyUser newDepositor, final boolean mustNotifyDepositor,
            final boolean mustNotifyNewDepositor) throws ServiceException, DataIntegrityException
    {
        getDatasetWorkDispatcher().changeDepositor(sessionUser, dataset, newDepositor, mustNotifyDepositor,
                mustNotifyNewDepositor);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Changed depositor of dataset with sid " + dataset.getStoreId());
        }
    }

    public void savePermissionRequest(final EasyUser sessionUser, final Dataset dataset, final PermissionRequestModel requestModel,
            final WorkListener... workListeners) throws ServiceException
    {
        getDatasetWorkDispatcher().savePermissionRequest(sessionUser, dataset, requestModel, workListeners);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' saved PermissionRequest of dataset "
                    + getStoreId(dataset));
        }
    }

    public void savePermissionReply(final EasyUser sessionUser, final Dataset dataset, final PermissionReplyModel replyModel,
            final WorkListener... workListeners) throws ServiceException
    {
        getDatasetWorkDispatcher().savePermissionReply(sessionUser, dataset, replyModel, workListeners);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("User '" + getUserId(sessionUser) + "' saved PermissionReply of dataset "
                    + getStoreId(dataset));
        }
    }
    
    public URL getUnitMetadataURL(EasyUser sessionUser, Dataset dataset, UnitMetadata unitMetadata) throws ServiceException
    {
        return getDatasetWorkDispatcher().getUnitMetadataURL(sessionUser, dataset, unitMetadata);
    }
    
    @Override
	public URL getAdditionalLicenseURL(Dataset dataset) throws ServiceException {
    	URL url = Data.getEasyStore().getFileURL(
                dataset.getDmoStoreId(), 
                new DsUnitId(AdditionalLicenseUnit.UNIT_ID));
		return url;
	}
    
    public DownloadHistory getDownloadHistoryFor(final EasyUser sessionUser, final Dataset dataset, final DateTime date)
            throws ServiceException
    {
        return getDatasetWorkDispatcher().getDownloadHistoryFor(sessionUser, dataset, date);
    }
    
    private DatasetWorkDispatcher getDatasetWorkDispatcher()
    {
        if (datasetWorkDispatcher == null)
        {
            datasetWorkDispatcher = new DatasetWorkDispatcher();
        }
        return datasetWorkDispatcher;
    }

	public CommonDataset getCommonDataset(final DmoStoreId sid) throws ServiceException
	{
		try
		{
			return (CommonDataset) Data.getEasyStore().retrieve(sid);
		}
		catch (final RepositoryException e)
		{
			throw new ServiceException(e);
		}
	}

	public List<UnitMetadata> getAdditionalLicenseVersions(final Dataset dataset) throws ServiceException
	{
		// TODO add security: only depositor and archivist
		try
		{
			return Data.getEasyStore().getUnitMetadata(dataset.getDmoStoreId(), new DsUnitId(AdditionalLicenseUnit.UNIT_ID));
		}
		catch (final RepositoryException e)
		{
			throw new ServiceException(e);
		}
	}

	private class CompareByCreationDate implements Comparator<UnitMetadata>
	{
		public int compare(final UnitMetadata arg0, final UnitMetadata arg1)
		{
			return - arg0.getCreationDate().compareTo(arg1.getCreationDate());
		}
	}

	public UnitMetadata getAdditionalLicense(final Dataset dataset) throws ServiceException
	{
		// TODO add security: any user who can download the dataset
		try
		{
			// code smell: same sort done in UnitMetaDataPanel, can't we just get the last from the store?
			// see also Data.getEasyStore().getFileURL(storeId, unitId, dateTime)
			List<UnitMetadata> list = Data.getEasyStore().getUnitMetadata(dataset.getDmoStoreId(), new DsUnitId(AdditionalLicenseUnit.UNIT_ID));
			if (list == null || list.size() < 1 ) return null;
			Collections.sort(list, new CompareByCreationDate());
			return list.get(0);
		}
		catch (final RepositoryException e)
		{
			throw new ServiceException(e);
		}
	}
	
	public List<UnitMetadata> getLicenseVersions(final Dataset dataset) throws ServiceException
	{
		// TODO add security: only known users
		try
		{
			return Data.getEasyStore().getUnitMetadata(dataset.getDmoStoreId(), new DsUnitId(LicenseUnit.UNIT_ID));
		}
		catch (final RepositoryException e)
		{
			throw new ServiceException(e);
		}
	}

	public void saveAdditionalLicense(EasyUser sessionUser, Dataset dataset, final WorkListener... workListeners) throws ServiceException, DataIntegrityException
	{
		try
		{
			// TODO currently we need the same security for both, butt better split them
			getDatasetWorkDispatcher().saveAdministrativeMetadata(sessionUser, dataset, workListeners);
		}
		catch (ServiceException e)
		{
			throw new ServiceException(e);
		}
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("User '" + getUserId(sessionUser) + "' saved Additional License of dataset " + getStoreId(dataset));
		}
	}

}

