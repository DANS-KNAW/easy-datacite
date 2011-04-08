package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataValidator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.purl.sword.base.SWORDException;
import org.xml.sax.SAXException;

public class SwordDatasetUtil
{
    public static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

    /**
     * Submits a new dataset.
     * 
     * @param user
     *        the owner of the new dataset
     * @param easyMetadata
     *        the metadata for the new dataset,
     * @param directory
     *        a directory containing the files for the new dataset
     * @param fileList
     *        the list of files in the directory to add to the new dataset
     * @return
     * @throws SWORDException
     */
    public static Dataset submitNewDataset(final String userID, final byte[] easyMetadata, final File directory, final List<File> fileList)
            throws SWORDException
    {
        final EasyUser user = getUser(userID);
        final Dataset dataset = createDataset(user, easyMetadata, directory, fileList);
        submit(user, dataset);
        return dataset;
    }

    /** As {@link #submitNewDataset(EasyUser, byte[], File, List)} but the dataset will be public immediately. */
    public static Dataset publishNewDataset(final String userID, final byte[] easyMetadata, final File directory, final List<File> fileList)
            throws SWORDException
    {
        final EasyUser user = getUser(userID);

        // TODO rather ask the SecurityOfficer if publishing is allowed 
        if (!user.hasRole(Role.ARCHIVIST))
            throw new SWORDException(user + " has no permission to submit. Dataset not created");

        final Dataset dataset = createDataset(user, easyMetadata, directory, fileList);
        submit(user, dataset);
        publish(user, dataset);
        return dataset;
    }

    /** As {@link #submitNewDataset(EasyUser, byte[], File, List)} without the submission. */
    private static Dataset createDataset(final EasyUser user, final byte[] easyMetadata, final File directory, final List<File> fileList) throws SWORDException
    {
        validateEasyMetadata(easyMetadata);
        
        final EasyMetadata metadata = unmarshallEasyMetaData(easyMetadata);
        final MetadataFormat mdFormat = metadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        final DatasetImpl dataset = createEmptyDataset(mdFormat);
        
        enhanceWithDefaults(metadata, dataset);
        
        dataset.setEasyMetadata(metadata);
        dataset.setOwnerId(user.getId());
        addFiles(user, dataset, directory, fileList);
        
        return dataset;
    }
    
    /**
     * Enhances the custom metadata with defaults present in the metadata of the dataset.
     * 
     * @param metadata
     *        the custom metadata
     * @param dataset
     *        containing default metadata for a specific format
     * @return the custom metadata
     */
    private static EasyMetadata enhanceWithDefaults(final EasyMetadata metadata, final DatasetImpl dataset)
    {
        final List<BasicString> audienceList = dataset.getEasyMetadata().getEmdAudience().getTermsAudience();
        if (!audienceList.isEmpty())
        {
            metadata.getEmdAudience().getTermsAudience().add(audienceList.get(0));
        }
        return metadata;
    }

    /** Just a wrapper to wrap exceptions. */
    private static void submit(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        final WorkListener[] workListeners = new WorkListener[]{};
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, user);
        try
        {
            Services.getDatasetService().submitDataset(submission, workListeners);
        }
        catch (final ServiceException exception)
        {
            throw new SWORDException("Dataset created but submision failed. " + user + " " + dataset.getStoreId(), exception);
        }
    }

    /** Just a wrapper to wrap exceptions. */
    private static void publish(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        final boolean mustNotifyDepositor = false;
        final boolean mustIncludeLicense = true;
        try
        {
            Services.getDatasetService().publishDataset(user, dataset, mustNotifyDepositor, mustIncludeLicense);
        }
        catch (final ServiceException exception)
        {
            throw new SWORDException("Dataset created and submitted but publish failed. " + user + " " + dataset.getStoreId(), exception);
        }
    }

    /** Just a wrapper to wrap exceptions. */
    private static void addFiles(final EasyUser user, final DatasetImpl dataset, final File tempDirectory, final List<File> fileList) throws SWORDException
    {
        final String storeId = dataset.getStoreId();
        try
        {
            Services.getItemService().addDirectoryContents(user, dataset, storeId, tempDirectory, fileList, new WorkReporter());
        }
        catch (final ServiceException exception)
        {
            throw new SWORDException("Can't add files to the new dataset " + storeId, exception);
        }
    }

    /** Just a wrapper to wrap exceptions. */
    private static DatasetImpl createEmptyDataset(final MetadataFormat metadataFormat) throws SWORDException
    {
        final DatasetImpl dataset;
        try
        {
            dataset = (DatasetImpl) Services.getDatasetService().newDataset(metadataFormat);
        }
        catch (final ServiceException exception)
        {
            throw new SWORDException("Can't create a new dataset", exception);
        }
        return dataset;
    }

    /** Just a wrapper to wrap exceptions. */
    private static EasyMetadata unmarshallEasyMetaData(final byte[] data) throws SWORDException
    {

        final EasyMetadata metadata;
        try
        {
            metadata = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, data);
        }
        catch (final XMLDeserializationException exception)
        {
            throw new SWORDException("EASY metadata unmarshall exception", exception);
        }
        return metadata;
    }

    /** Just a wrapper to wrap exceptions. */
    private static void validateEasyMetadata(final byte[] data) throws SWORDException
    {
        final XMLErrorHandler handler = new XMLErrorHandler(Reporter.off);
        try
        {
            EasyMetadataValidator.instance().validate(handler, new String(data, "UTF-8"), DEFAULT_EMD_VERSION);
        }
        catch (final ValidatorException exception)
        {
            throw new SWORDException("EASY metadata validation exception", exception);
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw new SWORDException("EASY metadata validation exception", exception);
        }
        catch (final SAXException exception)
        {
            throw new SWORDException("EASY metadata validation exception", exception);
        }
        catch (final SchemaCreationException exception)
        {
            throw new SWORDException("EASY metadata validation exception", exception);
        }
        if (!handler.passed())
            throw new SWORDException("Invalid EASY metadata: \n" + handler.getMessages());
    }

    /** Just a wrapper to wrap exceptions. */
    private static EasyUser getUser(final String ownerId) throws SWORDException
    {
        try
        {
            return Services.getUserService().getUserById(null, ownerId);
        }
        catch (final ObjectNotAvailableException exception)
        {
            throw new SWORDException("Can't find user " + ownerId, exception);
        }
        catch (final ServiceException exception)
        {
            throw new SWORDException("User service exception, " + ownerId, exception);
        }
    }
}
