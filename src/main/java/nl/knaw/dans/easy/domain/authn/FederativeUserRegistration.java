package nl.knaw.dans.easy.domain.authn;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.util.Messenger;

/**
 * Messenger object for user registration.
 * 
 */
public class FederativeUserRegistration extends Messenger<FederativeUserRegistration.State>
{
    private static final long serialVersionUID = -3595285611084899377L;

    private String federativeUserId;

    /**
     * Indicates the state of the registration.
     *
     */
    public enum State
    {
        /**
         * Registration is not effectuated.
         */
        NotRegistered,
        /**
         * Rejected because data is invalid, inappropriate, insufficient or indecent: not registered.
         */
        InvalidData, UserIdCannotBeBlank, InitialsCannotBeBlank, FirstnameCannotBeBlank, SurnameCannotBeBlank, EmailCannotBeBlank,
        /**
         * Rejected because the userId already exists: not registered.
         */
        UserIdNotUnique,
        /**
         * Oeps, but it happens: not registered.
         */
        SystemError,
        /**
         * Registration accepted and all necessary steps undertaken: registered.
         */
        Registered
    }

    private final EasyUser user;

    public FederativeUserRegistration(final String federativeUserId, final EasyUser user)
    {
        super(FederativeUserRegistration.State.class);
        this.federativeUserId = federativeUserId;
        this.user = user;
        this.user.addRole(Role.USER);
    }

    public String getFederativeUserId()
    {
        return federativeUserId;
    }

    public void setState(State state)
    {
        super.setState(state);
    }

    @Override
    public void setState(State state, Throwable e)
    {
        super.setState(state, e);
    }

    public EasyUser getUser()
    {
        return user;
    }

    public String getUserId()
    {
        return user.getId();
    }

    @Override
    public String toString()
    {
        return super.toString() + " [state=" + getState() + " user=" + (user == null ? "null" : user.toString()) + "] " + getExceptionsAsString();
    }

}
