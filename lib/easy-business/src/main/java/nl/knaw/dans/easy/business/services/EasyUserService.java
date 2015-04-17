package nl.knaw.dans.easy.business.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.business.authn.LoginService;
import nl.knaw.dans.easy.business.authn.PasswordService;
import nl.knaw.dans.easy.business.authn.RegistrationService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.annotations.MutatesUser;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.FederativeUserRegistration;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.authn.RegistrationMailAuthentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyUserService extends AbstractEasyService implements UserService {

    private static Logger logger = LoggerFactory.getLogger(EasyUserService.class);

    private LoginService loginService;

    private PasswordService passwordService;

    private RegistrationService registrationService;

    public EasyUserService() {}

    @Override
    public UsernamePasswordAuthentication newUsernamePasswordAuthentication() throws ServiceException {
        return loginService.newAuthentication();
    }

    @Override
    public RegistrationMailAuthentication newRegistrationMailAuthentication(final String userId, final String returnedTime, final String returnedToken)
            throws ServiceException
    {
        return registrationService.newAuthentication(userId, returnedTime, returnedToken);
    }

    @Override
    public ForgottenPasswordMailAuthentication newForgottenPasswordMailAuthentication(final String userId, final String returnedTime, final String returnedToken)
            throws ServiceException
    {
        return getPasswordService().newAuthentication(userId, returnedTime, returnedToken);
    }

    @Override
    public void authenticate(Authentication authentication) throws ServiceException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            loginService.login((UsernamePasswordAuthentication) authentication);
            logAuthentication(authentication);
        } else if (authentication instanceof RegistrationMailAuthentication) {
            registrationService.login((RegistrationMailAuthentication) authentication);
            logAuthentication(authentication);
        } else if (authentication instanceof ForgottenPasswordMailAuthentication) {
            getPasswordService().login((ForgottenPasswordMailAuthentication) authentication);
            logAuthentication(authentication);
        } else {
            final String msg = "No method for athentication: " + authentication;
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void logAuthentication(Authentication authentication) {
        if (authentication.isCompleted()) {
            if (logger.isDebugEnabled())
                logger.debug("Authentication successful: " + authentication.toString());
        } else {
            logger.warn("Authentication unsuccessful: " + authentication.toString());
        }
    }

    @Override
    public void logout(final EasyUser user) throws ServiceException {
        // If everything from this point on is stateless than there's no need to do anything.
    }

    @Override
    public EasyUser getUserById(EasyUser sessionUser, final String uid) throws ObjectNotAvailableException, ServiceException {
        EasyUser user = null;
        try {
            user = Data.getUserRepo().findById(uid);
            logger.debug("Found user: " + user.toString());
        }
        catch (final ObjectNotInStoreException e) {
            logger.debug("Object not found. userId='" + uid + "'");
            throw new ObjectNotAvailableException("Object not found. userId='" + uid + "' :", e);
        }
        catch (final RepositoryException e) {
            logger.debug("Could not get user with id '" + uid + "' :", e);
            throw new ServiceException("Could not get user with id '" + uid + "' :", e);
        }
        return user;
    }

    @Override
    public List<EasyUser> getUserByEmail(final String email) throws ServiceException {
        try {
            return Data.getUserRepo().findByEmail(email);
        }
        catch (RepositoryException e) {
            logger.debug("Could not retrieve users by email: ", e);
            throw new ServiceException("Could not retrieve users by email: ", e);
        }
    }

    @Override
    public List<EasyUser> getUsersByRole(Role role) throws ServiceException {
        try {
            return Data.getUserRepo().findByRole(role);
        }
        catch (RepositoryException e) {
            logger.debug("Could not retrieve users by role: ", e);
            throw new ServiceException("Could not retrieve users by role: ", e);
        }
    }

    @Override
    public List<EasyUser> getAllUsers() throws ServiceException {
        try {
            return Data.getUserRepo().findAll();
        }
        catch (final RepositoryException e) {
            logger.debug("Could not retrieve users: ", e);
            throw new ServiceException("Could not retrieve users: ", e);
        }
    }

    @Override
    public List<EasyUser> getUsersByState(State state) throws ServiceException {
        try {
            List<EasyUser> users = Data.getUserRepo().findAll();
            List<EasyUser> filtered = new ArrayList<EasyUser>(users);
            for (EasyUser user : users)
                if (!user.getState().equals(state))
                    filtered.remove(user);
            return filtered;
        }
        catch (final RepositoryException e) {
            logger.debug("Could not retrieve users: ", e);
            throw new ServiceException("Could not retrieve users: ", e);
        }
    }

    @Override
    public List<Group> getAllGroups() throws ServiceException {
        List<Group> groups = null;
        try {
            groups = Data.getGroupRepo().findAll();
        }
        catch (RepositoryException e) {
            logger.debug("Could not retrieve groups: ", e);
            throw new ServiceException("Could not retrieve groups: ", e);
        }
        return groups;
    }

    @Override
    public List<String> getAllGroupIds() throws ServiceException {
        List<String> groupIds = new ArrayList<String>();
        for (Group group : getAllGroups()) {
            groupIds.add(group.getId());
        }
        return groupIds;
    }

    @Override
    public Map<String, String> getByCommonNameStub(String stub, long maxCount) throws ServiceException {
        Map<String, String> idNameMap = null;
        try {
            idNameMap = Data.getUserRepo().findByCommonNameStub(stub, maxCount);
        }
        catch (RepositoryException e) {
            logger.debug("Could not retrieve users by common name stub: ", e);
            throw new ServiceException("Could not retrieve users by common name stub: ", e);
        }
        return idNameMap;
    }

    @MutatesUser
    @Override
    public EasyUser update(final EasyUser sessionUser, final EasyUser user) throws ServiceException {
        // validate user

        // set state of user: active after first login and update personal info
        boolean updaterEqualsUser = sessionUser.equals(user) && EasyUser.State.CONFIRMED_REGISTRATION.equals(user.getState());
        if (updaterEqualsUser) {
            user.setState(EasyUser.State.ACTIVE);
        }

        try {
            Data.getUserRepo().update(user);
        }
        catch (final RepositoryException e) {
            throw new ServiceException(e);
        }
        finally {
            if (updaterEqualsUser) {
                sessionUser.setState(user.getState());
            }
        }

        return user;
    }

    @Override
    public Registration handleRegistrationRequest(final Registration registration) throws ServiceException {
        registrationService.handleRegistrationRequest(registration);
        logger.debug("Handled registration: " + registration.toString());
        return registration;
    }

    @Override
    public FederativeUserRegistration handleRegistrationRequest(FederativeUserRegistration registration) throws ServiceException {
        registrationService.handleRegistrationRequest(registration);
        logger.debug("Handled registration: " + registration.toString());
        return registration;
    }

    @Override
    public boolean isUserWithStoredPassword(final EasyUser user) throws ServiceException {
        boolean hasPassword;
        try {
            hasPassword = Data.getUserRepo().isPasswordStored(user.getId());
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }

        return hasPassword;
    }

    @Override
    public void changePassword(final ChangePasswordMessenger messenger) throws ServiceException {
        // delegate to specialized service.
        getPasswordService().changePassword(messenger);
    }

    @Override
    public void handleForgottenPasswordRequest(final ForgottenPasswordMessenger messenger) throws ServiceException {
        // delegate to specialized service.
        // We can send a new password by mail:
        // passwordService.sendNewPassword(messenger);

        // Or do it in the fancy way by sending a link to a page where the user can type in a new
        // password:
        getPasswordService().sendUpdatePasswordLink(messenger);
    }

    @Override
    public OperationalAttributes getOperationalAttributes(EasyUser user) throws ServiceException {
        try {
            return Data.getUserRepo().getOperationalAttributes(user.getId());
        }
        catch (RepositoryException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public OperationalAttributes getOperationalAttributes(Group group) throws ServiceException {
        try {
            return Data.getGroupRepo().getOperationalAttributes(group.getId());
        }
        catch (RepositoryException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }

    @Override
    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
}
