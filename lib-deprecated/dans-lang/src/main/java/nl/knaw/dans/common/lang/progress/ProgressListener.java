package nl.knaw.dans.common.lang.progress;

public interface ProgressListener
{

    void onStartProcess(String processId);

    void updateProgress(int percentage);

    void onEndProcess(String processId);

}
