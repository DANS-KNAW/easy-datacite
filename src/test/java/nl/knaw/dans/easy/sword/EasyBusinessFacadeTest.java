package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyBusinessFacadeTest extends Tester
// composeLicense and verbose submissions are integration tests and do not belong in this test class
{
    private static final Logger log = LoggerFactory.getLogger(EasyBusinessFacadeTest.class);

    final static File basePath = new File("target/tmp");
    static File       tempDirectory;

    @BeforeClass
    public static void setupMocking() throws Exception
    {
        MockUtil.mockAll();
    }

    @BeforeClass
    public static void createTempDir() throws Exception
    {
        basePath.mkdirs();
        tempDirectory = FileUtil.createTempDirectory(basePath, "unzip");
    }

    private static void executeSubmit(final File zipFile, final File metaDataFile, final Class<? extends Exception> expectedCause) throws Exception
    {
        final List<File> fileList = new UnzipUtil(zipFile, tempDirectory.getPath(), createUnzipListener()).run();
        try
        {
            final EasyMetadata easyMetaData = EasyBusinessFacade.unmarshallEasyMetaData(FileUtil.readFile(metaDataFile));
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
            throw new Exception("expected "+expectedCause.getName());
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
