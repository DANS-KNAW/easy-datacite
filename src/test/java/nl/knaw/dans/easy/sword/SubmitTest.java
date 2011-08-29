package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;

public class SubmitTest extends EasySwordServerTester
{
    @BeforeClass
    public static void setupMocking() throws Exception {
        new MockUtil().mockAll();
    }

    @Ignore("WorkReporter.workStart and .workEnd not called by mocked itemService.addDirectoryContents" /* FIXME */) 
    @Test
    public void submit() throws Exception
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        
        final String zip = new File("src/test/resources/input/data-plus-meta.zip").getPath();
        deposit.setFile(new FileInputStream(zip));
        
        //TODO the private method easySwordServer.getUser should be mocked or overridden
        assertAsExpected(easySwordServer.doDeposit(deposit).toString(), "deposit.xml");
    }
}
