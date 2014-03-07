package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;

public final class SubmitNotification extends DatasetNotification implements Serializable
{
    private static final long serialVersionUID = 1L;

    private AccessCategory access;

    public SubmitNotification(DatasetSubmissionImpl submission)
    {
        super(submission.getDataset(), submission);
        access = submission.getDataset().getAccessCategory();
    }

    String getTemplateLocation()
    {
        // Send other email when 'other access' is selected for the dataset
        if (AccessCategory.NO_ACCESS.equals(access))
        {
            return "deposit/depositConfirmationOtherAccess";
        }
        else
        {
            return "deposit/depositConfirmation";
        }
    }
}
