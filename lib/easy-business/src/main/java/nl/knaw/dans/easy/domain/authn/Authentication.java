package nl.knaw.dans.easy.domain.authn;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.util.Messenger;

/**
 * Messenger object for user authentication.
 * 
 * @author ecco Feb 18, 2009
 */
public class Authentication extends Messenger<Authentication.State>
{
    /**
     * State of the authentication.
     * 
     * @author ecco Mar 14, 2009
     */
    public enum State
    {
        /**
         * Is at start.
         */
        NotAuthenticated,
        /**
         * Has insufficient data.
         */
        InsufficientData,
        /**
         * No username.
         */
        UserIdConnotBeBlank,
        /**
         * No credentials.
         */
        CredentialsCannotBeBlank,
        /**
         * Could not be authenticated.
         */
        InvalidUsernameOrCredentials,
        /**
         * UserId not found.
         */
        NotFound,
        /**
         * User is not qualified.
         * 
         * @see User.State
         */
        NotQualified,
        /**
         * Exception state.
         */
        SystemError,
        /**
         * Final state: is authenticated.
         */
        Authenticated
    }

    public static final String PROP_USER_ID = "userId";

    public static final String PROP_CREDENTIALS = "credentials";

    private static final long serialVersionUID = -967149522733767123L;

    private String userId;
    private String credentials;
    private EasyUser user;

    public Authentication()
    {
        super(Authentication.State.class);
    }

    protected Authentication(final String userId, final String credentials)
    {
        super(Authentication.State.class);
        this.userId = userId;
        this.credentials = credentials;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(final String userId)
    {
        this.userId = userId;
    }

    public String getCredentials()
    {
        return credentials;
    }

    public void setCredentials(final String credentials)
    {
        this.credentials = credentials;
    }

    public void setState(final State state)
    {
        super.setState(state);
    }

    public void setState(final State state, final Throwable e)
    {
        super.setState(state, e);
    }

    public boolean checkCredentials(final String userId, final String credentials)
    {
        boolean valid = true;
        valid &= this.userId.equals(userId);
        valid &= this.credentials.equals(credentials);
        return valid;
    }

    public EasyUser getUser()
    {
        return user;
    }

    public void setUser(final EasyUser user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return super.toString() + " [state=" + getState() + " userId=" + userId + " user=" + (user == null ? "null" : user.toString()) + "] "
                + getExceptionsAsString();
    }

}
