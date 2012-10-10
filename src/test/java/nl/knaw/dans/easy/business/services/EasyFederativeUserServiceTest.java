package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.util.TestHelper;

public class EasyFederativeUserServiceTest extends TestHelper
{
    private FederativeUserService federativeUserService = new EasyFederativeUserService();
    private Data data = new Data();

    final String FAKE_FEDID = "some.fake.federatedUserId";
    final String FAKE_EASYID = "some.fake.easyUserId";

    @Test
    public void findFederativeUser() throws RepositoryException, ServiceException
    {
        EasyUser easyUser = new EasyUserImpl();
        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);
        EasyMock.expect(userRepo.findById(FAKE_EASYID)).andReturn(easyUser);

        FederativeUserIdMap idMap = new FederativeUserIdMap(FAKE_FEDID, FAKE_EASYID);
        FederativeUserRepo federativeUserRepo = EasyMock.createMock(FederativeUserRepo.class);
        data.setFederativeUserRepo(federativeUserRepo);
        EasyMock.expect(federativeUserRepo.findById(FAKE_FEDID)).andReturn(idMap);

        EasyMock.replay(userRepo);
        EasyMock.replay(federativeUserRepo);
        
        EasyUserAnonymous sessionUser = new EasyUserAnonymous();
        // There is a mapping, so we want the EasyUser object.
        EasyUser userById = federativeUserService.getUserById(sessionUser, FAKE_FEDID);
        
        assertEquals(easyUser, userById);
        EasyMock.verify(userRepo);
        EasyMock.verify(federativeUserRepo);
    }

    @Test(expected = ObjectNotAvailableException.class)
    public void findFederativeUserNotMapped() throws RepositoryException, ServiceException
    {
        FederativeUserRepo federativeUserRepo = EasyMock.createMock(FederativeUserRepo.class);
        data.setFederativeUserRepo(federativeUserRepo);
        EasyMock.expect(federativeUserRepo.findById(FAKE_FEDID)).andThrow(new ObjectNotInStoreException("not in store"));

        EasyMock.replay(federativeUserRepo);
        
        EasyUserAnonymous sessionUser = new EasyUserAnonymous();
        // When there is no mapping for the federative user we want an exception
        federativeUserService.getUserById(sessionUser, FAKE_FEDID);
    }

}
