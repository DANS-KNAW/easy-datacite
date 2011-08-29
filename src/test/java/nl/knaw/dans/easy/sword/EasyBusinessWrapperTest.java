package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.SWORDException;

public class EasyBusinessWrapperTest extends Tester
{
    final static File basePath = new File("target/tmp");
    static File       tempDirectory;

    @BeforeClass
    public static void setupMocking() throws Exception
    {
        new MockUtil().mockAll();
    }

    @BeforeClass
    public static void createTempDir() throws Exception
    {
        basePath.mkdirs();
        tempDirectory = FileUtil.createTempDirectory(basePath, "unzip");
    }

    private static void execute(final File zipFile, final File metaDataFile, final Class<? extends Exception> expectedCause) throws Exception
    {
        final List<File> fileList = new UnzipUtil(zipFile, tempDirectory.getPath(), createUnzipListener()).run();
        try
        {
            final EasyMetadata easyMetaData = EasyBusinessWrapper.unmarshallEasyMetaData(FileUtil.readFile(metaDataFile));
            EasyBusinessWrapper.submitNewDataset(MockUtil.USER, easyMetaData, tempDirectory, fileList);
        }
        catch (final SWORDException se)
        {
            if (expectedCause == null)
                throw se;
            if (!(se.getCause().getClass().equals(expectedCause)))
                throw se;
        }
    }

    @Ignore("WorkReporter.workStart and .workEnd not called by mocked itemService.addDirectoryContents" /* FIXME */) 
    @Test
    public void submit() throws Exception
    {
        execute(ZIP_FILE, META_DATA_FILE, null);
    }

    @Test
    public void invalidMetadataByMM() throws Throwable
    {
        final File zipFile = new File("src/test/resources/input/invalidMetadata.zip");
        final File metaDataFile = new File(tempDirectory + "/easyMetadata.xml");
        execute(zipFile, metaDataFile, XMLDeserializationException.class);
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
