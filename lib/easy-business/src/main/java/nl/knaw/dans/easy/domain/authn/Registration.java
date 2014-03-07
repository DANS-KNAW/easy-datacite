package nl.knaw.dans.easy.domain.authn;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.util.Messenger;

/**
 * Messenger object for user registration.
 * 
 * @author ecco Feb 12, 2009
 */
public class Registration extends Messenger<Registration.State>
{
    private static final long serialVersionUID = -4037234100764375061L;

    /**
     * Indicates the state of the registration.
     * 
     * @author ecco Feb 17, 2009
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
        InvalidData, UserIdCannotBeBlank, InitialsCannotBeBlank, FirstnameCannotBeBlank, SurnameCannotBeBlank, PasswordCannotBeBlank, EmailCannotBeBlank,
        /**
         * Rejected because the userId already exists: not registered.
         */
        UserIdNotUnique,
        /**
         * So far so good, but the registration validation mail could not be send: not registered.
         */
        MailNotSend,
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
    private final String mailToken;

    private String validationUrl;

    public Registration(final EasyUser user)
    {
        super(Registration.State.class);
        this.user = user;
        this.user.addRole(Role.USER);
        mailToken = super.createMailToken(user.getId());
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

    public String getMailToken()
    {
        return mailToken;
    }

    public String getValidationUrl()
    {
        return validationUrl;
    }

    public void setValidationUrl(String validationUrl)
    {
        this.validationUrl = validationUrl;
    }

    @Override
    public String toString()
    {
        return super.toString() + " [state=" + getState() + " user=" + (user == null ? "null" : user.toString()) + "] " + getExceptionsAsString();
    }

}
