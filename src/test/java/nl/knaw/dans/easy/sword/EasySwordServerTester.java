package nl.knaw.dans.easy.sword;

import org.junit.BeforeClass;

/**
 * Fixture that creates the sword server and provides default values.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract  class EasySwordServerTester extends Tester
{
    protected static final String LOCATION = "http://localhost:8080/easy-sword/serviceDocument";
    protected static EasySwordServer easySwordServer;
    
    @BeforeClass
    public static void createSwordServer() throws Exception
    {
        easySwordServer = new EasySwordServer();
    }

}
