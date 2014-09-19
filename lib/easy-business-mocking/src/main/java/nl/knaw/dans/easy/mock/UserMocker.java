package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.expect;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

/** Wraps mocked instances of an {@link EasyUser} */
public class UserMocker {
    /**
     * Creates a mocked instance of an {@link EasyUser}. A fluent interface allows further configuration of possible/expected behavior of the instance, and
     * related methods of {@link EasyUserRepo}.
     * 
     * @param userId
     * @throws Exception
     */
    UserMocker(final String userId) throws Exception {
        final EasyUser mockedUser = PowerMock.createMock(EasyUser.class);
        expect(mockedUser.isAnonymous()).andStubReturn(false);
        expect(mockedUser.getId()).andStubReturn(userId);
        expect(Data.getUserRepo().findById(EasyMock.eq(userId))).andStubReturn(mockedUser);
    }
}
