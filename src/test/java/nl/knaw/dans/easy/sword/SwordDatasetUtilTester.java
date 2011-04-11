package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.SWORDException;

public class SwordDatasetUtilTester extends Tester
{
    final static File         basePath       = new File("target/tmp");
    static File               tempDirectory;
    static byte[]             easyMetaData;
    static List<File>         fileList;

    @BeforeClass
    public static void setupMocking() throws Exception {
        new MockUtil().mockAll();
    }

    @BeforeClass
    public static void createParameters() throws Exception
    {
        basePath.mkdirs();
        tempDirectory = FileUtil.createTempDirectory(basePath, "unzip");
        fileList = new UnzipUtil(ZIP_FILE, tempDirectory.getPath(), createUnzipListener()).run();
        easyMetaData = FileUtil.readFile(META_DATA_FILE);
    }

    @Test
    public void publish() throws Exception
    {
        SwordDatasetUtil.publishNewDataset(MockUtil.ARCHIV_USER_ID, easyMetaData, tempDirectory, fileList);
    }

    @Test(expected = SWORDException.class)
    public void publishWithoutPermission() throws Exception
    {
        SwordDatasetUtil.publishNewDataset(MockUtil.VALID_USER_ID, easyMetaData, tempDirectory, fileList);
    }

    @Test
    public void submit() throws Exception
    {
        SwordDatasetUtil.submitNewDataset(MockUtil.VALID_USER_ID, easyMetaData, tempDirectory, fileList);
    }

    @Ignore("adjust mocks") // TODO
    @Test(expected = SWORDException.class)
    public void anonymousSubmit() throws Exception
    {
        SwordDatasetUtil.submitNewDataset(MockUtil.INVALID_USER_ID, easyMetaData, tempDirectory, fileList);
    }

    private static UnzipListener createUnzipListener()
    {
        final UnzipListener unzipListener = new UnzipListener()
        {

            @Override
            public boolean onUnzipUpdate(final long bytesUnzipped, final long total)
            {
                return true; // continue unzip
            }

            @Override
            public void onUnzipStarted(final long totalBytes)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void onUnzipComplete(final List<File> files, final boolean canceled)
            {
            }
        };
        return unzipListener;
    }
}
