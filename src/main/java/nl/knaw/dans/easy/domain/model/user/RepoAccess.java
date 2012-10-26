package nl.knaw.dans.easy.domain.model.user;

import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Domain classes sometimes need access to store or repository data access points, in order to lazily create attributes.
 * For instance: a User wants to instantiate the Groups it belongs to, a Dataset wants to instantiate the user that is
 * the depositor of the dataset.
 * <p/>
 * The overall logic of business processes remains situated in the business layer so the RepoAccessDelegator should be
 * confined to simple getter-methods like 'getUser', 'getGroups', etc.
 * 
 * @author ecco Nov 19, 2009
 */
public final class RepoAccess
{

    private static final Logger logger = LoggerFactory.getLogger(RepoAccess.class);

    private static RepoAccessDelegator DELEGATOR;

    private RepoAccess()
    {
        // never instantiate
    }

    /**
     * Set the delegate for repository access
     * 
     * @param delegator
     *        the {@link RepoAccessDelegator}
     */
    public static void setDelegator(RepoAccessDelegator delegator)
    {
        DELEGATOR = delegator;
    }

    /**
     * Get the delegate for repository access.
     * 
     * @return the {@link RepoAccessDelegator}
     * @throws ApplicationException
     *         if no delegate was set on {@link RepoAccess}
     */
    public static RepoAccessDelegator getDelegator()
    {
        if (DELEGATOR == null)
        {
            String msg = "No delegator set on " + RepoAccess.class.getName();
            logger.error(msg);
            throw new ApplicationException(msg);
        }
        return DELEGATOR;
    }

}
