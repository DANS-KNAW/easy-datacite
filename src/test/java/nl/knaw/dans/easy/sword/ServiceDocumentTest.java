package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;
import static org.purl.sword.base.SwordValidationInfoType.*;

public class ServiceDocumentTest extends EasySwordServerTester
{
    @BeforeClass
    public static void setupMocking() throws Exception
    {
        MockUtil.mockAll();
    }

    @Test
    public void serviceDocumentWithUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(MockUtil.VALID_USER_ID);
        request.setPassword(MockUtil.PASSWORD);
        request.setLocation(LOCATION);
        mockContext();
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithUser.xml");
    }

    private void mockContext()
    {
        new Context().setCollectionPolicy("No guarantee of service, or that deposits will be retained for any length of time.");
        new Context().setCollectionTreatment("This is a test server");
        new Context().setCollectionAbstract("Electronic Archive System, accepts deposits by users registered on {0}");
        new Context().setCollectionTitle("EASY");
        new Context().setWorkspaceTitle("DANS sword interface");
    }

    @Test
    public void serviceDocumentWrongUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(MockUtil.INVALID_USER_ID);
        request.setPassword(MockUtil.PASSWORD);
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWrongUser.xml");
    }

    @Test
    // (expected=SWORDErrorException.class)
    public void serviceDocumentWithoutUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithoutUser.xml");
    }

    @Test
    public void serviceDocumentWithFederativeUser() throws Exception
    {
        final FederativeUserRepo federativeUserRepo = EasyMock.createMock(FederativeUserRepo.class);
        final FederativeUserService federativeUserService = EasyMock.createMock(FederativeUserService.class);

        new Data().setFederativeUserRepo(federativeUserRepo);
        new Services().setFederativeUserService(federativeUserService);

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        new Data().setUserRepo(userRepo);
        EasyUser easyUser = new EasyUserImpl();
        String mailName = "richardzijdeman";
        String mailAddress = mailName + "@SURFguest.nl";
        EasyMock.expect(userRepo.findById(mailName)).andReturn(easyUser);

        FederativeUserIdMap idMap = new FederativeUserIdMap(mailAddress, mailName);
        EasyMock.expect(federativeUserRepo.findById(mailAddress)).andReturn(idMap);

        EasyMock.replay(userRepo);
        EasyMock.replay(federativeUserRepo);
        EasyMock.replay(federativeUserService);

        // Done mocking, so do the test
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername("nl.knaw.dans.easy.federatedUser");
        request.setPassword(mailAddress + "f33bbf238a3157b0db8ab45088cc77d1d10bb640");
        request.setLocation(LOCATION);
        mockContext();
        final ServiceDocument serviceDocument = easySwordServer.doServiceDocument(request);
        assertAsExpected(serviceDocument.toString(), "serviceDocumentWithFederativeUser.xml");
        // TODO upgrade compliancy level to VALID
        assertCompliant(WARNING, serviceDocument.validate());
    }
}
