package nl.knaw.dans.easy.business.dataset;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;

public final class MailSender implements SubmissionProcessor
{
    public static final String       MSG_MAIL_ERROR     = "deposit.mail_error";

    public boolean continueAfterFailure()
    {
        return false;
    }

    public boolean process(final DatasetSubmissionImpl submission)
    {
        // Don't send a license when the AccessCategory = NO_ACCESS
        boolean attachLicense = !AccessCategory.NO_ACCESS.equals(submission.getDataset().getAccessCategory());
        final boolean success = new SubmitNotification(submission).sendMail(attachLicense);
        if (!success)
            submission.addGlobalErrorMessage(MailSender.MSG_MAIL_ERROR);
        submission.setMailSend(success);
        return success;
    }
}