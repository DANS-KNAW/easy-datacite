package nl.knaw.dans.easy.sword;

import java.io.FileInputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;

/** Abstract, implemented with a mocked and an online version */
public abstract class SubmitTester extends EasySwordServerTester
{
    @Ignore("mock system DateTime to make the test repeatable")
    @Test
    public void submit() throws Exception
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        
        // TODO what next?
        deposit.setFile(new FileInputStream(META_DATA_FILE));
        deposit.setContentDisposition(ZIP_FILE.getPath());

        assertAsExpected(easySwordServer.doDeposit(deposit).toString(), "deposit.xml");
    }
}
