package nl.knaw.dans.easy.sword;

import static nl.knaw.dans.easy.sword.EasyMetadataFacade.getFormDefinition;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.mail.MailComposer;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.easymock.EasyMock;
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
        if (userId == null || userId.length() == 0)
            throw new SWORDAuthenticationException("no credentials", null);
        final FederativeAuthentication federativeAuthentication = new FederativeAuthentication(userId, password);
        if (!federativeAuthentication.canBeTraditionalAccount())
        {
            final String fedUserId = federativeAuthentication.getUserId();
            if (fedUserId == null)
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
            throw new SWORDException((userId + " authentication problem"), exception);
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
            throw new SWORDException(("Could not get user with fedUserId '" + fedUserId + "' :"), e);
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
            throw new SWORDException((userId + " authentication problem"), exception);
        }
    }

    private static void authenticate(final String userId, final String password) throws SWORDException, SWORDErrorException, SWORDAuthenticationException
    {
        try
        {
            if (userId == null || password == null)
                throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("missing username [" + userId + "] or password"));
            else if (!Data.getUserRepo().authenticate(userId, password))
                throw new SWORDAuthenticationException("invalid username [" + userId + "] or password", null);
            logger.info(userId + " authenticated");
        }
        catch (final RepositoryException exception)
        {
            throw new SWORDException((userId + " authentication problem"), exception);
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
        final DatasetSubmission submission = new DatasetSubmissionImpl(getFormDefinition(dataset.getEasyMetadata()), dataset, user);
        final IngestReporter ingestReporter = new IngestReporter("submitting " + dataset.getStoreId() + " by " + user);
        try
        {
            logger.info("submitting " + dataset.getStoreId() + " " + user.getId());
            Services.getDatasetService().submitDataset(submission, ingestReporter);
            if (!ingestReporter.catchedExceptions())
                throw createSubmitException(dataset, "ingest exceptions: " + Arrays.toString(ingestReporter.getExceptionMessages()));
            if (submission.hasMetadataErrors() || submission.hasGlobalMessages())
                throw createSubmitException(dataset, gatherSubmissionMessages(submission));
            if (!submission.isMailSend())
                logger.warn("no submission mail sent for " + dataset.getStoreId() + " " + user.getId());
            if (!submission.isCompleted())
            {
                // For unit tests we use mocked datasets
                if (!Services.getDatasetService().getClass().getName().startsWith("$Proxy"))
                {
                    // FIXME a mocked dataset does not set the submission conditions, nor sends a mail
                    throw createSubmitException(dataset, "submission not completed");
                }
            }
        }
        catch (final ServiceException exception)
        {
            throw createSubmitException(dataset, exception);
        }
        catch (final DataIntegrityException exception)
        {
            throw createSubmitException(dataset, exception);
        }
    }

    private static String gatherSubmissionMessages(final DatasetSubmission submission)
    {
        final StringBuffer submissionMessages = new StringBuffer();
        if (submission.hasGlobalMessages())
        {
            if (submission.getGlobalErrorMessages().size() > 0)
            {
                submissionMessages.append(" global submission errors: ");
                submissionMessages.append(Arrays.toString(submission.getGlobalErrorMessages().toArray()));
            }
            if (submission.getGlobalInfoMessages().size() > 0)
            {
                submissionMessages.append(" global submission info messages: ");
                submissionMessages.append(Arrays.toString(submission.getGlobalInfoMessages().toArray()));
            }
        }
        if (submission.hasMetadataErrors())
        {
            // should have been covered by validateSemantics
            submissionMessages.append("submission metadata errors: ");
            for (final PanelDefinition panelDef : submission.getFirstErrorPage().getPanelDefinitions())
            {
                if (panelDef.getErrorMessages().size() > 0)
                {
                    submissionMessages.append(" ");
                    submissionMessages.append(panelDef.getLabelResourceKey());
                    submissionMessages.append(" ");
                    submissionMessages.append(Arrays.toString(panelDef.getErrorMessages().toArray()));
                }
            }
        }
        return submissionMessages.toString();
    }

    private static SWORDErrorException createSubmitException(final Dataset dataset, final String cause)
    {
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Created dataset (" + dataset.getStoreId() + ") with status draft. "
                + "Please use the web interface to remove the dataset or to correct the [meta]data and retry submission. \n" + cause);
    }

    private static SWORDErrorException createSubmitException(final Dataset dataset, final Throwable cause)
    {
        logger.error("failed to submit " + dataset.getStoreId(), cause);
        return createSubmitException(dataset, cause.getMessage());
    }

    /** Wraps exceptions thrown by Services.getItemService().addDirectoryContents(user, dataset, ...) */
    private static void ingestFiles(final EasyUser user, final Dataset dataset, final File tempDirectory, final List<File> fileList) throws SWORDException,
            SWORDErrorException
    {
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
            if (!reporter.catchedExceptions())
                throw createSubmitException(dataset, "");
        }
        catch (final ServiceException exception)
        {
            final Throwable cause = exception.getCause();
            if (cause instanceof ApplicationException && cause.getCause() instanceof ObjectNotFoundException)
            {
                // needed at least for invalid discipline id
                throw createSubmitException(dataset, cause.getCause());
            }
            else
                throw createSubmitException(dataset, exception);
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
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, exception.getMessage());
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
            throw new SWORDException(("Can't create a new dataset " + metadataFormat), exception);
        }
        return dataset;
    }

    public static String composeDepositTreatment(final EasyUser user, final Dataset dataset) throws SWORDException
    {
        final String depositTreatment = Context.getDepositTreatment();
        try
        {
            final String html = new MailComposer(user, dataset).compose(depositTreatment, true);
            logger.debug(html);
            return html;
        }
        catch (final MailComposerException exception)
        {
            throw new SWORDException("could not compose deposit treatment", exception);
        }
    }

    public static String composeCollectionTreatment(final EasyUser user) throws SWORDException
    {
        final String collectionTreatment = Context.getCollectionTreatment();
        if (user == null)
            return collectionTreatment;

        final MailComposer mailComposer = new MailComposer(user);
        try
        {
            final String html = mailComposer.compose(collectionTreatment, true);
            logger.debug(html);
            return html;
        }
        catch (final MailComposerException exception)
        {
            throw new SWORDException("could not compose deposit treatment", exception);
        }
    }

    public static String verboseInfo(final EasyUser user, final Dataset dataset)
    {
        /**
         * the example from the specification
         * 
         * <pre>
         * <sword:verboseDescription>
         *   Does collection exist? True.
         *   User authenticates? True.
         *   User: jbloggs
         *   User has rights to collection? True. 
         * </sword:verboseDescription>
         * </pre>
         */
        final StringBuffer sb = new StringBuffer("\n\r");
        final String format = "<p>{0}</p>\n\r";
        sb.append(MessageFormat.format(format, "created dataset: " + dataset.getStoreId()));
        sb.append(MessageFormat.format(format, "dataset owner: " + dataset.getOwnerId()));
        sb.append(MessageFormat.format(format, "confirmation and licence mailed to: " + user.getEmail()));
        sb.append(MessageFormat.format(format, dataset.getEasyMetadata().toString("; ").replaceAll("\n", "</p>\n\r<p>")));
        try
        {
            final List<String> filenames = Data.getFileStoreAccess().getFilenames(dataset.getDmoStoreId(), true);
            sb.append(MessageFormat.format(format, "archived file names: " + Arrays.deepToString(filenames.toArray())));
        }
        catch (final StoreAccessException e)
        {
            sb.append(MessageFormat.format(format, "problem retreiving file names: " + e.getMessage()));
            logger.error("problem retreiving file names of " + dataset.getStoreId(), e);
        }
        return sb.toString();
    }

    static void resetNoOpSubmitCounter()
    {
        // JUnit test execution order is unstable, a constant value makes results stable
        noOpSumbitCounter = 0;
    }

    public static Dataset mockSubmittedDataset(final EasyMetadata metadata, final EasyUser user) throws SWORDErrorException
    {
        ++noOpSumbitCounter;
        final String pid = (noOpSumbitCounter + "xxxxxxxx").replaceAll("(..)(...)(...)", "urn:nbn:nl:ui:$1-$2-$3");
        final String storeId = NO_OP_STORE_ID_DOMAIN + noOpSumbitCounter;
        final DmoStoreId DmoStoreID = new DmoStoreId(storeId);
        final Dataset dataset = EasyMock.createMock(Dataset.class);

        EasyMock.expect(dataset.getEasyMetadata()).andReturn(metadata).anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(storeId).anyTimes();
        EasyMock.expect(dataset.getOwnerId()).andReturn(user.getId()).anyTimes();
        EasyMock.expect(dataset.getDmoStoreId()).andReturn(DmoStoreID).anyTimes();
        EasyMock.expect(dataset.getDepositor()).andReturn(user).anyTimes();
        EasyMock.expect(dataset.getAccessCategory()).andReturn(metadata.getEmdRights().getAccessCategory()).anyTimes();
        EasyMock.expect(dataset.getPreferredTitle()).andReturn(metadata.getPreferredTitle()).anyTimes();
        EasyMock.expect(dataset.getPersistentIdentifier()).andReturn(pid).anyTimes();
        EasyMock.replay(dataset);

        final FormDefinition formDefinition = getFormDefinition(dataset.getEasyMetadata());
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(formDefinition, dataset, user);
        if (!new SubmitNotification(submission).sendMail(false))
            logger.error(storeId + " Mocked submit notification was no sent to " + user.getId());
        return dataset;
    }
}
