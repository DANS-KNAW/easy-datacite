package nl.dans.knaw.easy.mock.demo;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.mock.DatasetMocker;
import nl.knaw.dans.easy.mock.FileMocker;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ExampleTest {
    /** In case your project depends on easy-fedora: use for example DobState.Active.toString() */
    private static final String SOME_STATE = "SomeState";

    private static final DateTime BASE_DATE_TIME = new DateTime("2000-01-01T00:00:00");

    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception {
        mock = new BusinessMocker();
    }

    @After
    public void verifyAll() {
        PowerMock.verifyAll();
    }

    /**
     * Demonstrates mocking a complex dataset that changes during the test. The "with" methods of the mockers create default behavior with stubs. These stubs
     * are a fall back and used when the regular expectations are exhausted. Stubs are not verified. The order of {@link DatasetMocker#with(FileMocker...)} and
     * {@link DatasetMocker#expectGetDatasetFilesOnce(FileMocker...)} does not seem to be relevant.
     * 
     * @see also <a href="http://stackoverflow.com/questions/3740376/easymock-andreturn-vs-andstubreturn"> stackoverflow</>
     */
    @Test
    public void purge() throws Exception {
        final String datasetStoreId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.user("archivist");
        mock.dataset(datasetStoreId)//
                .withPid("urn:nbn:nl:ui:13-2g23-6f")//
                .with(DatasetState.MAINTENANCE, SOME_STATE)//
                .withAipId("twips-1")//
                .expectGetDatasetFilesOnce//
                (//
                mock.file("original/tiff/my.gif")//
                        .with(AccessibleTo.NONE, VisibleTo.NONE), //
                mock.file("tif/2.tif")//
                        .with(AccessibleTo.NONE, VisibleTo.NONE)//
                        .expectPurgeAt(BASE_DATE_TIME.plusMillis(1)), //
                        mock.file("1.png")//
                                .with(AccessibleTo.NONE, VisibleTo.NONE), //
                        mock.file("tif/1.gif")//
                                .with(AccessibleTo.ANONYMOUS, VisibleTo.NONE)//
                )//
                .with//
                (//
                mock.file("original/tiff/my.gif")//
                        .with(AccessibleTo.NONE, VisibleTo.NONE), //
                mock.file("1.png")//
                        .with(AccessibleTo.NONE, VisibleTo.NONE), //
                        mock.file("tif/1.gif")//
                                .with(AccessibleTo.ANONYMOUS, VisibleTo.NONE)//
                );

        PowerMock.replayAll();

        // purge files with extension tif in a folder named tif or tiff
        final String cleanUpPattern = "(.*/)?tiff?/[^/]*[.]tif";

        // exhausts the explicit expectations
        ClassUnderTest.cleanUp(datasetStoreId, cleanUpPattern);

        // falls back to the stubs
        ClassUnderTest.cleanUp(datasetStoreId, cleanUpPattern);
    }

    /** Demonstrates mocking object content with a test input file. */
    @Test
    public void openDatasetFile() throws Exception {
        final String userId = "archivist";
        final String datasetStoreId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        final String fileStoreId = mock.nextDmoStoreId(FileItem.NAMESPACE);

        final String path = "sample.txt";
        final String mockedFilesDir = "/src/test/resources/input/";
        final URL mockedContentUrl = new URL("file://" + new File(".").getAbsolutePath() + mockedFilesDir + path);

        mock.user(userId);
        mock.dataset(datasetStoreId).withPid("urn:nbn:nl:ui:13-2g23-6f")//
                .with//
                (//
                mock.file(path, fileStoreId)//
                        .with(mockedContentUrl)//
                        .with(AccessibleTo.ANONYMOUS, VisibleTo.ANONYMOUS)//
                );

        PowerMock.replayAll();

        final String content = ClassUnderTest.readFile(userId, datasetStoreId, fileStoreId);
        assertThat(content, equalTo("hello world"));
    }

    @Test
    public void emptyFolder() throws Exception {
        final String storeId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.dataset(storeId).with(mock.folder("a")).with(mock.file("b.txt"));

        PowerMock.replayAll();

        assertThat(ClassUnderTest.getNrOfFilesAndFolders(storeId), equalTo(2));
    }

    @Test
    public void justAnEmptyFolder() throws Exception {
        final String storeId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.dataset(storeId).with(mock.folder("a")).withoutFiles();

        PowerMock.replayAll();

        assertThat(ClassUnderTest.getNrOfFilesAndFolders(storeId), equalTo(1));
    }
}