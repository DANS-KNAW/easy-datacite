package nl.knaw.dans.easy.sword;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class OnlineTester
{
    private static final Logger logger = LoggerFactory.getLogger(OnlineTester.class);

    @BeforeClass
    public static void launchServer()
    {
        new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/include-easy.xml");
    }

    @Test
    public void depositNoOp() throws Exception
    {
        execute(true, false);
    }

    @Ignore("fix mocking")
    @Test
    public void depositNoOpVerbose() throws Exception
    {
        execute(true, true);
    }

    @Test
    public void depositRealVerbose() throws Exception
    {
        execute(false, true);
    }

    private void execute(final boolean noOp, final boolean verbose) throws FileNotFoundException, SWORDAuthenticationException, SWORDErrorException,
            SWORDException, IOException
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername("migration");
        deposit.setPassword("migration");
        deposit.setFile(new FileInputStream("src/test/resources/input/data-plus-meta.zip"));
        deposit.setContentType("application/zip");
        deposit.setLocation("http://a:a@localhost:8080/easy-sword/deposit");
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        final DepositResponse response = new EasySwordServer().doDeposit(deposit);
        logger.info("submitted " + response.toString());
        logger.info("submitted " + response.getHttpResponse());
        if (verbose)
            new FileOutputStream("target/tmp/license.html").write(response.getEntry().getVerboseDescription().getBytes());
    }
}
