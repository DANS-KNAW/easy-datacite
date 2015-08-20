package nl.knaw.dans.easy.tools.task;

import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.util.RepoUtil;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RepoUtil.class})
public class PurgeDeletedDatasetTaskTest {
    private static final DateTime DATE_TIME = new DateTime("2000-01-01T00:00:00");
    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception {
        mock = new BusinessMocker();
    }

    @After
    public void verifyAll() {
        PowerMock.verifyAll();
    }

    @Before
    public void expectectCheckSearchEngine() throws NoListenerException {
        mockStatic(RepoUtil.class);
        RepoUtil.checkListenersActive();
        expectLastCall().anyTimes();
    }

    @Test
    public void oneRedundantDataset() throws Exception {
        mock.dataset("easy-dataset:1").withAipId("twips-1").with(DatasetState.PUBLISHED, DobState.Active.toString());
        mock.dataset("easy-dataset:2").withAipId("twips-2").with(DatasetState.PUBLISHED, DobState.Active.toString());
        mock.dataset("easy-dataset:3").withAipId("twips-2").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(1));
        mock.dataset("easy-dataset:4").withAipId("twips-3").with(DatasetState.PUBLISHED, DobState.Active.toString());

        runTaskOnDatasets();
    }

    @Test
    public void twoRedundantDatasets() throws Exception {
        mock.dataset("easy-dataset:5").withAipId("twips-1").with(DatasetState.PUBLISHED, DobState.Active.toString());
        mock.dataset("easy-dataset:6").withAipId("twips-2").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(2));
        mock.dataset("easy-dataset:7").withAipId("twips-2").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(3));
        mock.dataset("easy-dataset:8").withAipId("twips-2").with(DatasetState.PUBLISHED, DobState.Active.toString());
        mock.dataset("easy-dataset:9").withAipId("twips-2").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(4));
        mock.dataset("easy-dataset:10").withAipId("twips-3").with(DatasetState.DELETED, DobState.Deleted.toString());
        runTaskOnDatasets();
    }

    @Test
    public void redundantLast() throws Exception {
        mock.dataset("easy-dataset:101").withAipId("twips-3").with(DatasetState.DELETED, DobState.Deleted.toString());
        mock.dataset("easy-dataset:102").withAipId("twips-4").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(5));
        mock.dataset("easy-dataset:103").withAipId("twips-4").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(6));
        runTaskOnDatasets();
    }

    @Test
    public void redundantFirst() throws Exception {
        mock.dataset("easy-dataset:201").withAipId("twips-4").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(5));
        mock.dataset("easy-dataset:202").withAipId("twips-4").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(6));
        mock.dataset("easy-dataset:203").withAipId("twips-3").with(DatasetState.DELETED, DobState.Deleted.toString());
        runTaskOnDatasets();
    }

    @Test
    public void notInteractive() throws Exception {
        mock.dataset("easy-dataset:15").withAipId("twips-2").with(DatasetState.DELETED, DobState.Deleted.toString())
                .expectMigrationPurgeAt(DATE_TIME.plusMillis(8));
        mock.dataset("easy-dataset:16").withAipId("twips-2").with(DatasetState.DELETED, DobState.Active.toString());
        // no longer a "y" or "n" required as inconsistent delete states are no longer purged
        runInteractive("");
    }

    private void runInteractive(final String string) throws UnsupportedEncodingException, Exception {
        final InputStream savedIn = System.in;
        System.setIn(new ByteArrayInputStream(string.getBytes("UTF-8")));
        runTaskOnDatasets();
        System.setIn(savedIn);
    }

    private void runTaskOnDatasets() throws Exception {
        PowerMock.replayAll();
        final PurgeDeletedDatasetTask task = new PurgeDeletedDatasetTask();
        task.setTest(false);
        for (final Dataset mockedDataset : mock.getDatasets()) {
            final JointMap jointMap = new JointMap();
            jointMap.setDataset(mockedDataset);
            task.run(jointMap);
        }
        task.close();
    }
}
