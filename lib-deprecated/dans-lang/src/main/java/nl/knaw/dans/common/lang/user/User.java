/**
 *
 */
package nl.knaw.dans.common.lang.user;

import org.joda.time.DateTime;

// import nl.knaw.dans.easy.dai.ldap.user.RepoEntry;

/**
 * Interface for the business user object.
 */
public interface User extends Person, RepoEntry
{

    public enum State
    {
        /**
         * The user has successfully registered, but has not validated the registration; the account
         * cannot be used (yet).
         */
        REGISTERED,
        /**
         * The user has confirmed the registration and the confirmation was valid; the user has not
         * logged in for the first time.
         */
        CONFIRMED_REGISTRATION,
        /**
         * The user has a valid registration; the account can be used.
         */
        ACTIVE,
        /**
         * The user is blocked; the account cannot be used.
         */
        BLOCKED
    }

    /**
     * Setter for userId (uid).
     * 
     * @param userId
     *        the userId to set
     */
    void setId(final String userId);

    /**
     * Getter for password (userPassword).
     * 
     * @return the password
     */
    String getPassword();

    /**
     * Setter for password (userPassword).
     * 
     * @param password
     *        the password to set
     */
    void setPassword(final String password);

    /**
     * Setter for ENCRYPTED password (userPassword). Use only when the password is already encrypted
     * !!!!!
     * 
     * @param encryptedPassword
     *        the ENCRYPTED password to set
     */
    void setSHAEncryptedPassword(final String encryptedPassword);

    /**
     * Getter for ENCRYPTED password (userPassword).
     * 
     * @return the encryptedPassword
     */
    String getSHAEncryptedPassword();

    /**
     * Set the state of the account.
     * 
     * @param state
     *        state of the account
     */
    void setState(State state);

    /**
     * Get the state of the account.
     * 
     * @return state of the account
     */
    State getState();

    /**
     * Get date of last login.
     * 
     * @return date of last login
     */
    DateTime getLastLoginDate();

    /**
     * Is this user fully qualified to partake in actions. A qualified user has state
     * {@link State#ACTIVE} or {@link State#CONFIRMED_REGISTRATION}.
     * 
     * @see User.State
     * @return <code>true</code> if qualified, <code>false</code> otherwise
     */
    boolean isQualified();

    /**
     * Is this user active? An active user has {@link State#ACTIVE}.
     * 
     * @return <code>true</code> if active, <code>false</code> otherwise
     */
    boolean isActive();

    /**
     * Is this user blocked? A blocked user has {@link State#BLOCKED}.
     * 
     * @return <code>true</code> if blocked, <code>false</code> otherwise
     */
    boolean isBlocked();

    boolean isFirstLogin();

    boolean isUserInfoUpdateRequired();

    void synchronizeOn(User otherUser);

    /**
     * Accepts the General DANS Conditions of Use at the time of registration.
     * 
     * @return <code>true</code> if accepted, <code>false</code> otherwise
     */
    boolean getAcceptConditionsOfUse();

    /**
     * Accept the General DANS Conditions of Use at the time of registration.
     * 
     * @param accept
     *        <code>true</code> if accepting the conditions of use, <code>false</code> otherwise
     */
    void setAcceptConditionsOfUse(boolean accept);

    boolean getOptsForNewsletter();

    void setOptsForNewsletter(boolean optsForNewsletter);

    boolean isAnonymous();

}
