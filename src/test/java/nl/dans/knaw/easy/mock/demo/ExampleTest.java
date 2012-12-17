package nl.dans.knaw.easy.mock.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.mock.DatasetHelper;
import nl.knaw.dans.easy.mock.FileHelper;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ExampleTest
{
    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception
    {
        mock = new BusinessMocker();
    }

    @After
    public void verifyAll()
    {
        mock.verifyAll();
    }

    /**
     * Demonstrates mocking a complex dataset that changes during the test. Without the change during the
     * test you might use {@link DatasetHelper#with(FileHelper...)} which implements more expectations
     * than {@link DatasetHelper#expectGetDatasetFilesOnce(FileHelper...).
     */
    @Test
    public void purgeTiff() throws Exception
    {
        final String datasetId = "easy-dataset:0";
        mock.user("archivist");
        mock.dataset("easy-dataset:0").withPid("urn:nbn:nl:ui:13-2g23-6f")//
                .expectGetDatasetFilesOnce(//
                        mock.file("original/tiff/my.gif").forNone(), //
                        mock.file("tif/2.tif").forNone().expectPurgeAt(new DateTime("2000-01-01T00:00:00").plus(1)), //
                        mock.file("1.png").forNone(), //
                        mock.file("tif/1.gif").with(AccessibleTo.ANONYMOUS).with(VisibleTo.NONE))//
                .expectGetDatasetFilesOnce(// same files for the second test call except for the purged file
                        mock.file("original/tiff/my.gif").forNone(), //
                        mock.file("1.png").forNone(), //
                        mock.file("tif/1.gif").with(AccessibleTo.ANONYMOUS).with(VisibleTo.NONE));

        mock.replayAll();

        ClassUnderTest.purgeTiffFiles(datasetId);
        ClassUnderTest.purgeTiffFiles(datasetId);
    }

    /**
     * Demonstrates mocking object content with a test input file.<br>
     * Note: the mocked file does not exist, the message of the exception shows the location where it
     * should have been for a real test.
     */
    @Test(expected = FileNotFoundException.class)
    public void openDatasetFile() throws Exception
    {
        final String userId = "archivist";
        final String datasetStoreId = "easy-dataset:6";
        final String mockedFile = "sample.txt";

        final String mockedFilesDir = "/src/test/resources/input/";
        final URL url = new URL("file://" + new File(".").getAbsolutePath() + mockedFilesDir + mockedFile);

        final FileHelper fileToRead = mock.file(mockedFile).with(url).with(AccessibleTo.ANONYMOUS).with(VisibleTo.ANONYMOUS);
        final String fileStoreId = fileToRead.getStoreId();
        mock.user(userId);
        mock.dataset(datasetStoreId).withPid("urn:nbn:nl:ui:13-2g23-6f").with(fileToRead);

        mock.replayAll();

        ClassUnderTest.openFile(userId, datasetStoreId, fileStoreId);
    }
}
