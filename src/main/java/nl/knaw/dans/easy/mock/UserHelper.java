package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.expect;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.powermock.api.easymock.PowerMock;

public class UserHelper
{
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
        expect(Data.getUserRepo().findById(userId)).andReturn(mockedUser).anyTimes();
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
