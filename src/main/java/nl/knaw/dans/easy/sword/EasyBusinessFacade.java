package nl.knaw.dans.easy.sword;

import static nl.knaw.dans.easy.sword.EasyMetadataFacade.getFormDefinition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.EasyMailComposer;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for the easy business API
 */
public class EasyBusinessFacade
{
    /** TODO share constant with {@link SubmitNotification} or define another template */
    static final String        TEMPLATE              = SubmitNotification.TEMPLATE_BASE_LOCATION + "deposit/depositConfirmation" + ".html";

    private static int         noOpSumbitCounter     = 0;
    public static final String NO_OP_STORE_ID_DOMAIN = "mockedStoreID:";
    
    private static Logger      logger                = LoggerFactory.getLogger(EasyBusinessFacade.class);

    /**
     * Gets an authenticated user.
     * 
     * @param userID
     * @param password
     * @return
     * @throws SWORDAuthenticationException
     *         if the user can not be authenticated
     * @throws SWORDException
     *         if required services are not available
     */
    public static EasyUser getUser(final String userId, final String password) throws SWORDException, SWORDErrorException, SWORDAuthenticationException
    {
        if (userId==null ||userId.length()==0)
            throw new SWORDAuthenticationException("no credentials", null);
        final FederativeAuthentication federativeAuthentication = new FederativeAuthentication(userId, password);
        if (!federativeAuthentication.canBeTraditionalAccount())
        {
            final String fedUserId = federativeAuthentication.getUserId();
            if (fedUserId== null)
                throw new SWORDAuthenticationException("invalid credentials", null);
            return getFederativeUser(fedUserId);
        }

        // not a federative user
        authenticate(userId, password);
        try
        {
            return Data.getUserRepo().findById(userId);
        }
        catch (final ObjectNotInStoreException exception)
        {
            throw new SWORDAuthenticationException(userId + " not authenticed", exception);
        }
        catch (final RepositoryException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
    }

    // NO username/password authentication, that is allready done via the federation
    private static EasyUser getFederativeUser(final String fedUserId) throws SWORDException, SWORDAuthenticationException
    {
        FederativeUserIdMap userIdMap = null;
        try
        {
            userIdMap = Data.getFederativeUserRepo().findById(fedUserId);
        }
        catch (final ObjectNotInStoreException e)
        {
            throw new SWORDAuthenticationException(fedUserId + " authentication problem", e);
        }
        catch (final RepositoryException e)
        {
            throw newSwordException("Could not get user with fedUserId '" + fedUserId + "' :", e);
        }

        final String userId = userIdMap.getDansUserId();
        logger.debug("Found easy user for federative user: fedUserId='" + fedUserId + "', userId='" + userId + "'");
        try
        {
            return Data.getUserRepo().findById(userId);
        }
        catch (final ObjectNotInStoreException exception)
        {
            throw new SWORDAuthenticationException(userId + " authentication problem", exception);
        }
        catch (final RepositoryException exception)
        {
            throw newSwordException(userId + " authentication problem", exception);
        }
    }

    private static void authenticate(final String userId, final String password) throws SWORDException, SWORDErrorException, SWORDAuthenticationException
    {
        try
        {
            if (userId == null || password == null)
                throw newSwordInputException("missing username [" + userId + "] or password", null);
            else if (!Data.getUserRepo().authenticate(userId, password))
                throw new SWORDAuthenticationException("invalid username [" + userId + "] or password", null);
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

    /** Wraps exceptions thrown by Services.getDatasetService().submitDataset */
    private static void submit(final EasyUser user, final Dataset dataset) throws SWORDException, SWORDErrorException
    {
        // final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(new
        // FormDefinition("dummy"), dataset, user);
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(getFormDefinition(dataset.getEasyMetadata()), dataset, user);
        final IngestReporter reporter = new IngestReporter("submitting " + dataset.getStoreId() + " by " + user);
        try
        {
            logger.info("submitting " + dataset.getStoreId() + " " + user.getId());
            Services.getDatasetService().submitDataset(submission, reporter);
            if (!reporter.checkOK())
                throw newSwordException("Dataset created but problem with submitting", null);
            if (submission.hasGlobalMessages())
            {
                for (final String s : submission.getGlobalErrorMessages())
                    logger.error(s);
                for (final String s : submission.getGlobalInfoMessages())
                    logger.error(s);
            }
            if (submission.hasMetadataErrors())
            {
                // should have been covered by validateSemantics
                final String format = "%s created by [%s] but not submitted because meta data has errors: ";
                String message = String.format(format, dataset.getStoreId(), user.getId());
                for (final PanelDefinition panelDef : submission.getFirstErrorPage().getPanelDefinitions())
                {
                    if (panelDef.getErrorMessages().size() > 0)
                        message += " " + panelDef.getLabelResourceKey() + " " + panelDef.getErrorMessages();
                }
                throw newSwordInputException(message, null);
            }
            if (!submission.isMailSend())
            {
                logger.warn("no submission mail sent for " + dataset.getStoreId() + " " + user.getId());
            }
            if (!submission.isCompleted())
            {
                if (!Services.getDatasetService().getClass().getName().startsWith("$Proxy"))
                    // FIXME workaround mock problems for JUnit tests
                    throw newSwordException("submission incomplete " + dataset.getStoreId() + " " + user.getId(), null);
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
    private static void ingestFiles(final EasyUser user, final Dataset dataset, final File tempDirectory, final List<File> fileList) throws SWORDException,
            SWORDErrorException
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
            final IngestReporter reporter = new IngestReporter(message);
            logger.debug(message);

            itemService.addDirectoryContents(user, dataset, dataset.getDmoStoreId(), tempDirectory, fileList, reporter);
            if (!reporter.checkOK())
                throw newSwordException("Dataset created but problem with ingesting files", null);
        }
        catch (final ServiceException exception)
        {
            throw newSwordException("Can't add files to the new dataset " + storeId + " " + user.getId(), exception);
        }
    }

    public static String formatAudience(final EasyMetadata metadata) throws SWORDErrorException
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

    private static SWORDException newSwordException(final String message, final Exception exception)
    {
        logger.error(message, exception);
        if (exception != null)
            return new SWORDException(message, exception);
        return new SWORDException(message, new Exception(message));
    }

    private static SWORDErrorException newSwordInputException(final String message, final Exception exception)
    {
        logger.error(message, exception);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
    }

    public static String getSubmussionNotification(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        try
        {
            final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, user);
            final EasyMailComposer composer = new EasyMailComposer(user, dataset, submission, new SubmitNotification(submission));
            return composer.composeHtml(TEMPLATE);
        }
        catch (final MailComposerException exception)
        {
            final String message = "Could not compose submussion notification";
            throw newSwordException(message, exception);
        }
    }

    public static String getLicenseAsHtml(final EasyUser user, final boolean generateSample, final Dataset dataset) throws SWORDException
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

    static void resetNoOpSubmitCounter(){
        // JUnit test execution order is unstable, a constant value makes results stable
        noOpSumbitCounter = 0;
    }
    
    public static Dataset mockSubmittedDataset(final EasyMetadata metadata, final EasyUser user)
    {
        ++noOpSumbitCounter;
        final String pid = (noOpSumbitCounter + "xxxxxxxx").replaceAll("(..)(...)(...)", "urn:nbn:nl:ui:$1-$2-$3");
        final String storeId = NO_OP_STORE_ID_DOMAIN + noOpSumbitCounter;
        DmoStoreId DmoStoreID = new DmoStoreId(storeId);
        final Dataset dataset = EasyMock.createMock(Dataset.class);

        // TODO the following lines duplicates logic of DatasetImpl, move to EasyMetadata?
        final List<IsoDate> lda = metadata.getEmdDate().getEasAvailable();
        final List<IsoDate> lds = metadata.getEmdDate().getEasDateSubmitted();
        final DateTime dateAvailable = (lda.size() == 0 ? null : lda.get(0).getValue());
        final IsoDate dateSubmitted = (lds.size() == 0) ? new IsoDate() : lds.get(0);
        final boolean underEmbargo = dateAvailable != null && new DateTime().plusMinutes(1).isBefore(dateAvailable);

        EasyMock.expect(dataset.getEasyMetadata()).andReturn(metadata).anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(storeId).anyTimes();
        EasyMock.expect(dataset.getDmoStoreId()).andReturn(DmoStoreID).anyTimes();
        EasyMock.expect(dataset.getAccessCategory()).andReturn(metadata.getEmdRights().getAccessCategory()).anyTimes();
        EasyMock.expect(dataset.getDateSubmitted()).andReturn(dateSubmitted).anyTimes();
        EasyMock.expect(dataset.getDateAvailable()).andReturn(dateAvailable).anyTimes();
        EasyMock.expect(dataset.getPreferredTitle()).andReturn(metadata.getPreferredTitle()).anyTimes();
        EasyMock.expect(dataset.getDepositor()).andReturn(user).anyTimes();
        EasyMock.expect(dataset.getPersistentIdentifier()).andReturn(pid).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(underEmbargo).anyTimes();
        EasyMock.replay(dataset);
        return dataset;
    }
}
