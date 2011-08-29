package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

public class SubmitTest extends EasySwordServerTester
{
    @BeforeClass
    public static void setupMocking() throws Exception {
        MockUtil.mockAll();
    }

    @Ignore("WorkReporter.workStart and .workEnd not called by mocked itemService.addDirectoryContents" /* FIXME */) 
    @Test
    public void submit() throws Exception
    {
        execute(false, false);
    }

    @Test
    public void submitVerboseNoOp() throws Exception
    {
        execute(true, true);
    }

    @Test
    public void submitNoOp() throws Exception
    {
        execute(false, true);
    }

    private void execute(boolean verbose, boolean noOp) throws FileNotFoundException, Exception, SWORDAuthenticationException, SWORDErrorException,
            SWORDException
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        
        final String zip = new File("src/test/resources/input/data-plus-meta.zip").getPath();
        deposit.setFile(new FileInputStream(zip));
        
        assertAsExpected(easySwordServer.doDeposit(deposit).toString(), "deposit_"+verbose+noOp+".xml");
    }
}
