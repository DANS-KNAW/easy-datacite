package nl.knaw.dans.easy.business.authn;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.annotations.MutatesUser;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.FederativeUserRegistration;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.authn.RegistrationMailAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.RegistrationConfirmation;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationService extends AbstractTokenList
{
    private static Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    /**
     * Store all tokens for registration requests.
     */
    private static final Map<String, String> TOKEN_MAP = new HashMap<String, String>();

    public RegistrationService()
    {
    }

    @Override
    public Map<String, String> getTokenMap()
    {
        return TOKEN_MAP;
    }

    public RegistrationMailAuthentication newAuthentication(final String userId, final String returnedTime, final String returnedToken)
    {
        RegistrationMailAuthentication authentication = new RegistrationMailAuthentication(userId, returnedTime, returnedToken);
        // do more security things ...
        return authentication;
    }

    public void login(final RegistrationMailAuthentication authentication)
    {
        final String userId = authentication.getUserId();
        final String requestTime = authentication.getReturnedTime();
        final String requestToken = authentication.getReturnedToken();
        boolean authenticated = checkToken(userId, requestTime, requestToken)
        // gets the user
                && AuthenticationSpecification.userIsInQualifiedState(authentication);

        if (authenticated)
        {
            handleConfirmation(authentication);
        }
        else
        {
            logger.warn("Invalid authentication: " + authentication.toString());
        }
        removeTokenFromList(userId);
    }

    // part of login(RegistrationMailAuthentication) procedure
    private void handleConfirmation(final RegistrationMailAuthentication authentication)
    {
        EasyUser user = authentication.getUser();
        user.setState(EasyUser.State.CONFIRMED_REGISTRATION);
        try
        {
            update(user, user);
            authentication.setState(Authentication.State.Authenticated);
        }
        catch (RepositoryException e)
        {
            authentication.setState(Authentication.State.SystemError, e);
            authentication.setUser(null);
            user.setState(EasyUser.State.REGISTERED);
            logger.error("Could not update user after confirm registration: " + user.toString(), e);
        }
    }

    public FederativeUserRegistration handleRegistrationRequest(FederativeUserRegistration registration)
    {
        if (!FederativeUserRegistrationSpecification.isSatisfiedBy(registration))
        {
            logger.debug("Registration does not conform to specification: " + registration.toString());
            return registration;
        }

        handleRegistration(registration);

        if (registration.isCompleted())
        {
            logger.debug("Registered: " + registration.toString());
        }
        else
        {
            logger.error("Registration process unsuccessful: " + registration.toString());
            rollBackRegistration(registration);
        }

        return registration;
    }

    private FederativeUserRegistration handleRegistration(FederativeUserRegistration registration)
    {
        // NOTE Activate User
        registration.getUser().setState(EasyUser.State.ACTIVE);
        // whipe any password jsut to be sure?
        registration.getUser().setPassword(null);

        // put new user in UserRepo
        EasyUser user = registration.getUser();
        try
        {
            add(user, user);
        }
        catch (RepositoryException e)
        {
            logger.error("Could not store a user for registration: ", e);
            registration.setState(FederativeUserRegistration.State.SystemError, e);
            return registration;
        }

        // make coupling with federative User ID?
        try
        {
            Services.getFederativeUserService().addFedUserToEasyUserIdCoupling(registration.getFederativeUserId(), user.getId());
        }
        catch (ServiceException e)
        {
            logger.error("Could not couple a federated user for registration: ", e);
            registration.setState(FederativeUserRegistration.State.SystemError, e);
            return registration;
        }

        registration.setState(FederativeUserRegistration.State.Registered);

        return registration;
    }

    private void rollBackRegistration(FederativeUserRegistration registration)
    {
        logger.debug("Trying a rollback.");
        EasyUser user = registration.getUser();
        try
        {
            delete(user, user);
            logger.debug("Rollback of user registration successful.");
        }
        catch (RepositoryException e)
        {
            logger.error("Rollback of user registration unsuccessful: " + e);
        }
    }

    public Registration handleRegistrationRequest(Registration registration)
    {
        if (!RegistrationSpecification.isSatisfiedBy(registration))
        {
            logger.debug("Registration does not conform to specification: " + registration.toString());
            return registration;
        }

        // do data persistent things
        handleRegistration(registration);
        if (registration.isCompleted())
        {
            logger.debug("Registered: " + registration.toString());
        }
        else
        {
            logger.error("Registration process unsuccessful: " + registration.toString());
            rollBackRegistration(registration);
        }
        return registration;
    }

    private void rollBackRegistration(Registration registration)
    {
        logger.debug("Trying a rollback.");
        EasyUser user = registration.getUser();
        try
        {
            delete(user, user);
            logger.debug("Rollback of user registration successful.");
        }
        catch (RepositoryException e)
        {
            logger.error("Rollback of user registration unsuccessful: " + e);
        }
        removeTokenFromList(user.getId());
    }

    private Registration handleRegistration(Registration registration)
    {
        // put new user in UserRepo
        EasyUser user = registration.getUser();
        try
        {
            add(user, user);
        }
        catch (RepositoryException e)
        {
            logger.error("Could not store a user for registration: ", e);
            registration.setState(Registration.State.SystemError, e);
            return registration;
        }

        try
        {
            new RegistrationConfirmation(registration).send();
        }
        catch (final ServiceException e1)
        {
            registration.setState(Registration.State.MailNotSend, e1);
        }

        // put token in list
        final String userId = registration.getUser().getId();
        final String dateTime = registration.getRequestTimeAsString();
        final String token = registration.getMailToken();
        putTokenInTokenList(userId, dateTime, token);

        // set state of registration and persist state of user
        registration.setState(Registration.State.Registered);
        registration.getUser().setState(EasyUser.State.REGISTERED);
        try
        {
            update(user, user);
        }
        catch (RepositoryException e)
        {
            logger.error("Could not update a user as REGISTERED: ", e);
            registration.setState(Registration.State.SystemError, e);
            registration.getUser().setState(null);
            return registration;
        }
        return registration;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.services.RegistrationService#validateRegistration(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public boolean validateRegistration(final String userId, final String dateTime, final String token)
    {
        boolean valid = false;
        logger.debug("userid: " + userId);
        logger.debug("dateTime: " + dateTime);
        logger.debug("token: " + token);

        valid = checkToken(userId, dateTime, token);
        if (valid)
        {
            try
            {
                EasyUser user = Data.getUserRepo().findById(userId);
                if (EasyUser.State.ACTIVE.equals(user.getState()) || user.getState() == null)
                {
                    logger.warn("Activating a user that is not in state " + EasyUser.State.REGISTERED + ". Actual state=" + user.getState());
                }
                if (EasyUser.State.BLOCKED.equals(user.getState()))
                {
                    logger.warn("Not activating a user that is in state " + EasyUser.State.BLOCKED);
                    return false;
                }
                user.setState(EasyUser.State.CONFIRMED_REGISTRATION);
                update(user, user);
                removeTokenFromList(userId);
            }
            catch (ObjectNotInStoreException e)
            {
                logger.error("The user with userId '" + userId + "' could not be found: ", e);
                removeTokenFromList(userId);
                return false;
            }
            catch (RepositoryException e)
            {
                logger.error("Error while handling checkRegistrationRequest: ", e);
                return false;
            }
        }
        logger.debug("Validation of registration of user " + userId + " completed. Valid registration=" + valid);
        return valid;
    }

    // double parameters for MutatesData.aj. 1. actor, 2. subject
    @MutatesUser
    private void update(EasyUser sessionUser, EasyUser user) throws RepositoryException
    {
        Data.getUserRepo().update(user);
    }

    // double parameters for MutatesData.aj. 1. actor, 2. subject
    @MutatesUser
    private void add(EasyUser sessionUser, EasyUser user) throws ObjectExistsException, RepositoryException
    {
        Data.getUserRepo().add(user);
    }

    // double parameters for MutatesData.aj. 1. actor, 2. subject
    @MutatesUser
    private void delete(EasyUser sessionUser, EasyUser user) throws RepositoryException
    {
        Data.getUserRepo().delete(user);
    }

}
