package nl.knaw.dans.easy.business.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class DatasetSubmissionImpl implements DatasetSubmission
{

    private static final long    serialVersionUID    = 8588377037969121311L;

    private final FormDefinition formDefinition;
    private final Dataset        dataset;
    private final EasyUser           sessionUser;


    private List<String>         globalErrorMessages = new ArrayList<String>();
    private List<String>         globalInfoMessages  = new ArrayList<String>();

    private boolean              metadataValid;
    private boolean              submitted;
    private boolean              mailSend;
    private boolean              completed;

    public DatasetSubmissionImpl(FormDefinition formDefinition, Dataset dataset, EasyUser sessionUser)
    {
        this.dataset = dataset;
        this.formDefinition = formDefinition;
        this.sessionUser = sessionUser;
    }

    /**
     * Construct a shallow copy for (limited) threaded use.
     *
     * @param submission
     *        submission to copy
     */
    protected DatasetSubmissionImpl(DatasetSubmissionImpl submission)
    {
        this.dataset = submission.dataset;
        this.sessionUser = submission.sessionUser;
        this.formDefinition = null;
        this.globalErrorMessages.addAll(submission.globalErrorMessages);
        this.globalInfoMessages.addAll(submission.globalInfoMessages);
        this.metadataValid = submission.metadataValid;
        this.submitted = submission.submitted;
        this.mailSend  = submission.mailSend;
        this.completed = submission.completed;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#clearAllMessages()
     */
    public void clearAllMessages()
    {
        clearMetadataErrors();
        globalErrorMessages.clear();
        globalInfoMessages.clear();
    }

    protected boolean isMetadataValid()
    {
        return metadataValid;
    }

    protected void setMetadataValid(boolean metadataValid)
    {
        this.metadataValid = metadataValid;
    }

    public boolean isMailSend()
    {
        return mailSend;
    }

    protected void setMailSend(boolean mailSend)
    {
        this.mailSend = mailSend;
    }

    protected boolean isSubmitted()
    {
        return submitted;
    }

    protected void setSubmitted(boolean submitted)
    {
        this.submitted = submitted;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#isCompleted()
     */
    public boolean isCompleted()
    {
        return completed;
    }

    protected void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    protected String getDatasetId()
    {
        return dataset.getStoreId();
    }

    protected FormDefinition getFormDefinition()
    {
        return formDefinition;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getDataset()
     */
    public Dataset getDataset()
    {
        return dataset;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getSessionUser()
     */
    public EasyUser getSessionUser()
    {
        return sessionUser;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#hasMetadataErrors()
     */
    public boolean hasMetadataErrors()
    {
        return getFirstErrorPage() != null;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getFirstErrorPage()
     */
    public FormPage getFirstErrorPage()
    {
        FormPage firstErrorPage = null;
        if (formDefinition != null)
        {
            for (FormPage formPage : formDefinition.getFormPages())
            {
                if (formPage.hasErrors())
                {
                    firstErrorPage = formPage;
                    break;
                }
            }
        }
        return firstErrorPage;
    }

    protected void clearMetadataErrors()
    {
        if (formDefinition != null)
        {
            for (FormPage formPage : formDefinition.getFormPages())
            {
                formPage.clearErrorMessages();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getGlobalErrorMessages()
     */
    public List<String> getGlobalErrorMessages()
    {
        return globalErrorMessages;
    }

    protected void addGlobalErrorMessage(String msgKey)
    {
        globalErrorMessages.add(msgKey);
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getGlobalInfoMessages()
     */
    public List<String> getGlobalInfoMessages()
    {
        return globalInfoMessages;
    }

    protected void addGlobalInfoMessage(String msgKey)
    {
        globalInfoMessages.add(msgKey);
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#hasGlobalMessages()
     */
    public boolean hasGlobalMessages()
    {
        return !globalErrorMessages.isEmpty() || !globalInfoMessages.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getDatasetTitle()
     */
    public String getDatasetTitle()
    {
        return getDataset().getPreferredTitle();
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.dataset.DatasetSubmission#getState()
     */
    public String getState()
    {
        StringBuilder sb = new StringBuilder("Dataset submission");
        sb.append(" [datasetId=");
        sb.append(getDatasetId());
        sb.append("] [metadataValid=");
        sb.append(metadataValid);
        sb.append("] [submitted=");
        sb.append(submitted);
        sb.append("] [mailSend=");
        sb.append(mailSend);
        sb.append("] [completed=");
        sb.append(completed);
        sb.append("]");

        return sb.toString();
    }
    
    public String getPersistentIdentifier() {
        return getDataset().getPersistentIdentifier();
    }
}
