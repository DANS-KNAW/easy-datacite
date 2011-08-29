package nl.knaw.dans.easy.sword;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.EasyMailComposer;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataValidator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
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
public class EasyBusinessWrapper
{
    /** TODO share constant with {@link SubmitNotification} or define another template */
    static final String        TEMPLATE            = SubmitNotification.TEMPLATE_BASE_LOCATION + "deposit/depositConfirmation" + ".html";

    public static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

    private static Logger      logger              = LoggerFactory.getLogger(EasyBusinessWrapper.class);

    /**
     * Gets an authenticated user.
     * 
     * @param userID
     * @param password
     * @return
     * @throws SWORDAuthenticationException
     *         if required services are not available
     * @throws SWORDException
     *         if the user can not be authenticated
     */
    public static EasyUser getUser(final String userId, final String password) throws SWORDAuthenticationException, SWORDException
    {
        authenticate(userId, password);
        try
        {
            return Data.getUserRepo().findById(userId);
        }
        catch (ObjectNotInStoreException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
        catch (RepositoryException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
    }

    private static void authenticate(final String userId, final String password) throws SWORDException
    {
        try
        {
            Data.getUserRepo().authenticate(userId, password);
        }
        catch (RepositoryException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
    }

    /**
     * Submits a new dataset.
     * 
     * @param user
     *        the owner of the new dataset
     * @param metadata
     *        the metadata for the new dataset,
     * @param directory
     *        a directory containing the files for the new dataset
     * @param fileList
     *        the list of files in the directory to add to the new dataset
     * @return
     * @throws SWORDException
     */
    public static Dataset submitNewDataset(final EasyUser user, final EasyMetadata metadata, final File directory, final List<File> fileList)
            throws SWORDException
    {
        final MetadataFormat mdFormat = metadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        final Dataset dataset = createEmptyDataset(mdFormat);

        enhanceWithDefaults(metadata, dataset);
        ((DatasetImpl) dataset).setEasyMetadata(metadata);

        dataset.setOwnerId(user.getId());
        dataset.getAdministrativeMetadata().setDepositor(user);

        ingestFiles(user, dataset, directory, fileList);
        submit(user, dataset);

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

    /** Just a wrapper for exceptions. */
    private static void submit(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(new FormDefinition("dummy"), dataset, user);
        final MyReporter reporter = new MyReporter("submitting " + dataset.getStoreId() + " by " + user, "problem with submitting");

        try
        {
            Services.getDatasetService().submitDataset(submission, reporter);
            reporter.checkOK();
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Dataset created but submission failed " + dataset.getStoreId() + " " + user.getId(), exception);
        }
        catch (DataIntegrityException exception)
        {
            throw newSwordException("Dataset created but submission failed " + dataset.getStoreId() + " " + user.getId(), exception);
        }
    }

    /** Just a wrapper for exceptions. */
    private static void ingestFiles(final EasyUser user, final Dataset dataset, final File tempDirectory, final List<File> fileList) throws SWORDException
    {
        final String storeId = dataset.getStoreId();
        try
        {
            final ItemService itemService = Services.getItemService();
            final StringBuffer list = new StringBuffer();
            for (File file : fileList)
            {
                list.append("\n\t" + file);
            }
            final String message = "ingesting files from " + tempDirectory + " into " + dataset.getStoreId() + list;
            final MyReporter reporter = new MyReporter(message, "ingesting files");
            logger.debug(message);

            itemService.addDirectoryContents(user, dataset, storeId, tempDirectory, fileList, reporter);

            final int size = itemService.getFilesAndFolders(user, dataset, storeId, -1, -1, null, null).size();
            reporter.checkOK();
            if (size == 0)
            {
                logger.error("Services.getItemService().getFilesAndFolders() does not find the ingested files, verify /opt/fedora/server/config/custom-db.xml");
                throw newSwordException("ingested files not retreivable", null);
            }
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't add files to the new dataset " + storeId + " " + user.getId(), exception);
        }
    }

    private static class MyReporter extends WorkReporter
    {

        List<Throwable>      reportedExceptions = new ArrayList<Throwable>();
        private final String message;
        private final String messageForClient;

        MyReporter(final String message, final String messageForClient)
        {
            this.message = message;
            this.messageForClient = messageForClient;
        }

        @Override
        public void onException(final Throwable t)
        {
            super.onException(t);
            logger.error("problem with " + message, t);
            reportedExceptions.add(t);
        }

        public void checkOK() throws SWORDException
        {
            logger.debug(
                    " exceptions: \n" + reportedExceptions.size() + "\n" + super.toString());
            if (reportedExceptions.size() > 0 )
                throw newSwordException("Dataset created but problem with " + messageForClient, null);
        }
    }

    /** Just a wrapper for exceptions. */
    private static Dataset createEmptyDataset(final MetadataFormat metadataFormat) throws SWORDException
    {
        final Dataset dataset;
        try
        {
            dataset = Services.getDatasetService().newDataset(metadataFormat);
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't create a new dataset " + metadataFormat, exception);
        }
        return dataset;
    }

    /** Just a wrapper for exceptions. */
    static EasyMetadata unmarshallEasyMetaData(final byte[] data) throws SWORDException
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

    static void validateSemantics(final EasyUser user, final EasyMetadata metadata) throws SWORDException
    {
        final EasySwordValidationReporter validationReporter = new EasySwordValidationReporter();
        FormatValidator.instance().validate(metadata, validationReporter);
        if (!validationReporter.isMetadataValid())
            throw new SWORDException(user.getId() + " tried to submit invalid meta data");
    }

    static void validateSyntax(final byte[] data) throws SWORDException
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
            throw new SWORDException("Invalid EASY metadata: \n" + handler.getMessages(), null, ErrorCodes.ERROR_BAD_REQUEST);
    }

    private static SWORDException newSwordException(final String message, final Exception exception)
    {
        logger.error(message, exception);
        return new SWORDException(message, exception);
    }

    public static String composeTreatment(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        try
        {
            final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, user);
            final EasyMailComposer composer = new EasyMailComposer(user, dataset, submission, new SubmitNotification(submission));
            return composer.composeHtml(TEMPLATE);
        }
        catch (MailComposerException exception)
        {
            final String message = "Could not compose treatment";
            logger.error(message, exception);
            throw new SWORDException(message);
        }
    }

    public static String composeLicense(final EasyUser user, final boolean generateSample, final Dataset dataset) throws SWORDException
    {

        final String errorMessage = "Could not create license document";
        try
        {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            logger.debug(dataset + " AccessCategory=" + dataset.getAccessCategory());
            new LicenseComposer(user, dataset, generateSample).createHtml(outputStream);
            return outputStream.toString();
        }
        catch (final NoSuchMethodError exception)
        {
            logger.error(errorMessage, exception);
            throw new SWORDException(errorMessage);
        }
        catch (final LicenseComposerException exception)
        {
            logger.error(errorMessage, exception);
            throw new SWORDException(errorMessage);
        }
    }

}
