package nl.knaw.dans.common.lang.file;

import java.io.File;
import java.util.List;

public interface UnzipListener
{

    void onUnzipStarted(long totalBytes);

    /* a two way upadte:
     * 1. Send an unzip progress update to the implementor via the parameters
     * 2. Returns to the unzipper class if the process should continue
     * @Returns true for continuing the unzipping process and false for stopping it
     */
    boolean onUnzipUpdate(long bytesUnzipped, long total);

    void onUnzipComplete(List<File> files, boolean canceled);
}
