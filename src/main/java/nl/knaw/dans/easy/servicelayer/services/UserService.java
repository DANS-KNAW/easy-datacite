/**
 * 
 */
package nl.knaw.dans.easy.servicelayer.services;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.authn.LoginService;
import nl.knaw.dans.easy.business.authn.PasswordService;
import nl.knaw.dans.easy.business.authn.RegistrationService;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.FederativeUserRegistration;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.authn.RegistrationMailAuthentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

/**
 * Services related to the user.
 * 
 * @author ecco
 */
public interface UserService
{
    UsernamePasswordAuthentication newUsernamePasswordAuthentication() throws ServiceException;

    RegistrationMailAuthentication newRegistrationMailAuthentication(final String userId, final String returnedTime, final String returnedToken)
            throws ServiceException;

    ForgottenPasswordMailAuthentication newForgottenPasswordMailAuthentication(final String userId, final String returnedTime, final String returnedToken)
            throws ServiceException;

    void authenticate(Authentication authentication) throws ServiceException;

    void logout(EasyUser user) throws ServiceException;

    EasyUser getUserById(EasyUser sessionUser, String uid) throws ObjectNotAvailableException, ServiceException;

    List<EasyUser> getUserByEmail(String email) throws ServiceException;

    List<EasyUser> getUsersByRole(Role role) throws ServiceException;

    Map<String, String> getByCommonNameStub(String stub, long maxCount) throws ServiceException;

    EasyUser update(EasyUser updater, EasyUser user) throws ServiceException;

    /**
     * Handle registration of a new user.
     * 
     * @param registration
     *        registration messenger with valid user and confirmation url
     */
    Registration handleRegistrationRequest(Registration registration) throws ServiceException;

    FederativeUserRegistration handleRegistrationRequest(FederativeUserRegistration registration) throws ServiceException;

    /**
     * Determine if there is a password stored for the given user. This does not determine if a user is
     * federative, because some federative users might have a password stored. This is the case for EASY
     * accounts (with password) that have been coupled with a federative account.
     * 
     * @param user
     *        user
     * @return <code>true</code> if a password is stored, <code>false</code> otherwise
     * @throws ServiceException
     */
    boolean isUserWithStoredPassword(final EasyUser user) throws ServiceException;

    void changePassword(ChangePasswordMessenger messenger) throws ServiceException;

    void handleForgottenPasswordRequest(ForgottenPasswordMessenger messenger) throws ServiceException;

    List<EasyUser> getAllUsers() throws ServiceException;

    List<Group> getAllGroups() throws ServiceException;

    List<String> getAllGroupIds() throws ServiceException;

    OperationalAttributes getOperationalAttributes(EasyUser user) throws ServiceException;

    OperationalAttributes getOperationalAttributes(Group group) throws ServiceException;

    void setPasswordService(PasswordService passwordService);

    void setLoginService(LoginService loginService);

    void setRegistrationService(RegistrationService registrationService);

}
