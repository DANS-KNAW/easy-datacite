package nl.knaw.dans.c.dmo.collections.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.config.Configuration;
import nl.knaw.dans.i.security.SecurityAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings
{
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    
    private static Settings instance;
    
    private final Map<String, SecurityAgent> securityAgents = 
        Collections.synchronizedMap(new HashMap<String, SecurityAgent>());
    private  boolean securityEnabled = true;
    private boolean allowSecuredMethods;
    private String contentModelOAISet;
    
    public static Settings instance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }
        return instance;
    }
    
    protected static void reset()
    {
        instance = null;
    }
    
    private Settings()
    {
        // singleton
    }
    
    public void configure(Configuration configuration)
    {
        setContentModelOAISet(configuration.getOAISetContentModelId());        

        for (DmoNamespace namespace : configuration.getCollectionNamespaces())
        {
            registerDmoNamespace(namespace);
        }
        putSecurityAgents(configuration.getSecurityAgents());
    }
    
    public void setContentModelOAISet(DmoStoreId dmoStoreId)
    {
        contentModelOAISet = dmoStoreId.getStoreId();
        logger.info("Content Model for OAI sets set to " + dmoStoreId.getStoreId());
    }
    
    public String getContentModelOAISet()
    {
        if (contentModelOAISet == null)
        {
            throw new IllegalStateException("The id of the content model for OAI sets was not set.");
        }
        return contentModelOAISet;
    }
    
    public void registerDmoNamespace(DmoNamespace namespace)
    {
        DmoFactory<DmoCollection> factory = new DmoCollectionFactory(namespace);
        AbstractDmoFactory.register(factory);
        logger.info("Registered DmoFactory for DmoNamespace " + namespace);
    }
    
    public void putSecurityAgents(List<SecurityAgent> agents)
    {
        synchronized (securityAgents)
        {
            for (SecurityAgent agent : agents)
            {
                securityAgents.put(agent.getSecurityId(), agent);
                logger.info("Registered SecurityAgent for secured operation " + agent.getSecurityId());
            }
        }
    }

    public void putSecurityAgents(SecurityAgent...agents)
    {
        synchronized (securityAgents)
        {
            for (SecurityAgent agent : agents)
            {
                securityAgents.put(agent.getSecurityId(), agent);
            }
        }
    }
    
    public void setSecurityEnabled(boolean enabled)
    {
        securityEnabled = enabled;
    }

    public void setAllowSecuredMethods(boolean allowSecuredMethods)
    {
        this.allowSecuredMethods = allowSecuredMethods;
    }

    public SecurityAgent getAgentFor(String securityId)
    {
        if (securityEnabled)
        {
            synchronized (securityAgents)
            {
                return securityAgents.get(securityId);
            }
        }
        else
        {
            logger.warn("                                                         ");
            logger.warn("*********************************************************");
            logger.warn("**************** SECURITY IS NOT ENABLED ****************");
            logger.warn("*                            ^^^                        *");
            logger.warn("*********************************************************");
            logger.warn("                                                         ");
            logger.info("Method is secured: " + securityId);
            logger.info("SecurityAgent.isAllowed will return " + allowSecuredMethods);
            return new SecurityAgent()
            {
                
                @Override
                public boolean isAllowed(String ownerId, Object... args)
                {
                    return allowSecuredMethods;
                }
                
                @Override
                public String getSecurityId()
                {
                    return "whatever";
                }
            };
        }
    }

}
