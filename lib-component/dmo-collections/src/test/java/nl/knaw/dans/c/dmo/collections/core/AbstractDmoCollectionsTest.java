package nl.knaw.dans.c.dmo.collections.core;


import java.net.URL;

import nl.knaw.dans.c.dmo.collections.xml.JiBXCollectionConverterTest;
import nl.knaw.dans.i.security.SecurityAgent;

import org.easymock.EasyMock;

public abstract class AbstractDmoCollectionsTest extends EasyMock
{
    
    public static URL getUrlForXml()
    {
        return JiBXCollectionConverterTest.class.getResource("class-resources/jibcol.xml");
    }
    
    public static void doNotInitialize()
    {
        Settings.reset();
    }
    
    public static void initializeWithNoSecurity()
    {
        MockCollectionsConfiguration configuration = new MockCollectionsConfiguration();
        Settings.reset();
        Settings.instance().configure(configuration);
        Settings.instance().setSecurityEnabled(false);
        Settings.instance().setAllowSecuredMethods(true);
    }
    
    public static void initializeWithSecurity(SecurityAgent...agents)
    {
        MockCollectionsConfiguration configuration = new MockCollectionsConfiguration();
        Settings.reset();
        Settings.instance().configure(configuration);
        Settings.instance().putSecurityAgents(agents);
    }
    

}
