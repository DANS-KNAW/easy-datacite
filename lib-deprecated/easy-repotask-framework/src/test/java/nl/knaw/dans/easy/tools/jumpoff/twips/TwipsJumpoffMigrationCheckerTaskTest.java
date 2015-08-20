package nl.knaw.dans.easy.tools.jumpoff.twips;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.JointMap;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Data.class, RL.class})
public class TwipsJumpoffMigrationCheckerTaskTest {
    private MigrationRepo migrationRepoMock;
    private TwipsJumpoffMigrationCheckerTask taskMock;
    private TwipsJumpoffMigrationCheckerTask task;
    private static final String TWIPS_ROOT_PATH = "src/test/resources/test-files/migration";
    private JointMap jointMap;

    @Before
    public void setUp() {
        taskMock = createMock(TwipsJumpoffMigrationCheckerTask.class, TWIPS_ROOT_PATH);
        task = new TwipsJumpoffMigrationCheckerTask(TWIPS_ROOT_PATH);
        jointMap = new JointMap();
        mockStatic(RL.class);
        mockStatic(Data.class);

    }

    @Test
    public void testConstructor() throws Exception {
        // Testing the constructor argument whether right or not.
        assertEquals(TWIPS_ROOT_PATH, Whitebox.getInternalState(taskMock, "repositoryRoot", TwipsJumpoffMigrationCheckerTask.class));
    }

    @Test
    public void testTask() throws Exception {
        IdMap idMapMock = createMock(IdMap.class);
        expect(idMapMock.getPersistentIdentifier()).andReturn("urn:nbn:nl:ui:13-1234").anyTimes();
        expect(idMapMock.getAipId()).andStubReturn("twips.dans.knaw.nl-6731496536029815457-1248074830293");
        expect(idMapMock.getStoreId()).andStubReturn("easy-dataset:1");

        List<IdMap> idMapList = new ArrayList<IdMap>();
        idMapList.add(idMapMock);

        migrationRepoMock = PowerMock.createMock(MigrationRepo.class);
        expect(Data.getMigrationRepo()).andStubReturn(migrationRepoMock);

        EasyStore easyStoreMock = PowerMock.createMock(EasyStore.class);
        expect(Data.getEasyStore()).andStubReturn(easyStoreMock);

        expect(migrationRepoMock.findByAipId("twips.dans.knaw.nl-687128515135706527-1272979186597")).andStubReturn(idMapList);
        expect(migrationRepoMock.findByPersistentIdentifier("urn:nbn:nl:ui:13-1234")).andStubReturn(idMapList);
        JumpoffDmo overviewJumpoffMock = PowerMock.createMock(JumpoffDmo.class);
        expect(overviewJumpoffMock.getStoreId()).andStubReturn(TwipsJumpoffMigrationCheckerTask.ROOT_OVERVIEW_DATASET);
        expect(easyStoreMock.findJumpoffDmoFor(new DmoStoreId(TwipsJumpoffMigrationCheckerTask.ROOT_OVERVIEW_DATASET))).andStubReturn(overviewJumpoffMock);
        final Capture<Event> event = new Capture<Event>();
        RL.error(capture(event));
        expectLastCall().anyTimes();
        replayAll();
    }
}
