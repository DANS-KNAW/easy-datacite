package nl.knaw.dans.easy.sword;

import java.io.FileInputStream;

import org.junit.Test;
import org.purl.sword.base.Deposit;

public class SubmitTest extends EasySwordServerTester
{
    @Test
    public void submit() throws Exception
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(VALID_USER_ID);
        deposit.setPassword(PASSWORD);
        deposit.setLocation(LOCATION);
        
        // TODO what next?
        deposit.setFile(new FileInputStream(META_DATA_FILE));
        deposit.setContentDisposition(ZIP_FILE.getPath());

        assertAsExpected(easySwordServer.doDeposit(deposit).toString(), "deposit.xml");
    }

}
