package nl.knaw.dans.easy.sword;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;

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
    private static final Logger log = LoggerFactory.getLogger(OnlineTester.class);

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

    @Test
    public void depositNoOpVerbose() throws Exception
    {
        execute(true, true);
    }

    //@Ignore("Ingest problems" /* FIXME */) 
    @Test
    public void depositRealVerbose() throws Exception
    {
        execute(false, true);
    }

    /** Tests whether the mocked dataset provides enough information to generate body of the message that confirms submission */
    @Test
    public void wrapTreatment() throws Exception
    {
        final Dataset dataset = createMockedDataset();
        EasyBusinessFacade.composeTreatment(MockUtil.USER, dataset);
        assertMockedOk(dataset, "after create notification mail content");
    }

    /** Tests whether the mocked dataset provides enough information to generate the license document */
    @Test
    public void wrapVerbose() throws Exception
    {
        final Dataset dataset = createMockedDataset();
        EasyBusinessFacade.composeLicense(MockUtil.USER, true, dataset);
        
        assertMockedOk(dataset, "after create license");
    }

    private void assertMockedOk(final Dataset dataset, final String string) throws XMLSerializationException
    {
        log.debug(string+dataset.getEasyMetadata().asXMLString());
    }

    private Dataset createMockedDataset() throws SWORDException, SWORDErrorException, FileNotFoundException
    {
        final FileInputStream inputStream = new FileInputStream("src/test/resources/input/data-plus-meta.zip");
        return new UnzipResult(inputStream).submit(MockUtil.USER, true);
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
        //log.info("submitted " + response.toString());
        log.info("submitted " + response.getHttpResponse());
        if (verbose)
            new FileOutputStream("target/tmp/license.html").write(response.getEntry().getVerboseDescription().getBytes());
    }
}
