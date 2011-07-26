package nl.knaw.dans.easy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyAuthz implements Authz
{
    
    private static final Logger logger = LoggerFactory.getLogger(DummyAuthz.class);

    public SecurityOfficer getSecurityOfficer(String item)
    {
        logger.warn("Method getSecurityOfficer(String) called, on DummyAuthz!!!!");
        SecurityOfficer officer = new DummySecurityOfficer();
        return officer;
    }

    public boolean hasSecurityOfficer(String item)
    {
        return false;
    }

    public boolean isProtectedPage(String pageName)
    {
        return false;
    }

}
