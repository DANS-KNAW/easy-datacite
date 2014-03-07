package nl.knaw.dans.common.lang.user;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.ldap.DateTimeTranslator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * User of a DANS application.
 */
@LdapObject(objectClasses = {"dansUser", "inetOrgPerson", "organizationalPerson", "person"})
public class UserImpl extends PersonVO implements User
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2895529338320356222L;

    /**
     * The unique username (uid).
     */
    @LdapAttribute(id = "uid", required = true)
    private String userId;

    /**
     * Attribute password.
     */
    @LdapAttribute(id = "userPassword", oneWayEncrypted = true)
    private String password;

    /**
     * Attribute encrypted password.
     */
    @LdapAttribute(id = "userPassword", encrypted = "SHA")
    private String shaEncryptedPassword;

    @LdapAttribute(id = "dansAcceptConditionsOfUse")
    private boolean acceptConditionsOfUse;

    @LdapAttribute(id = "dansNewsletter")
    private boolean optsForNewsletter = true; // Users have to explicitly opt out

    @LdapAttribute(id = "dansState")
    private State state;

    @LdapAttribute(id = "dansLastLogin", valueTranslator = DateTimeTranslator.class)
    private DateTime lastLogin;

    /**
     * Default constructor.
     */
    public UserImpl()
    {
        super();
    }

    public UserImpl(String userId)
    {
        this.userId = userId;
    }

    /**
     * Constructor with values.
     * 
     * @param userId
     *        UserImpl id.
     * @param email
     *        email address.
     */
    public UserImpl(final String userId, final String email)
    {
        this.userId = userId;
        setEmail(email);
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#getUserId()
     */
    public String getId()
    {
        return this.userId;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#setUserId(java.lang.String)
     */
    public void setId(final String userId)
    {
        this.userId = userId;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#getPassword()
     */
    public String getPassword()
    {
        return this.password;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#setPassword(java.lang.String)
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#getEncryptedPassword()
     */
    public String getSHAEncryptedPassword()
    {
        return this.shaEncryptedPassword;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.easy.business.model.User#setEncryptedPassword()
     */
    public void setSHAEncryptedPassword(final String shaEncryptedPassword)
    {
        this.shaEncryptedPassword = shaEncryptedPassword;
    }

    public boolean getAcceptConditionsOfUse()
    {
        return acceptConditionsOfUse;
    }

    public void setAcceptConditionsOfUse(boolean acceptConditionsOfUse)
    {
        this.acceptConditionsOfUse = acceptConditionsOfUse;
    }

    public boolean getOptsForNewsletter()
    {
        return optsForNewsletter;
    }

    public void setOptsForNewsletter(boolean optsForNewsletter)
    {
        this.optsForNewsletter = optsForNewsletter;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public DateTime getLastLoginDate()
    {
        return lastLogin;
    }

    public boolean isFirstLogin()
    {
        return State.CONFIRMED_REGISTRATION.equals(state);
    }

    public boolean isUserInfoUpdateRequired()
    {
        return isFirstLogin(); // or this, and that etc.
    }

    public void synchronizeOn(User otherUser)
    {
        super.synchronizeOn(otherUser);
    }

    public boolean isQualified()
    {
        return State.ACTIVE.equals(state) || State.CONFIRMED_REGISTRATION.equals(state);
    }

    public boolean isActive()
    {
        return State.ACTIVE.equals(state);
    }

    public boolean isBlocked()
    {
        return State.BLOCKED.equals(state);
    }

    /**
     * String representation.
     * 
     * @return string representation
     */
    @Override
    public String toString()
    {
        return super.toString() + " [" + userId + "] " + this.getCommonName() + " state=" + state;
    }

    /**
     * Test if object is equal.
     * 
     * @param obj
     *        object to test
     * @return true if object is equal.
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean equals = false;
        if (obj != null)
        {
            if (obj == this)
            {
                equals = true;
            }
            else
            {
                if (obj.getClass() == this.getClass())
                {
                    final UserImpl otherUser = (UserImpl) obj;
                    equals = new EqualsBuilder().append(this.userId, otherUser.userId).append(this.getEmail(), otherUser.getEmail())
                            .append(this.getCommonName(), otherUser.getCommonName()).append(this.state, otherUser.state).isEquals();
                }
            }
        }

        return equals;
    }

    /**
     * Return hashCode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(1, 3).append(this.userId).append(this.getEmail()).append(this.getCommonName()).append(this.state).toHashCode();
    }

    public boolean isAnonymous()
    {
        return false;
    }

}
