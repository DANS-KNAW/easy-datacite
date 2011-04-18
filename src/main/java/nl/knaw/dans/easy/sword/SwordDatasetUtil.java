package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.domain.authn.Authentication.State;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataValidator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Wrapper for the easy business API
 */
public class SwordDatasetUtil
{
    public static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

    private static Logger log     = LoggerFactory.getLogger(SwordDatasetUtil.class);

    /**
     * Gets an authenticated user.
     * 
     * @param userID
     * @param password
     * @return
     * @throws SWORDAuthenticationException if required services are not available
     * @throws SWORDException if the user can not be authenticated
     */
    public static EasyUser getUser(final String userID, final String password) throws SWORDAuthenticationException, SWORDException
    {
        final UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(userID, password);
        try
        {
            Services.getUserService().authenticate(authentication);
        }
        catch (final ServiceException exception)
        {
            throw newSwordException(userID + " authentication problem",exception);
        }
        if (authentication.getState() == State.NotAuthenticated)
            return null;
        return authentication.getUser();
    }

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
     * @param workListeners
     * @return
     * @throws SWORDException
     */
    public static Dataset submitNewDataset(final EasyUser user, final byte[] easyMetadata, final File directory, final List<File> fileList,
            final WorkListener... workListeners) throws SWORDException
    {
        final Dataset dataset = createDataset(user, easyMetadata, directory, fileList);
        submit(user, dataset, workListeners);
        return dataset;
    }

    /** As {@link #submitNewDataset(EasyUser, byte[], File, List)} without the submission. */
    private static Dataset createDataset(final EasyUser user, final byte[] easyMetadata, final File directory, final List<File> fileList) throws SWORDException
    {
        validateEasyMetadata(easyMetadata);

        final EasyMetadata metadata = unmarshallEasyMetaData(easyMetadata);
        final MetadataFormat mdFormat = metadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        final Dataset dataset = createEmptyDataset(mdFormat);

        enhanceWithDefaults(metadata, dataset);

        ((DatasetImpl)dataset).setEasyMetadata(metadata);
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
    private static EasyMetadata enhanceWithDefaults(final EasyMetadata metadata, final Dataset dataset)
    {
        final List<BasicString> audienceList = dataset.getEasyMetadata().getEmdAudience().getTermsAudience();
        if (!audienceList.isEmpty())
        {
            metadata.getEmdAudience().getTermsAudience().add(audienceList.get(0));
        }
        return metadata;
    }

    /** Just a wrapper to wrap exceptions. */
    private static void submit(final EasyUser user, final Dataset dataset, final WorkListener... workListeners) throws SWORDException
    {
        // TODO FORM definition designed report error to the web GUI, but we are no GUI
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(new FormDefinition("dummy") , dataset, user);
        try
        {
            log.debug("before Services.getDatasetService().submitDataset for "+dataset.getStoreId());
            Services.getDatasetService().submitDataset(submission, workListeners);
            log.debug("after Services.getDatasetService().submitDataset for "+dataset.getStoreId());
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Dataset created but submission failed " + dataset.getStoreId() + " "+user.getId(), exception);
        }
    }

    /** Just a wrapper to wrap exceptions. */
    private static void addFiles(final EasyUser user, final Dataset dataset, final File tempDirectory, final List<File> fileList) throws SWORDException
    {
        final String storeId = dataset.getStoreId();
        try
        {
            log.debug(user.getId()+" "+user.getDisplayName()+" "+storeId+" "+tempDirectory+" "+Arrays.deepToString(fileList.toArray()));
            Services.getItemService().addDirectoryContents(user, dataset, storeId, tempDirectory, fileList, new WorkReporter());
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't add files to the new dataset " + storeId + " "+user.getId(), exception);
        }
    }

    /** Just a wrapper to wrap exceptions. */
    private static Dataset createEmptyDataset(final MetadataFormat metadataFormat) throws SWORDException
    {
        final Dataset dataset;
        try
        {
            dataset = Services.getDatasetService().newDataset(metadataFormat);
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't create a new dataset "+metadataFormat, exception);
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
            throw newSwordException("EASY metadata unmarshall exception", exception);
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
            throw newSwordException("EASY metadata validation exception", exception);
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw newSwordException("EASY metadata validation exception", exception);
        }
        catch (final SAXException exception)
        {
            throw newSwordException("EASY metadata validation exception", exception);
        }
        catch (final SchemaCreationException exception)
        {
            throw newSwordException("EASY metadata validation exception", exception);
        }
        if (!handler.passed())
            throw new SWORDException("Invalid EASY metadata: \n" + handler.getMessages(),null,ErrorCodes.ERROR_BAD_REQUEST);
    }

    private static SWORDException newSwordException(final String message, final Exception exception)
    {
        log.error(message,exception);
        return new SWORDException(message);
    }
}
