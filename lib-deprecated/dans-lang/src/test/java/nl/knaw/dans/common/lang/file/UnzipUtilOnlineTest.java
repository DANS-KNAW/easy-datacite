package nl.knaw.dans.common.lang.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnzipUtilOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(UnzipUtilOnlineTest.class);

    // change paths for local testing.
    private String zipfilePath = "/mnt/hgfs/ecco/Public.zip";
    private String destinationPath = "/mnt/hgfs/ecco/Public/test";

    @Ignore("local path")
    @Test
    public void unzip() throws Exception
    {
        List<File> unzippedFiles = UnzipUtil.unzip(new File(zipfilePath), destinationPath, new UnzipListener()
        {

            @Override
            public boolean onUnzipUpdate(long bytesUnzipped, long total)
            {
                logger.debug("bytesUnzipped=" + bytesUnzipped + " total=" + total);
                return true;
            }

            @Override
            public void onUnzipStarted(long totalBytes)
            {
                logger.debug("started total=" + totalBytes);

            }

            @Override
            public void onUnzipComplete(List<File> files, boolean canceled)
            {
                logger.debug("complete. canceled=" + canceled);

            }
        });

        for (File file : unzippedFiles)
        {
            assertTrue(file.exists());
        }

    }

}
