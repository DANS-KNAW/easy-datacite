package nl.knaw.dans.easy.business.authn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.util.TestHelper;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ForgottenPasswordSpecificationTest extends TestHelper {

    @BeforeClass
    public static void before() {
        before(ForgottenPasswordSpecificationTest.class);
    }

    @AfterClass
    public static void afterClass() {
        // the next test class should not inherit from this one
        new Data().setUserRepo(null);
    }

    @Test
    public void testInSufficientData() {
        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();
        assertFalse(ForgottenPasswordSpecification.isSatisfiedBy(messenger));
        assertEquals(State.InsufficientData, messenger.getState());

    }

    @Test
    public void testUserCanBeFoundByUserId() throws RepositoryException {
        EasyUserRepo repo = EasyMock.createMock(EasyUserRepo.class);
        Data data = new Data();
        data.setUserRepo(repo);
        EasyUser piet = new EasyUserImpl("piet", "piet@boo.nl");
        piet.setState(EasyUser.State.ACTIVE);
        EasyMock.expect(repo.findById("piet")).andReturn(piet).anyTimes();

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();
        messenger.setUserId("piet");

        EasyMock.replay(repo);
        assertTrue(ForgottenPasswordSpecification.isSatisfiedBy(messenger));
        assertEquals(0, messenger.getExceptions().size());
        assertEquals("piet@boo.nl", messenger.getUsers().get(0).getEmail());

        EasyMock.verify(repo);
    }

    @Test
    public void testUserCanNOTBeFoundByUserId() throws RepositoryException {
        EasyUserRepo repo = EasyMock.createMock(EasyUserRepo.class);
        Data data = new Data();
        data.setUserRepo(repo);

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();
        messenger.setUserId("jan");
        EasyMock.expect(repo.findById("jan")).andThrow(new ObjectNotInStoreException("Where is jan?")).anyTimes();

        EasyMock.replay(repo);
        assertFalse(ForgottenPasswordSpecification.isSatisfiedBy(messenger));
        assertEquals(State.UserNotFound, messenger.getState());
        EasyMock.verify(repo);
    }

    @Test
    public void testSystemError() throws RepositoryException {
        EasyUserRepo repo = EasyMock.createMock(EasyUserRepo.class);
        Data data = new Data();
        data.setUserRepo(repo);

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();
        messenger.setUserId("jan");
        EasyMock.expect(repo.findById("jan")).andThrow(new RepositoryException("controlled exception: don't worry Eko!")).anyTimes();

        EasyMock.replay(repo);
        assertFalse(ForgottenPasswordSpecification.isSatisfiedBy(messenger));
        assertEquals(State.SystemError, messenger.getState());
        EasyMock.verify(repo);
    }

}
