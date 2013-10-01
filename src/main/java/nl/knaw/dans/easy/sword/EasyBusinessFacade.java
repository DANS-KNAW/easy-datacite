package nl.knaw.dans.easy.sword;

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
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSpecification;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

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
    private static int noOpSumbitCounter = 0;
    public static final String NO_OP_STORE_ID_DOMAIN = "mockedStoreID:";

    private static Logger logger = LoggerFactory.getLogger(EasyBusinessFacade.class);

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
            throw newSWORDAuthenticationException("no credentials", null);
        final FederativeAuthentication federativeAuthentication = new FederativeAuthentication(userId, password);
        if (!federativeAuthentication.canBeTraditionalAccount())
        {
            final String fedUserId = federativeAuthentication.getUserId();
            if (fedUserId == null)
                throw newSWORDAuthenticationException("invalid credentials", null);
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
            throw newSWORDAuthenticationException(userId + " not authenticed", exception);
        }
        catch (final RepositoryException exception)
        {
            throw newSWORDException((userId + " authentication problem"), exception);
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
            throw newSWORDAuthenticationException(fedUserId + " authentication problem", e);
        }
        catch (final RepositoryException e)
        {
            throw newSWORDException(("Could not get user with fedUserId '" + fedUserId + "' :"), e);
        }

        final String userId = userIdMap.getDansUserId();
        logger.debug("Found easy user for federative user: fedUserId='" + fedUserId + "', userId='" + userId + "'");
        try
        {
            return Data.getUserRepo().findById(userId);
        }
        catch (final ObjectNotInStoreException exception)
        {
            throw newSWORDAuthenticationException(userId + " authentication problem", exception);
        }
        catch (final RepositoryException exception)
        {
            throw newSWORDException((userId + " authentication problem"), exception);
        }
    }

    private static void authenticate(final String userId, final String password) throws SWORDException, SWORDErrorException, SWORDAuthenticationException
    {
        try
        {
            if (userId == null || password == null)
                throw newBadRequestException("missing username [" + userId + "] or password");
            else if (!Data.getUserRepo().authenticate(userId, password))
                throw newSWORDAuthenticationException("invalid username [" + userId + "] or password", null);
            logger.info(userId + " authenticated");
        }
        catch (final RepositoryException exception)
        {
            throw newSWORDException((userId + " authentication problem"), exception);
        }
    }

    /**
     * Submits a new dataset.
     * 
     * @param noOp
     *        no changes in the repository
     * @param user
     *        the owner of the new dataset
     * @param metadata
     *        the metadata for the new dataset,
     * @param folder
     *        a directory containing the files for the new dataset
     * @param files
     *        the list of files in the directory to add to the new dataset
     * @return
     * @throws SWORDException
     * @throws SWORDErrorException
     */
    public static Dataset submitNewDataset(final boolean noOp, final EasyUser user, final EasyMetadata metadata, final File folder, final List<File> files)
            throws SWORDException, SWORDErrorException
    {
        final Dataset mockedDataset = validateSubmission(user, metadata);
        if (noOp)
            return mockedDataset;

        final MetadataFormat mdFormat = metadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        final FormDefinition formDefinition = getFormDefinition(metadata);
        enhanceWithDefaults(metadata);

        // detect as much as possible errors before irreversible creation of the dataset
        // from now on, treat any error as a bad request to return the ID of the created draft dataset

        final Dataset dataset = createEmptyDataset(mdFormat);
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(formDefinition, dataset, user);

        ((DatasetImpl) dataset).setEasyMetadata(metadata);
        dataset.setOwnerId(user.getId());
        dataset.getAdministrativeMetadata().setDepositor(user);

        ingestFiles(submission, folder, files);
        submit(submission);

        return dataset;
    }

    /**
     * Enhances the custom metadata with defaults present in the metadata of the dataset.
     * 
     * @param metadata
     *        the custom metadata
     * @return the custom metadata
     */
    private static EasyMetadata enhanceWithDefaults(final EasyMetadata metadata)
    {
        final List<BasicString> audienceList = metadata.getEmdAudience().getTermsAudience();
        if (!audienceList.isEmpty())
        {
            metadata.getEmdAudience().getTermsAudience().add(audienceList.get(0));
        }
        return metadata;
    }

    /** Wraps exceptions thrown by Services.getDatasetService().submitDataset */
    private static void submit(final DatasetSubmissionImpl submission) throws SWORDException, SWORDErrorException
    {
        final Dataset dataset = submission.getDataset();
        final EasyUser user = submission.getSessionUser();
        final String msg = dataset.getStoreId() + " is created with draft status, submission failed.";
        logger.info("submitting " + dataset.getStoreId() + " " + user.getId());
        try
        {
            Services.getDatasetService().submitDataset(submission);
        }
        catch (final ServiceException exception)
        {
            throw newSWORDException(msg, exception);
        }
        catch (final DataIntegrityException exception)
        {
            throw newSWORDException(msg, exception);
        }
        if (!submission.isSubmitted())
            throw newSWORDException(msg, null);
        if (!submission.isMailSend())
            logger.error("Submission OK but no confirmation mail sent for " + dataset.getStoreId() + " " + user.getId());
    }

    /** Wraps exceptions thrown by Services.getItemService().addDirectoryContents() */
    private static void ingestFiles(final DatasetSubmission submission, final File directory, final List<File> fileList) throws SWORDException,
            SWORDErrorException
    {
        final Dataset dataset = submission.getDataset();
        final EasyUser user = submission.getSessionUser();
        final DmoStoreId dmoStoreId = dataset.getDmoStoreId();
        final IngestReporter ingestReporter = new IngestReporter();
        logIngest(directory, fileList, dmoStoreId);
        String message = "Problem with ingesting files. " + dmoStoreId + " might be created with a draft state.";
        try
        {
            Services.getItemService().addDirectoryContents(user, dataset, dmoStoreId, directory, new ItemIngester(dataset), ingestReporter);
        }
        catch (final ServiceException exception)
        {
            throw newSWORDException(message, exception);
        }
        if (ingestReporter.catchedExceptions())
            throw newSWORDException(message, null);
    }

    private static void logIngest(final File directory, final List<File> fileList, final DmoStoreId dmoStoreId)
    {
        /*
         * ArraysdeepToString would cause a too long line which gets wrapped on a single line in the
         * eclipse console
         */
        final StringBuffer sb = new StringBuffer();
        for (final File file : fileList)
            sb.append("\n\t" + file);
        final String string = sb.toString();
        logger.debug("ingesting files from " + directory + " into " + dmoStoreId + " " + string);
    }

    public static String formatAudience(final EasyMetadata metadata) throws SWORDException
    {
        final String msg = "can't get audience description ";
        final DisciplineCollectionService disciplineService = Services.getDisciplineService();
        if (disciplineService == null)
            throw newSWORDException(msg, null);
        final StringBuffer string = new StringBuffer();
        for (final String sid : metadata.getEmdAudience().getValues())
        {
            string.append(", ");
            try
            {
                string.append(disciplineService.getDisciplineById(new DmoStoreId(sid)).getName());
            }
            catch (final ObjectNotFoundException e)
            {
                throw newSWORDException(msg, e);
            }
            catch (final ServiceException e)
            {
                throw newSWORDException(msg, e);
            }
        }
        return string.substring(2);
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
            throw newSWORDException("Can't create a new dataset with metadataFormat " + metadataFormat, exception);
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
            throw newSWORDException("could not compose deposit treatment", exception);
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
            throw newSWORDException("could not compose deposit treatment", exception);
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

    public static void resetNoOpSubmitCounter()
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
        final AdministrativeMetadataImpl administrativeMetadata = new AdministrativeMetadataImpl()
        {
            private static final long serialVersionUID = 1L;
        };
        administrativeMetadata.setDepositor(user);

        EasyMock.expect(dataset.getEasyMetadata()).andReturn(metadata).anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(storeId).anyTimes();
        EasyMock.expect(dataset.getOwnerId()).andReturn(user.getId()).anyTimes();
        EasyMock.expect(dataset.getDmoStoreId()).andReturn(DmoStoreID).anyTimes();
        EasyMock.expect(dataset.getDepositor()).andReturn(user).anyTimes();
        EasyMock.expect(dataset.getAccessCategory()).andReturn(metadata.getEmdRights().getAccessCategory()).anyTimes();
        EasyMock.expect(dataset.getPreferredTitle()).andReturn(metadata.getPreferredTitle()).anyTimes();
        EasyMock.expect(dataset.getPersistentIdentifier()).andReturn(pid).anyTimes();
        EasyMock.expect(dataset.getMetadataFormat()).andReturn(metadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat()).anyTimes();
        EasyMock.expect(dataset.getAdministrativeMetadata()).andReturn(administrativeMetadata).anyTimes();
        EasyMock.replay(dataset);
        return dataset;
    }

    /** validates at least the disciplineID */
    public static Dataset validateSubmission(final EasyUser depositor, final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
        final Dataset dataset = mockSubmittedDataset(metadata, depositor);
        try
        {
            DatasetSpecification.evaluate(dataset);
            return dataset;
        }
        catch (final DataIntegrityException exception)
        {
            throw newBadRequestException(Arrays.deepToString(exception.getErrorMessages().toArray()));
        }
        catch (final ServiceException exception)
        {
            throw newSWORDException("could not validate metadata", exception);
        }
    }

    public static FormDefinition getFormDefinition(final EasyMetadata emd) throws SWORDErrorException, SWORDException
    {
        final MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        if (mdFormat == null)
            throw newBadRequestException("meta data format not specified.");
        final DepositDiscipline discipline;
        try
        {
            discipline = Services.getDepositService().getDiscipline(mdFormat);
        }
        catch (final ServiceException e)
        {
            throw newSWORDException("Cannot get deposit discipline.", e);
        }
        if (discipline == null)
            throw newBadRequestException("Cannot get deposit discipline.");
        final FormDefinition formDefinition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
        if (formDefinition == null)
            throw newBadRequestException("Cannot get formdefinition for MetadataFormat " + mdFormat.toString());
        return formDefinition;
    }

    private static SWORDErrorException newBadRequestException(String message)
    {
        logger.error(message);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
    }

    private static SWORDAuthenticationException newSWORDAuthenticationException(final String message, final Exception cause)
    {
        logger.error(message, cause);
        return new SWORDAuthenticationException(message, cause);
    }

    private static SWORDException newSWORDException(final String message, final Exception cause)
    {
        logger.error(message, cause);
        return new SWORDException(message, cause);
    }
}
