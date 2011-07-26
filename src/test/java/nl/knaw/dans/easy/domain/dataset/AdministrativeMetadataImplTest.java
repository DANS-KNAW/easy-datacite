package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class AdministrativeMetadataImplTest
{
    private static EasyUserRepo userRepo;
    private boolean         verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
        userRepo = EasyMock.createMock(EasyUserRepo.class);
        new Data().setUserRepo(userRepo);
    }

    @Test
    public void getDepositor() throws ObjectNotInStoreException, RepositoryException
    {
        EasyUser willem = new EasyUserImpl("willem");
        EasyUser jan = new EasyUserImpl("jan");

        AdministrativeMetadata amd = new AdministrativeMetadataImpl();
        amd.setDepositorId("willem");

        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("willem")).andReturn(willem).times(1);
        EasyMock.replay(userRepo);
        {
            assertEquals("willem", amd.getDepositorId());
            EasyUser depositor = amd.getDepositor(); // get user from userRepo once
            assertEquals(willem, depositor);
            EasyUser depositor2 = amd.getDepositor(); // don't ask userRepo again, depositor already there
            assertEquals(willem, depositor2);
        }
        EasyMock.verify(userRepo);

        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan).times(1);
        EasyMock.replay(userRepo);
        {
            // change depositorId
            amd.setDepositorId("jan");
            EasyUser depositor = amd.getDepositor(); // get user from userRepo, depositorId has changed
            assertEquals(jan, depositor);
            assertEquals("jan", amd.getDepositorId());
        }
        EasyMock.verify(userRepo);

        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("willem")).andReturn(willem).times(1);
        EasyMock.replay(userRepo);
        {
            // change depositor
            amd.setDepositor(willem);
            EasyUser depositor = amd.getDepositor();
            assertEquals(willem, depositor);
            assertEquals("willem", amd.getDepositorId());
        }
        EasyMock.verify(userRepo);

        EasyMock.reset(userRepo);
        EasyMock.replay(userRepo);
        {
            // set depositorId to null
            amd.setDepositorId(null);
            EasyUser depositor = amd.getDepositor(); // don't ask userRepo if depositorId == null
            assertNull(depositor);
        }
        EasyMock.verify(userRepo);
    }
    
    @Test
    public void dirtyChecking() // if hash-dirty-checking is implemented correctly this test can be removed
    {
        if (verbose)
            Tester.printClassAndFieldHierarchy(AdministrativeMetadataImpl.class);
        // fields affected by dirty checking:
        // versionable:boolean
        // administrativeState:AdministrativeMetadata$AdministrativeState
        // depositorId:java.lang.String
        // workflowData:nl.knaw.dans.easy.domain.dataset.WorkflowData

        AdministrativeMetadataImpl amd = new AdministrativeMetadataImpl();
        assertTrue(amd.isDirty());

        amd.setVersionable(!amd.isVersionable());
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        assertFalse(amd.isDirty());

        assertEquals(DatasetState.DRAFT, amd.getAdministrativeState());
        amd.setAdministrativeState(DatasetState.SUBMITTED);
        assertTrue(amd.isDirty());
        amd.setDirty(false);

        amd.setDepositorId("janK");
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        
        if (verbose)
            Tester.printClassAndFieldHierarchy(WorkflowDataImpl.class);       
        // fields affected by dirty checking:
        // assigneeId:java.lang.String
        // workflow:nl.knaw.dans.easy.domain.workflow.WorkflowStep

        WorkflowData wfd = amd.getWorkflowData();
        assertFalse(amd.isDirty());
        wfd.setAssigneeId("pietK");
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        //assertFalse(amd.isDirty());
        
        if (verbose)
            Tester.printClassAndFieldHierarchy(WorkflowStep.class);
        // fields affected by dirty checking
        // completed:boolean
        // doneById:java.lang.String
        // timeSpent:double
        // remarks:java.util.List
        
        WorkflowStep wfStep = wfd.getWorkflow();
        wfStep.setCompleted(true);
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        //assertFalse(amd.isDirty());
        
        wfStep.setDoneById("KeesK");
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        
        WorkflowStep wfStepLevel2 = wfStep.getSteps().get(0);
        wfStepLevel2.setTimeSpent(1.0D);
        assertTrue(amd.isDirty());
        amd.setDirty(false);
        
        wfStep.addRemark(new Remark("Hoi", "myId"));
        assertTrue(amd.isDirty());
        amd.setDirty(false);
    }

    @Test
    public void testDatasetState()
    {
        AdministrativeMetadata amd = new AdministrativeMetadataImpl();
        DateTime nullStateChange = amd.getLastStateChange();
        assertNotNull(nullStateChange);
        assertEquals(DatasetState.DRAFT, amd.getAdministrativeState());

        pause(10); // to make sure we are not executing at split millisecond speed
        amd.setAdministrativeState(DatasetState.SUBMITTED);
        assertEquals(DatasetState.SUBMITTED, amd.getAdministrativeState());
        DateTime draftStateChange = amd.getLastStateChange();
        assertFalse(nullStateChange.equals(draftStateChange));

        pause(10);
        amd.setAdministrativeState(DatasetState.DRAFT);
        assertEquals(DatasetState.DRAFT, amd.getAdministrativeState());
        DateTime resetStateChange = amd.getLastStateChange();
        assertTrue(nullStateChange.equals(resetStateChange));
    }
    
    @Test
    public void testStateChangeDate()
    {
        AdministrativeMetadata amd = new AdministrativeMetadataImpl();

        // since draft is the first state no state change is seen
        amd.setAdministrativeState(DatasetState.DRAFT);
        assertEquals(0, amd.getStateChangeDates().size());

        amd.setAdministrativeState(DatasetState.SUBMITTED);
        assertEquals(1, amd.getStateChangeDates().size());
        assertTrue(amd.getDateOfFirstChangeTo(DatasetState.SUBMITTED) != null);
        assertEquals(DatasetState.DRAFT, amd.getStateChangeDates().get(0).getFromState());
        assertEquals(DatasetState.SUBMITTED, amd.getStateChangeDates().get(0).getToState());
    	
        // state change ignored
        amd.setAdministrativeState(DatasetState.SUBMITTED);
        assertEquals(1, amd.getStateChangeDates().size());
        assertTrue(amd.getDateOfFirstChangeTo(DatasetState.SUBMITTED) != null);

        amd.setAdministrativeState(DatasetState.PUBLISHED);
        assertEquals(2, amd.getStateChangeDates().size());
        assertTrue(amd.getDateOfFirstChangeTo(DatasetState.PUBLISHED) != null);
        assertEquals(DatasetState.SUBMITTED, amd.getStateChangeDates().get(1).getFromState());
        assertEquals(DatasetState.PUBLISHED, amd.getStateChangeDates().get(1).getToState());
        
        amd.setAdministrativeState(DatasetState.MAINTENANCE);
        assertEquals(3, amd.getStateChangeDates().size());
        assertTrue(amd.getDateOfFirstChangeTo(DatasetState.MAINTENANCE) != null);
        assertEquals(DatasetState.PUBLISHED, amd.getStateChangeDates().get(2).getFromState());
        assertEquals(DatasetState.MAINTENANCE, amd.getStateChangeDates().get(2).getToState());
        
        // small sleep so times are not the same
        pause(50);
        
        amd.setAdministrativeState(DatasetState.PUBLISHED);
        assertEquals(4, amd.getStateChangeDates().size());
        assertTrue(amd.getDateOfFirstChangeTo(DatasetState.PUBLISHED) != null);
        assertEquals(DatasetState.MAINTENANCE, amd.getStateChangeDates().get(3).getFromState());
        assertEquals(DatasetState.PUBLISHED, amd.getStateChangeDates().get(3).getToState());
        
        DateTime first = amd.getDateOfFirstChangeTo(DatasetState.PUBLISHED);
        DateTime last = amd.getDateOfLastChangeTo(DatasetState.PUBLISHED);
        assertTrue(first.isBefore(last));
    }

    private void pause(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            //
        }
    }

}
