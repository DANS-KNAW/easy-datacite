package nl.knaw.dans.easy.business.authn;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.annotations.MutatesUser;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.UpdatePasswordMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordService extends AbstractTokenList {
    private static Logger logger = LoggerFactory.getLogger(PasswordService.class);

    /**
     * Store all tokens for update password requests.
     */
    private static final Map<String, String> TOKEN_MAP = new HashMap<String, String>();

    private AuthenticationSpecification authenticationSpecification;
    private ChangePasswordSpecification changePasswordSpecification;

    @Override
    public Map<String, String> getTokenMap() {
        return TOKEN_MAP;
    }

    public ForgottenPasswordMailAuthentication newAuthentication(final String userId, final String returnedTime, final String returnedToken) {
        ForgottenPasswordMailAuthentication fpmAuthn = new ForgottenPasswordMailAuthentication(userId, returnedTime, returnedToken);
        return fpmAuthn;
    }

    public void login(ForgottenPasswordMailAuthentication authentication) {
        final String userId = authentication.getUserId();
        final String requestTime = authentication.getReturnedTime();
        final String requestToken = authentication.getReturnedToken();
        boolean authenticated = checkToken(userId, requestTime, requestToken)
        // gets the user
                && authenticationSpecification.userIsInQualifiedState(authentication);

        if (authenticated) {
            authentication.setState(Authentication.State.Authenticated);
        } else {
            logger.warn("Invalid authentication: " + authentication.toString());
        }
        removeTokenFromList(userId);
    }

    /**
     * Change the password of a user.
     * 
     * @param messenger
     *        messenger for this job
     */
    public void changePassword(ChangePasswordMessenger messenger) {
        if (changePasswordSpecification.isSatisFiedBy(messenger)) {
            changePasswordOnDataLayer(messenger);
            resetRequestToken(messenger);
        } else {
            logger.debug("ChangePassword does not confirm to specification: " + messenger.toString());
        }
    }

    /**
     * Sends a mail with a link to a change-password-page.
     * 
     * @param messenger
     *        messenger for this job
     */
    public void sendUpdatePasswordLink(ForgottenPasswordMessenger messenger) {
        if (ForgottenPasswordSpecification.isSatisfiedBy(messenger)) {
            try {
                handleSendUpdatePasswordLink(messenger);
                messenger.setState(State.UpdateURLSend);
            }
            catch (ServiceException e) {
                messenger.setState(State.MailError, e);
                logger.error(" Not sending mail", e);
            }
        } else {
            logger.debug("Forgotten password data do not confirm to specification: " + messenger.getState());
        }
    }

    private void handleSendUpdatePasswordLink(final ForgottenPasswordMessenger messenger) throws ServiceException {
        final String mailToken = messenger.getMailToken();
        final String requestTime = messenger.getRequestTimeAsString();

        for (EasyUser user : messenger.getUsers()) {
            putTokenInTokenList(user.getId(), requestTime, mailToken);
            new UpdatePasswordMessage(user, messenger).send();
            logger.debug("Update password link send to " + user.getEmail());
        }

    }

    private void changePasswordOnDataLayer(ChangePasswordMessenger messenger) {
        try {
            EasyUser user = messenger.getUser();
            user.setPassword(messenger.getNewPassword());
            update(user, user);
            messenger.setState(ChangePasswordMessenger.State.PasswordChanged);
            logger.debug("Changed password for user " + user);
        }
        catch (RepositoryException e) {
            messenger.setState(ChangePasswordMessenger.State.SystemError, e);
            logger.error("Could not change password for user " + messenger.getUserId(), e);
        }
    }

    private void resetRequestToken(ChangePasswordMessenger messenger) {
        if (messenger.isMailContext()) {
            removeTokenFromList(messenger.getUserId());
        }
    }

    // double parameters for MutatesData.aj. 1. actor, 2. subject
    @MutatesUser
    private void update(EasyUser sessionUser, EasyUser user) throws RepositoryException {
        Data.getUserRepo().update(user);
    }

    public void setAuthenticationSpecification(AuthenticationSpecification authenticationSpecification) {
        this.authenticationSpecification = authenticationSpecification;
    }

    public void setChangePasswordSpecification(ChangePasswordSpecification changePasswordSpecification) {
        this.changePasswordSpecification = changePasswordSpecification;
    }
}
