package nl.knaw.dans.easy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Security
{

    private static final Logger logger = LoggerFactory.getLogger(Security.class);
    private static Authz AUTHZ;

    /**
     * DO NOT USE - called by the application context. The class we are going to proxy using CGLib has to provide a
     * default constructor. Alternatively switch to JDK dynamic proxies (if that's configurable for annotations).
     */
    protected Security()
    {
        logger.debug("Created " + this);
    }

    /**
     * Constructs new Security - called by the application context.
     * 
     * @param authz
     *        the Authz implementation used
     * @throws IllegalStateException
     *         if the constructor was called during application runtime
     */
    public Security(Authz authz) throws IllegalStateException
    {
        Security.AUTHZ = authz;
        logger.debug("Created " + this);
    }

    public static Authz getAuthz()
    {
        return AUTHZ;
    }
}
