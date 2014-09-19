package nl.knaw.dans.common.lang.ldap;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.user.User;

// import nl.knaw.dans.easy.domain.exceptions.DataAccessException;

/**
 * A Data Access Point providing access to the domain object {@link User}.
 * 
 * @author ecco
 */
public interface UserRepo<T extends User> extends GenericRepo<T> {

    /**
     * Authenticate with userId and password.
     * 
     * @param userId
     *        username
     * @param password
     *        password
     * @return <code>true</code> if authenticated, <code>false</code> otherwise
     * @throws RepositoryException
     *         for system failure
     */
    boolean authenticate(String userId, String password) throws RepositoryException;

    /**
     * Find users by email.
     * 
     * @param emailAddress
     *        email
     * @return a list of users with the given email address
     * @throws RepositoryException
     *         for system failure
     */
    List<T> findByEmail(String emailAddress) throws RepositoryException;

    Map<String, String> findByCommonNameStub(String stub, long maxCount) throws RepositoryException;

    /**
     * Determine if there is a password stored for the user with the given uid. Some users might not need a password stored in this user repository, for
     * instance when authenticated by an external system (federation).
     * 
     * @param userId
     *        username
     * @return <code>true</code> if a password is stored, <code>false</code> otherwise
     * @throws RepositoryException
     */
    boolean isPasswordStored(String userId) throws RepositoryException;
}
