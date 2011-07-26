package nl.knaw.dans.common.ldap.ds;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandAloneDS implements DirContextSupplier
{

    /**
     * Logger for logging.
     */
    private static final Logger              logger               = LoggerFactory.getLogger(StandAloneDS.class);

    private String providerURL;
    private String securityPrincipal;
    private String securityCredentials;

    public String getProviderURL()
    {
        if (providerURL == null)
        {
            final String msg = "Missing providerURL: null";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        return providerURL;
    }

    public void setProviderURL(final String providerURL)
    {
        this.providerURL = providerURL;
    }

    public String getSecurityPrincipal()
    {
        if (securityPrincipal == null)
        {
            final String msg = "Missing securityPrincipal: null";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        return securityPrincipal;
    }

    public void setSecurityPrincipal(final String securityPrincipal)
    {
        this.securityPrincipal = securityPrincipal;
    }

    public String getSecurityCredentials()
    {
        if (securityCredentials == null)
        {
            final String msg = "Missing securityCredentials: null";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        return securityCredentials;
    }

    public void setSecurityCredentials(final String securityCredentials)
    {
        this.securityCredentials = securityCredentials;
    }

    
    public DirContext getDirContext() throws NamingException
    {
        // InitialDirContext is not thread safe.
        return new InitialDirContext(getEnvironment());
    }


    private Hashtable<String, String> getEnvironment()
    {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Constants.CONTEXT_FACTORY);
        env.put(Context.SECURITY_AUTHENTICATION, Constants.SIMPLE_AUTHENTICATION);

        env.put(Context.PROVIDER_URL, getProviderURL());
        env.put(Context.SECURITY_PRINCIPAL, getSecurityPrincipal());
        env.put(Context.SECURITY_CREDENTIALS, getSecurityCredentials());
        return env;
    }

}
