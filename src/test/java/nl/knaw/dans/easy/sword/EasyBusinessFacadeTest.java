package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

public class EasyBusinessFacadeTest extends Tester
{
    static File       tempDirectory;

    @Before
    public void setupMocking() throws Exception
    {
        MockUtil.mockAll();
    }

    @BeforeClass
    public static void createTempDir() throws Exception
    {
        final File basePath = new File("target/tmp");
        basePath.mkdirs();
        tempDirectory = FileUtil.createTempDirectory(basePath, "unzip");
    }

    @AfterClass
    public static void clearTempDir() throws Exception
    {
        // TODO clear target/tmp/unzip;
    }

    private static void executeSubmit(final File zipFile, final File metaDataFile, final Class<? extends Exception> expectedCause) throws Exception
    {
        final List<File> fileList = new UnzipUtil(zipFile, tempDirectory.getPath(), createUnzipListener()).run();
        try
        {
            final EasyMetadata easyMetaData = EasyMetadataFacade.validate(FileUtil.readFile(metaDataFile));
            EasyBusinessFacade.submitNewDataset(MockUtil.USER, easyMetaData, tempDirectory, fileList);
        }
        catch (final SWORDException se)
        {
            if (expectedCause == null)
                throw se;
            if (se.getCause()==null||!(se.getCause().getClass().equals(expectedCause)))
                throw se;
        }
        if (expectedCause != null)
            throw new Exception("got no exception but expected "+expectedCause.getName());
    }

    @Test
    public void submit() throws Exception
    {
        executeSubmit(ZIP_FILE, META_DATA_FILE, null);
    }

    @Test (expected=SWORDErrorException.class)
    public void invalidMetadataByMM() throws Throwable
    {
        final File zipFile = new File("src/test/resources/input/invalidMetadata.zip");
        final File metaDataFile = new File(tempDirectory + "/easyMetadata.xml");
        executeSubmit(zipFile, metaDataFile, SWORDErrorException.class);
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
            }

            @Override
            public void onUnzipComplete(final List<File> files, final boolean canceled)
            {
            }
        };
        return unzipListener;
    }
}
