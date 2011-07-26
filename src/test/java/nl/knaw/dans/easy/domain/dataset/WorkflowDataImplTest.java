package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkflowDataImplTest
{

    private static EasyUserRepo userRepo;

    @BeforeClass
    public static void beforeClass()
    {
        userRepo = EasyMock.createMock(EasyUserRepo.class);
        new Data().setUserRepo(userRepo);
    }

    @Test
    public void getAssignee() throws ObjectNotInStoreException, RepositoryException
    {
        EasyUser willem = new EasyUserImpl("willem");
        EasyUser jan = new EasyUserImpl("jan");

        WorkflowData wfd = new WorkflowDataImpl();
        wfd.setAssigneeId("willem");

        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("willem")).andReturn(willem).times(1);
        EasyMock.replay(userRepo);
        {
            assertEquals("willem", wfd.getAssigneeId());
            EasyUser assignee = wfd.getAssignee(); // get user from userRepo once
            assertEquals(willem, assignee);
            EasyUser assignee2 = wfd.getAssignee(); // don't ask userRepo again, assignee already there
            assertEquals(willem, assignee2);
        }
        EasyMock.verify(userRepo);
          
        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan).times(1);
        EasyMock.replay(userRepo);
        {
            // change assigneeId
            wfd.setAssigneeId("jan");
            EasyUser assignee = wfd.getAssignee(); // get user from userRepo, assigneeId has changed
            assertEquals(jan, assignee);
            assertEquals("jan", wfd.getAssigneeId());
        }
        EasyMock.verify(userRepo);

        EasyMock.reset(userRepo);
        EasyMock.expect(userRepo.findById("willem")).andReturn(willem).times(1);
        EasyMock.replay(userRepo);
        {
            // change assignee
            wfd.setAssignee(willem);
            EasyUser assignee = wfd.getAssignee();
            assertEquals(willem, assignee);
            assertEquals("willem", wfd.getAssigneeId());
        }
        EasyMock.verify(userRepo);

        EasyMock.reset(userRepo);
        EasyMock.replay(userRepo);
        {
            // set assigneeId to null
            wfd.setAssigneeId(null);
            EasyUser assignee = wfd.getAssignee(); // don't ask userRepo if assigneeId == null
            assertNull(assignee);
        }
        EasyMock.verify(userRepo);
    }
    

}
