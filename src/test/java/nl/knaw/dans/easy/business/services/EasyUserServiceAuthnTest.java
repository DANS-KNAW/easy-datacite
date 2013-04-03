package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.mail.Attachement;
import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.business.authn.AuthenticationSpecification;
import nl.knaw.dans.easy.business.authn.ChangePasswordSpecification;
import nl.knaw.dans.easy.business.authn.LoginService;
import nl.knaw.dans.easy.business.authn.PasswordService;
import nl.knaw.dans.easy.business.authn.RegistrationService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.authn.RegistrationMailAuthentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.util.TestHelper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyUserServiceAuthnTest extends TestHelper
{
    private static MockMailer mockMailer;

    private UserService userService = new EasyUserService();
    private Data data = new Data();

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        before(EasyUserServiceAuthnTest.class);
        mockMailer = new MockMailer();
        new ExternalServices().setMailOffice(mockMailer);

        ClassPathHacker.addFile("../easy-webui/src/main/resources");
    }

    @Before
    public void before()
    {
        mockMailer.mailCount = 0;
        mockMailer.verbose = verbose;
        userService = new EasyUserService();
        LoginService loginService = new LoginService();
        loginService.setAuthenticationSpecification(new AuthenticationSpecification());
        RegistrationService registrationService = new RegistrationService();
        registrationService.setAuthenticationSpecification(new AuthenticationSpecification());
        PasswordService passwordService = new PasswordService();
        passwordService.setAuthenticationSpecification(new AuthenticationSpecification());
        ChangePasswordSpecification changePasswordSpecification = new ChangePasswordSpecification();
        changePasswordSpecification.setAuthenticationSpecification(new AuthenticationSpecification());
        passwordService.setChangePasswordSpecification(changePasswordSpecification);
        userService.setLoginService(loginService);
        userService.setRegistrationService(registrationService);
        userService.setPasswordService(passwordService);
    }

    @Test
    public void authenticateWithUserNamePassword() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithUserNamePassword");
        EasyUser jan = new EasyUserImpl();
        jan.setState(EasyUser.State.ACTIVE);
        UsernamePasswordAuthentication authn = authenticateWithUsernamePassword(jan);
        assertTrue(authn.isCompleted());
        assertEquals(jan, authn.getUser());
        assertEquals(0, authn.getExceptions().size());

        jan.setState(EasyUser.State.CONFIRMED_REGISTRATION);
        authn = authenticateWithUsernamePassword(jan);
        assertTrue(authn.isCompleted());
        assertEquals(jan, authn.getUser());
        assertEquals(0, authn.getExceptions().size());

        jan.setState(EasyUser.State.REGISTERED);
        authn = authenticateWithUsernamePassword(jan);
        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.NotQualified, authn.getState());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());

        jan.setState(EasyUser.State.BLOCKED);
        authn = authenticateWithUsernamePassword(jan);
        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.NotQualified, authn.getState());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    private UsernamePasswordAuthentication authenticateWithUsernamePassword(EasyUser jan) throws RepositoryException, ServiceException
    {
        UsernamePasswordAuthentication authn = userService.newUsernamePasswordAuthentication();
        authn.setUserId("jan");
        authn.setCredentials("secret");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "secret")).andReturn(true);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);

        EasyMock.replay(userRepo);
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        return authn;
    }

    @Test
    public void authenticateWithUsernamePasswordAndInsufficientData() throws ServiceException
    {
        startOfTest("authenticateWithUsernamePasswordAndInsufficientData");
        UsernamePasswordAuthentication authn = userService.newUsernamePasswordAuthentication();
        userService.authenticate(authn);
        assertFalse(authn.isCompleted());
        assertTrue(authn.getAccumelatedStates().contains(Authentication.State.UserIdConnotBeBlank));
        assertTrue(authn.getAccumelatedStates().contains(Authentication.State.CredentialsCannotBeBlank));
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithUsernamePasswordAndInvalidCredentials() throws RepositoryException, ServiceException, ServiceException
    {
        startOfTest("authenticateWithUsernamePasswordAndInvalidCredentials");
        UsernamePasswordAuthentication authn = userService.newUsernamePasswordAuthentication();
        authn.setUserId("jan");
        authn.setCredentials("secret");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "secret")).andReturn(false);

        EasyMock.replay(userRepo);
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.InvalidUsernameOrCredentials, authn.getState());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithUsernamePasswordAndException() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithUsernamePasswordAndException");
        UsernamePasswordAuthentication authn = userService.newUsernamePasswordAuthentication();
        authn.setUserId("jan");
        authn.setCredentials("secret");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "secret")).andThrow(new RepositoryException("Whoeah! (Bobby)"));

        EasyMock.replay(userRepo);
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.SystemError, authn.getState());
        assertNull(authn.getUser());
        assertEquals(1, authn.getExceptions().size());
    }

    @Test
    public void register() throws RepositoryException, ServiceException
    {
        startOfTest("register");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        Registration registration = register(jan);
        assertEquals(Registration.State.Registered, registration.getState());
        assertTrue(jan.hasRole(Role.USER));
        assertEquals(1, jan.getRoles().size());
        assertEquals(1, registrationService.pendingRequests());
        assertTrue(registration.isCompleted());
        assertEquals(0, registration.getExceptions().size());
        assertEquals(1, mockMailer.getMailCount());
    }

    private Registration register(EasyUser jan) throws RepositoryException, ServiceException
    {
        // register
        Registration registration = new Registration(jan);
        registration.setValidationUrl("http://foo.com");
        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.exists("jan")).andReturn(false);
        EasyMock.expect(userRepo.add(jan)).andReturn("jan");
        EasyMock.expect(userRepo.update(jan)).andReturn("jan");

        EasyMock.replay(userRepo);
        userService.handleRegistrationRequest(registration);
        EasyMock.verify(userRepo);
        return registration;
    }

    @Test
    public void registerWithExistingId() throws RepositoryException, ServiceException
    {
        startOfTest("registerWithExistingId");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        Registration registration = new Registration(jan);
        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.exists("jan")).andReturn(true);

        EasyMock.replay(userRepo);
        userService.handleRegistrationRequest(registration);
        EasyMock.verify(userRepo);

        assertEquals(Registration.State.UserIdNotUnique, registration.getState());
        assertEquals(0, registrationService.pendingRequests());
        assertFalse(registration.isCompleted());
        assertEquals(0, registration.getExceptions().size());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void registerWithInsufficientData() throws ServiceException
    {
        startOfTest("registerWithInsufficientData");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser user = new EasyUserImpl();
        Registration registration = new Registration(user);
        userService.handleRegistrationRequest(registration);

        assertTrue(registration.getAccumelatedStates().contains(Registration.State.EmailCannotBeBlank));
        assertTrue(registration.getAccumelatedStates().contains(Registration.State.InitialsCannotBeBlank));
        assertTrue(registration.getAccumelatedStates().contains(Registration.State.PasswordCannotBeBlank));
        assertTrue(registration.getAccumelatedStates().contains(Registration.State.SurnameCannotBeBlank));
        assertTrue(registration.getAccumelatedStates().contains(Registration.State.UserIdCannotBeBlank));
        assertEquals(0, registrationService.pendingRequests());
        assertFalse(registration.isCompleted());
        assertEquals(0, registration.getExceptions().size());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void registerWithException() throws RepositoryException, ServiceException
    {
        startOfTest("registerWithException");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        Registration registration = new Registration(jan);
        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.exists("jan")).andReturn(false);
        EasyMock.expect(userRepo.add(jan)).andReturn("jan");
        EasyMock.expect(userRepo.update(jan)).andThrow(new RepositoryException("boo!"));
        userRepo.delete(jan);

        EasyMock.replay(userRepo);
        userService.handleRegistrationRequest(registration);
        EasyMock.verify(userRepo);

        assertFalse(registration.isCompleted());
        assertEquals(Registration.State.SystemError, registration.getState());
        assertEquals(0, registrationService.pendingRequests());
        assertFalse(registration.isCompleted());
        assertEquals(1, registration.getExceptions().size());
        // mail is send before update(jan) and DataAccessException!
        assertEquals(1, mockMailer.getMailCount());
    }

    @Test
    public void authenticateWithRegistrationMail() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithRegistrationMail");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        Registration registration = register(jan);
        assertEquals(1, registrationService.pendingRequests());

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);
        EasyMock.expect(userRepo.update(jan)).andReturn("jan");

        EasyMock.replay(userRepo);
        RegistrationMailAuthentication authn = userService.newRegistrationMailAuthentication(registration.getUserId(), registration.getRequestTimeAsString(),
                registration.getMailToken());
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertTrue(authn.isCompleted());
        assertEquals(0, registrationService.pendingRequests());
        assertEquals(jan, authn.getUser());
        assertEquals(EasyUser.State.CONFIRMED_REGISTRATION, jan.getState());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithInvalidRegistrationMail() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithInvalidRegistrationMail");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        register(jan);
        assertEquals(1, registrationService.pendingRequests());

        RegistrationMailAuthentication authn = userService.newRegistrationMailAuthentication("jan", "123", "-456");
        userService.authenticate(authn);

        assertFalse(authn.isCompleted());
        assertEquals(0, registrationService.pendingRequests());
        assertNull(authn.getUser());
        assertEquals(EasyUser.State.REGISTERED, jan.getState());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithRegistrationMailAndException() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithRegistrationMailAndException");
        RegistrationService registrationService = ((EasyUserService) userService).getRegistrationService();
        registrationService.reset();
        assertEquals(0, registrationService.pendingRequests());

        EasyUser jan = getValidUser();
        Registration registration = register(jan);
        assertEquals(1, registrationService.pendingRequests());

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);
        EasyMock.expect(userRepo.update(jan)).andThrow(
                new RepositoryException("troglodyte, bashi-bazouk, kleptomaniac, ectoplasm, sea-gherkin, anacoluthon, and pockmark"));

        EasyMock.replay(userRepo);
        RegistrationMailAuthentication authn = userService.newRegistrationMailAuthentication(registration.getUserId(), registration.getRequestTimeAsString(),
                registration.getMailToken());
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertFalse(authn.isCompleted());
        assertEquals(0, registrationService.pendingRequests());
        assertNull(authn.getUser());
        assertEquals(EasyUser.State.REGISTERED, jan.getState());
        assertEquals(1, authn.getExceptions().size());
    }

    @Test
    public void forgottenPasswordRequestWithUserId() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithUserId");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ForgottenPasswordMessenger messenger = forgottenPasswordRequestWithUserId(jan);
        assertTrue(messenger.isCompleted());
        assertEquals(jan, messenger.getUsers().get(0));
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(1, passwordService.pendingRequests());
        assertEquals(1, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.CONFIRMED_REGISTRATION);
        messenger = forgottenPasswordRequestWithUserId(jan);
        assertTrue(messenger.isCompleted());
        assertEquals(jan, messenger.getUsers().get(0));
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(1, passwordService.pendingRequests());
        assertEquals(1, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.REGISTERED);
        messenger = forgottenPasswordRequestWithUserId(jan);
        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.NoQualifiedUsers, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.BLOCKED);
        messenger = forgottenPasswordRequestWithUserId(jan);
        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.NoQualifiedUsers, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    private ForgottenPasswordMessenger forgottenPasswordRequestWithUserId(EasyUser jan) throws RepositoryException, ServiceException
    {
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger("jan", null);
        messenger.setUpdateURL("http://this.is.the.updateUrl/requestTime/123/requestToken/-456/");
        messenger.setUserIdParamKey("keyForUserId");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        return messenger;
    }

    @Test
    public void forgottenPasswordRequestWithEmail() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithEmail");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        EasyUser piet = getValidPiet();
        piet.setState(EasyUser.State.ACTIVE);
        List<EasyUser> users = Arrays.asList(new EasyUser[] {jan, piet});
        ForgottenPasswordMessenger messenger = forgottenPasswordRequestWithEmail(users);
        assertTrue(messenger.isCompleted());
        assertTrue(messenger.getUsers().contains(jan));
        assertTrue(messenger.getUsers().contains(piet));
        assertEquals(2, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(2, passwordService.pendingRequests());
        assertEquals(2, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        piet = getValidPiet();
        piet.setState(EasyUser.State.CONFIRMED_REGISTRATION);
        users = Arrays.asList(new EasyUser[] {jan, piet});
        messenger = forgottenPasswordRequestWithEmail(users);
        assertTrue(messenger.isCompleted());
        assertTrue(messenger.getUsers().contains(jan));
        assertTrue(messenger.getUsers().contains(piet));
        assertEquals(2, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(2, passwordService.pendingRequests());
        assertEquals(2, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        piet = getValidPiet();
        piet.setState(EasyUser.State.REGISTERED);
        users = Arrays.asList(new EasyUser[] {jan, piet});
        messenger = forgottenPasswordRequestWithEmail(users);
        assertTrue(messenger.isCompleted());
        assertTrue(messenger.getUsers().contains(jan));
        assertFalse(messenger.getUsers().contains(piet));
        assertEquals(1, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(1, passwordService.pendingRequests());
        assertEquals(1, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        piet = getValidPiet();
        piet.setState(EasyUser.State.BLOCKED);
        users = Arrays.asList(new EasyUser[] {jan, piet});
        messenger = forgottenPasswordRequestWithEmail(users);
        assertTrue(messenger.isCompleted());
        assertTrue(messenger.getUsers().contains(jan));
        assertFalse(messenger.getUsers().contains(piet));
        assertEquals(1, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(1, passwordService.pendingRequests());
        assertEquals(1, mockMailer.getMailCount());

        jan = getValidUser();
        jan.setState(EasyUser.State.REGISTERED);
        piet = getValidPiet();
        piet.setState(EasyUser.State.BLOCKED);
        users = Arrays.asList(new EasyUser[] {jan, piet});
        messenger = forgottenPasswordRequestWithEmail(users);
        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.NoQualifiedUsers, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    private ForgottenPasswordMessenger forgottenPasswordRequestWithEmail(List<EasyUser> users) throws RepositoryException, ServiceException
    {
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger(null, "jan@jansen.com");
        messenger.setUpdateURL("http://this.is.the.updateUrl/requestTime/123/requestToken/-456/");
        messenger.setUserIdParamKey("keyForUserId");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findByEmail("jan@jansen.com")).andReturn(users);

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        return messenger;
    }

    @Test
    public void forgottenPasswordRequestWithInvalidData() throws ServiceException
    {
        startOfTest("forgottenPasswordRequestWithInvalidData");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger(null, null);

        userService.handleForgottenPasswordRequest(messenger);

        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.InsufficientData, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void forgottenPasswordRequestWithUserIdAndDataAccessException() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithUserIdAndDataAccessException");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger("jan", null);

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andThrow(new RepositoryException("Oh no! La Castafiore!"));

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.SystemError, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(1, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void forgottenPasswordRequestWithUserIdAndObjectNotFoundException() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithUserIdAndObjectNotFoundException");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger("jan", null);

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andThrow(new ObjectNotInStoreException("Duizend bommen en granaten!"));

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.UserNotFound, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(1, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void forgottenPasswordRequestWithEmailAndDataAccessException() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithEmailAndDataAccessException");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger(null, "jan@jansen.com");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findByEmail("jan@jansen.com")).andThrow(new RepositoryException("GEEN spuitwater in mijn whisky!"));

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.SystemError, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(1, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void forgottenPasswordRequestWithEmailNotFound() throws RepositoryException, ServiceException
    {
        startOfTest("forgottenPasswordRequestWithEmailNotFound");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();
        passwordService.reset();
        assertEquals(0, passwordService.pendingRequests());

        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger(null, "jan@jansen.com");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findByEmail("jan@jansen.com")).andReturn(new ArrayList<EasyUser>());

        EasyMock.replay(userRepo);
        userService.handleForgottenPasswordRequest(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(ForgottenPasswordMessenger.State.UserNotFound, messenger.getState());
        assertEquals(0, messenger.getUsers().size());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(0, mockMailer.getMailCount());
    }

    @Test
    public void authenticateWithForgottenPasswordMail() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithForgottenPasswordMail");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ForgottenPasswordMessenger messenger = forgottenPasswordRequestWithUserId(jan);
        assertEquals(1, passwordService.pendingRequests());

        ForgottenPasswordMailAuthentication authn = userService.newForgottenPasswordMailAuthentication(messenger.getUserId(), messenger
                .getRequestTimeAsString(), messenger.getMailToken());

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);

        EasyMock.replay(userRepo);
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertTrue(authn.isCompleted());
        assertEquals(0, passwordService.pendingRequests());
        assertEquals(jan, authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithForgottenPasswordMailButUserStateChanged() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithForgottenPasswordMailButUserStateChanged");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ForgottenPasswordMessenger messenger = forgottenPasswordRequestWithUserId(jan);
        assertEquals(1, passwordService.pendingRequests());

        ForgottenPasswordMailAuthentication authn = userService.newForgottenPasswordMailAuthentication(messenger.getUserId(), messenger
                .getRequestTimeAsString(), messenger.getMailToken());

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        // change of user state
        jan.setState(EasyUser.State.BLOCKED);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);

        EasyMock.replay(userRepo);
        userService.authenticate(authn);
        EasyMock.verify(userRepo);

        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.NotQualified, authn.getState());
        assertEquals(0, passwordService.pendingRequests());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithInvalidForgottenPasswordMail() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithInvalidForgottenPasswordMail");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        forgottenPasswordRequestWithUserId(jan);
        assertEquals(1, passwordService.pendingRequests());

        ForgottenPasswordMailAuthentication authn = userService.newForgottenPasswordMailAuthentication("jan", "blabla", "bliep");

        userService.authenticate(authn);

        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.NotAuthenticated, authn.getState());
        assertEquals(0, passwordService.pendingRequests());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void authenticateWithTotalyInvalidForgottenPasswordMail() throws RepositoryException, ServiceException
    {
        startOfTest("authenticateWithTotalyInvalidForgottenPasswordMail");
        PasswordService passwordService = ((EasyUserService) userService).getPasswordService();

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        forgottenPasswordRequestWithUserId(jan);
        assertEquals(1, passwordService.pendingRequests());

        ForgottenPasswordMailAuthentication authn = userService.newForgottenPasswordMailAuthentication("foo", "blabla", "bliep");

        userService.authenticate(authn);

        assertFalse(authn.isCompleted());
        assertEquals(Authentication.State.NotAuthenticated, authn.getState());
        assertEquals(1, passwordService.pendingRequests());
        assertNull(authn.getUser());
        assertEquals(0, authn.getExceptions().size());
    }

    @Test
    public void changePasswordAndInsufficientData() throws ServiceException
    {
        startOfTest("changePasswordAndInsufficientData");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, false);

        userService.changePassword(messenger);

        assertFalse(messenger.isCompleted());
        assertEquals(ChangePasswordMessenger.State.InsufficientData, messenger.getState());
        assertEquals(0, messenger.getExceptions().size());
    }

    @Test
    public void changePasswordAndNotAuthenticated() throws RepositoryException, ServiceException
    {
        startOfTest("changePasswordAndNotAuthenticated");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, false);
        messenger.setOldPassword("wrong");
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "wrong")).andReturn(false);

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(ChangePasswordMessenger.State.NotAuthenticated, messenger.getState());
        assertEquals(0, messenger.getExceptions().size());
    }

    @Test
    public void changePassword() throws RepositoryException, ServiceException
    {
        startOfTest("changePassword");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, false);
        messenger.setOldPassword("secret");
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "secret")).andReturn(true);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);
        EasyMock.expect(userRepo.update(jan)).andReturn("jan");

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertTrue(messenger.isCompleted());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals("haddock", jan.getPassword());
    }

    @Test
    public void changePasswordAfterMail() throws RepositoryException, ServiceException
    {
        startOfTest("changePasswordAfterMail");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, true);
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.update(jan)).andReturn("jan");

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertTrue(messenger.isCompleted());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals("haddock", jan.getPassword());
    }

    @Test
    public void changePasswordAfterMailAndUserNotFound() throws RepositoryException, ServiceException
    {
        startOfTest("changePasswordAfterMailAndUserNotFound");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.ACTIVE);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, true);
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.update(jan)).andThrow(new RepositoryException("Jan has been deleted 5 seconds ago!"));

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(1, messenger.getExceptions().size());
        assertEquals(ChangePasswordMessenger.State.SystemError, messenger.getState());
        // momentary value of new password stays on user, but it doesn't matter: old password wasn't
        // changed on data layer.
        assertEquals("haddock", jan.getPassword());
    }

    @Test
    public void changePasswordForUnQualifiedUser() throws RepositoryException, ServiceException
    {
        startOfTest("changePasswordForUnQualifiedUser");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.BLOCKED);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, false);
        messenger.setOldPassword("secret");
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.expect(userRepo.authenticate("jan", "secret")).andReturn(true);
        EasyMock.expect(userRepo.findById("jan")).andReturn(jan);

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals("secret", jan.getPassword());
    }

    @Test
    public void changePasswordForUnQualifiedUserAfterMail() throws RepositoryException, ServiceException
    {
        startOfTest("changePasswordForUnQualifiedUserAfterMail");

        EasyUser jan = getValidUser();
        jan.setState(EasyUser.State.BLOCKED);
        ChangePasswordMessenger messenger = new ChangePasswordMessenger(jan, true);
        messenger.setOldPassword("secret");
        messenger.setNewPassword("haddock");

        EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        EasyMock.replay(userRepo);
        userService.changePassword(messenger);
        EasyMock.verify(userRepo);

        assertFalse(messenger.isCompleted());
        assertEquals(0, messenger.getExceptions().size());
        assertEquals(ChangePasswordMessenger.State.NotChanged, messenger.getState());
        assertEquals("secret", jan.getPassword());
    }

    private EasyUser getValidUser()
    {
        EasyUser jan = new EasyUserImpl();
        // set required data
        jan.setId("jan");
        jan.setInitials("J.A.N.");
        jan.setFirstname("Jan");
        jan.setSurname("Jansen");
        jan.setPassword("secret");
        jan.setEmail("jan@jansen.com");
        return jan;
    }

    private EasyUser getValidPiet()
    {
        EasyUser piet = new EasyUserImpl();
        // set required data
        piet.setId("piet");
        piet.setInitials("P.");
        piet.setFirstname("Piet");
        piet.setSurname("Pietersen");
        piet.setPassword("secret");
        piet.setEmail("jan@jansen.com");
        return piet;
    }

    private static class MockMailer implements Mailer
    {

        private static final Logger logger = LoggerFactory.getLogger(MockMailer.class);
        private int mailCount;
        private boolean verbose;

        protected int getMailCount()
        {
            int mc = mailCount;
            mailCount = 0;
            return mc;
        }

        public void sendMail(String subject, String textContent, String htmlContent, String... receivers) throws MailerException
        {
            mailCount++;
            if (verbose)
                logger.debug("\n" + htmlContent + "\n");

        }

        public void sendMail(String subject, String textContent, String htmlContent, Attachement[] attachments, String... receivers) throws MailerException
        {
            mailCount++;
            if (verbose)
                logger.debug("\n" + htmlContent + "\n");
        }

        public void sendSimpleMail(String subject, String textContent, String... receivers) throws MailerException
        {
            mailCount++;
            if (verbose)
                logger.debug("\n" + textContent + "\n");
        }

        public void sendSimpleMail(String subject, String textContent, Attachement[] attachments, String... receivers) throws MailerException
        {
            mailCount++;
            if (verbose)
                logger.debug("\n" + textContent + "\n");
        }

        @Override
        public void sendSimpleMail(String subject, String textContent, List<String> recievers) throws MailerException
        {
            mailCount++;
            if (verbose)
                logger.debug("\n" + textContent + "\n");
        }

    }

}
