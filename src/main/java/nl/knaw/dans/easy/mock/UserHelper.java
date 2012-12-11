package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.expect;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.aspectj.lang.annotation.Before;
import org.powermock.api.easymock.PowerMock;

public class UserHelper
{
    private static EasyUserRepo userRepoMock;

    /**
     * Creates a mocked instance of a {@link EasyUser}. A fluent interface allows further configuration of
     * possible/expected behavior of the instance, and related methods of {@link EasyUserRepo}.
     * 
     * @param userId
     * @throws Exception
     */
    UserHelper(final String userId) throws Exception
    {
        final EasyUser mockedUser = PowerMock.createMock(EasyUser.class);
        expect(userRepoMock.findById(userId)).andReturn(mockedUser).anyTimes();
    }

    /**
     * Prepares the mocks for a new configuration. To be called by a {@link Before}.
     */
    static void reset()
    {
        userRepoMock = PowerMock.createMock(EasyUserRepo.class);
        new Data().setUserRepo(userRepoMock);
    }

    public static void verifyAll()
    {
        PowerMock.verifyAll();
    }

    public static void replayAll()
    {
        PowerMock.replayAll();
    }
}
