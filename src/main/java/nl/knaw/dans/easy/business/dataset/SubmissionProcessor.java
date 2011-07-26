package nl.knaw.dans.easy.business.dataset;


public interface SubmissionProcessor
{

    boolean process(DatasetSubmissionImpl submission);

    boolean continueAfterFailure();

}
