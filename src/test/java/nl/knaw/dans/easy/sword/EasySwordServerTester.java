package nl.knaw.dans.easy.sword;

import org.junit.BeforeClass;
import org.purl.sword.base.SwordValidationInfo;
import org.purl.sword.base.SwordValidationInfoType;

/**
 * Fixture that creates the sword server and provides default values.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract  class EasySwordServerTester extends Tester
{
    protected static final String LOCATION = "http://mockedhost:8080/servlet/request";
    protected static EasySwordServer easySwordServer;
    
    @BeforeClass
    public static void createSwordServer() throws Exception
    {
        easySwordServer = new EasySwordServer();
    }

    protected void assertCompliant(SwordValidationInfoType level, SwordValidationInfo info) throws Exception
    {
        final StringBuffer buffer = new StringBuffer();
        info.createString(info, buffer, " ");
        if (!level.equals(info.getType()))
            throw new Exception (buffer.toString());
    }

}
