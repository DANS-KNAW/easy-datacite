package nl.knaw.dans.easy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Security
{

    private static final Logger logger         = LoggerFactory.getLogger(Security.class);
    private static boolean      locked;
    private static Authz        AUTHZ;

    /**
     * DO NOT USE - called by the application context. The class we are going to proxy using CGLib has to provide a
     * default constructor. Alternatively switch to JDK dynamic proxies (if that's configurable for annotations).
     */
    protected Security()
    {
        if (locked)
        {
            final String msg = "Illegal constructor call: The Security class cannot be instantiated.";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
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
        if (locked)
        {
            final String msg = "Illegal constructor call: The Security class cannot be instantiated.";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        Security.AUTHZ = authz;
        logger.debug("Created " + this);
    }

    /**
     * Lock Security. Can be called by a BeanPostProcessor to prevent further changes. All constructor calls and setter
     * methods will throw an IllegalStateException afterwards.
     */
    public void lock()
    {
        locked = true;
        logger.info(this + " has been locked. Authz=" + getAuthz());
    }

    /**
     * Unlock Security.
     */
    void unlock()
    {
        locked = false;
        logger.debug(this + " has been unlocked.");
    }

    public static Authz getAuthz()
    {
        return AUTHZ;
    }
}
