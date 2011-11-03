package nl.knaw.dans.easy.sword;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
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
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.DisciplineImpl;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
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
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Wrapper for the easy business API
 */
public class EasyBusinessFacade
{
    /** TODO share constant with {@link SubmitNotification} or define another template */
    static final String        TEMPLATE            = SubmitNotification.TEMPLATE_BASE_LOCATION + "deposit/depositConfirmation" + ".html";

    public static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

    private static Logger      logger              = LoggerFactory.getLogger(EasyBusinessFacade.class);

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
    public static EasyUser getUser(final String userId, final String password) throws SWORDException, SWORDErrorException
    {
        authenticate(userId, password);
        try
        {
            return Data.getUserRepo().findById(userId);
        }
        catch (final ObjectNotInStoreException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
        catch (final RepositoryException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
    }

    private static void authenticate(final String userId, final String password) throws SWORDException, SWORDErrorException
    {
        try
        {
            if (userId == null || password == null || !Data.getUserRepo().authenticate(userId, password))
                throw newSwordInputException("invalid or missing username ["+userId+"] or password", null);
            logger.info(userId + " authenticated");
        }
        catch (final RepositoryException exception)
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
     * @throws SWORDErrorException 
     */
    public static Dataset submitNewDataset(final EasyUser user, final EasyMetadata metadata, final File directory, final List<File> fileList)
            throws SWORDException, SWORDErrorException
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
    
    private static FormDefinition getFormDefinition(final EasyMetadata emd) throws SWORDException 
    {
        final MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        
        DepositDiscipline discipline;
        try
        {
            discipline = Services.getDepositService().getDiscipline(mdFormat);
        }
        catch (final ServiceException e)
        {
            RL.error(new Event("Cannot get deposit discipline.", e, e.getMessage(), "MetadataFormat is " + mdFormat));
            throw newSwordException("Cannot get deposit discipline." , e);
        }
        if (discipline == null)
        {
            if (Services.getDepositService().getClass().getName().startsWith("$Proxy"))
                // FIXME workaround unexpected mock results for JUnit tests
                discipline = new DisciplineImpl(new FormDescriptor("dummy"));
            else
                throw newSwordException("Cannot get deposit discipline.", null);
        }
        final FormDefinition formDefinition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
        return formDefinition;
    }
    
    /** Wraps exceptions thrown by Services.getDatasetService().submitDataset */
    private static void submit(final EasyUser user, final Dataset dataset) throws SWORDException, SWORDErrorException
    {
//        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(new FormDefinition("dummy"), dataset, user);
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(getFormDefinition(dataset.getEasyMetadata()), dataset, user);
        final IngestReporter reporter = new IngestReporter("submitting " + dataset.getStoreId() + " by " + user, "problem with submitting");
        try
        {
            logger.info("submitting " + dataset.getStoreId() + " " + user.getId());
            Services.getDatasetService().submitDataset(submission, reporter);
            reporter.checkOK();
            if (submission.hasGlobalMessages())
            {
                for (String s : submission.getGlobalErrorMessages())
                    logger.error(s);
                for (String s : submission.getGlobalInfoMessages())
                    logger.error(s);
            }
            if (submission.hasMetadataErrors())
            {
                /*
                 * TODO rather validate before creating the dataset, requires refactoring of
                 * MetadataValidator.process(submission) into validate(metadata,formDefinition)
                 */
                final String format = "%s created by [%s] but not submitted because meta data has errors: ";
                String message = String.format(format, dataset.getStoreId(), user.getId());
                for (PanelDefinition panelDef : submission.getFirstErrorPage().getPanelDefinitions())
                {
                    if (panelDef.getErrorMessages().size() > 0)
                        message += " " + panelDef.getLabelResourceKey() + " " + panelDef.getErrorMessages();
                }
                throw newSwordInputException(message, null);
            }
            if (!submission.isCompleted())
            {
                if (!Services.getDatasetService().getClass().getName().startsWith("$Proxy"))
                    // FIXME workaround mock problems for JUnit tests
                    throw newSwordException("submission incomplete " + dataset.getStoreId() + " " + user.getId(), null);
            }
            if (!submission.isMailSend())
            {
                logger.warn("no submission mail sent for " + dataset.getStoreId() + " " + user.getId());
            }
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Dataset created but submission failed " + dataset.getStoreId() + " " + user.getId(), exception);
        }
        catch (final DataIntegrityException exception)
        {
            throw newSwordException("Dataset created but submission failed " + dataset.getStoreId() + " " + user.getId(), exception);
        }
    }

    /** Wraps exceptions thrown by Services.getItemService().addDirectoryContents(user, dataset, ...) */
    private static void ingestFiles(final EasyUser user, final Dataset dataset, final File tempDirectory, final List<File> fileList) throws SWORDException
    {
        final String storeId = dataset.getStoreId();
        try
        {
            final ItemService itemService = Services.getItemService();
            final StringBuffer list = new StringBuffer();
            for (final File file : fileList)
            {
                list.append("\n\t" + file);
            }
            final String message = "ingesting files from " + tempDirectory + " into " + dataset.getStoreId() + list;
            final IngestReporter reporter = new IngestReporter(message, "ingesting files");
            logger.debug(message);

            itemService.addDirectoryContents(user, dataset, storeId, tempDirectory, fileList, reporter);
            reporter.checkOK();
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't add files to the new dataset " + storeId + " " + user.getId(), exception);
        }
    }

    private static class IngestReporter extends WorkReporter
    {

        List<Throwable>      reportedExceptions = new ArrayList<Throwable>();
        private final String message;
        private final String messageForClient;

        IngestReporter(final String message, final String messageForClient)
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
            logger.debug(" exceptions: \n" + reportedExceptions.size() + "\n" + super.toString());
            if (reportedExceptions.size() > 0)
                throw newSwordException("Dataset created but problem with " + messageForClient, null);
        }
    }

    /** Wraps exceptions thrown by Services.getDatasetService().newDataset(metadataFormat). */
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
    static EasyMetadata unmarshallEasyMetaData(final byte[] data) throws SWORDErrorException
    {
        final EasyMetadata metadata;
        try
        {
            metadata = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, data);
        }
        catch (final XMLDeserializationException exception)
        {
            throw newSwordInputException("EASY metadata unmarshall exception: " + exception.getMessage(), exception);
        }
        return metadata;
    }

    static void validateSemantics(final EasyUser user, final EasyMetadata metadata) throws SWORDErrorException
    {
        final EasySwordValidationReporter validationReporter = new EasySwordValidationReporter();
        FormatValidator.instance().validate(metadata, validationReporter);
        if (!validationReporter.isMetadataValid())
            throw newSwordInputException(user.getId() + " tried to submit invalid meta data", null);
    }

    static void validateSyntax(final byte[] data) throws SWORDErrorException, SWORDException
    {
        final XMLErrorHandler handler = new XMLErrorHandler(Reporter.off);
        try
        {
            EasyMetadataValidator.instance().validate(handler, new String(data, "UTF-8"), DEFAULT_EMD_VERSION);
        }
        catch (final ValidatorException exception)
        {
            throw newSwordInputException("EASY metadata validation exception: " + exception.getMessage(), exception);
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw newSwordInputException("EASY metadata encoding exception: " + exception.getMessage(), exception);
        }
        catch (final SAXException exception)
        {
            throw newSwordInputException("EASY metadata parse exception: " + exception.getMessage(), exception);
        }
        catch (final SchemaCreationException exception)
        {
            throw newSwordException("EASY metadata schema creation problem", exception);
        }
        if (!handler.passed())
            throw newSwordInputException("Invalid EASY metadata: \n" + handler.getMessages(), null);
    }

    private static SWORDException newSwordException(final String message, final Exception exception)
    {
        logger.error(message, exception);
        return new SWORDException(message, exception);
    }

    private static SWORDErrorException newSwordInputException(final String message, final Exception exception)
    {
        logger.error(message, exception);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
    }

    public static String composeTreatment(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        try
        {
            final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, user);
            final EasyMailComposer composer = new EasyMailComposer(user, dataset, submission, new SubmitNotification(submission));
            return composer.composeHtml(TEMPLATE);
        }
        catch (final MailComposerException exception)
        {
            final String message = "Could not compose treatment";
            throw newSwordException(message, exception);
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
            throw newSwordException(errorMessage + ": " + exception.getMessage(), exception);
        }
    }

    static String formatAudience(final EasyMetadata metadata) throws SWORDErrorException
    {
        try
        {
            return LicenseComposer.formatAudience(metadata);
        }
        catch (final LicenseComposerException exception)
        {
            throw newSwordInputException(exception.getMessage(), exception);
        }
    }
}
