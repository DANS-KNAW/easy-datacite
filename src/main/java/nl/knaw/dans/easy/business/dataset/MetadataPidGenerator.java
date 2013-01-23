package nl.knaw.dans.easy.business.dataset;

import java.net.URI;

import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataPidGenerator implements SubmissionProcessor
{
    public static final String PID_ERROR = "deposit.pid_error";
    private static final Logger logger = LoggerFactory.getLogger(DatasetIngester.class);

    public boolean continueAfterFailure()
    {
        return false;
    }

    public boolean process(final DatasetSubmissionImpl submission)
    {
        boolean processed = false;
        String pid = submission.getDataset().getPersistentIdentifier();
        if (pid == null)
        {
            processed = generatePid(submission);
        }
        else
        {
            processed = true;
            logger.debug("Not generating new Pid. Pid already assigned. pid=" + pid);
        }
        return processed;
    }

    private boolean generatePid(final DatasetSubmissionImpl submission)
    {
        try
        {
            BasicIdentifier id = new BasicIdentifier();
            String pid = ExternalServices.getPidGenerator().getNextPersistentIdentifierUrn();
            id.setValue(pid);
            id.setIdentificationSystem(URI.create(EmdConstants.BRI_RESOLVER));
            id.setScheme(EmdConstants.SCHEME_PID);
            submission.getDataset().getEasyMetadata().getEmdIdentifier().getDcIdentifier().add(id);
            ((DatasetRelations) submission.getDataset().getRelations()).setPersistentIdentifier(pid);
            logger.debug("Generated new Pid. pid=" + pid);
        }
        // TODO: catch all prevents normal error handling for all callers. 
        catch (final Exception exception)
        {
            return reportError("Can't generate persistent identifier", submission, exception);
        }
        return true;
    }

    private boolean reportError(final String string, final DatasetSubmissionImpl submission, final Exception exception)
    {
        submission.getGlobalErrorMessages().add(PID_ERROR);
        logger.error(string + " for " + submission.getDatasetId(), exception);
        return false;
    }

}
