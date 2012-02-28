package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.easy.data.ext.ExternalServices;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

/** Integration test for the configuration. */
public class SubmitTester extends EasySwordServerTester
// maven should not run this test therefore the name should not start or end with test.
{
    private static final String PROPER_ZIP = new File("src/test/resources/input/data-plus-meta.zip").getPath();

    @Before // FIXME some tests stumble over easy-home if run stand-alone
    public void setupMocking() throws Exception {
        //EasyHome.setValue(System.getProperty("easy.home"));
        MockUtil.mockAll();
    }
    
    @Test // TODO test was supposed to touch AbstractNotification.send(...)
    public void submitWithoutMailer() throws Exception
    {
        final Mailer saved = ExternalServices.getMailOffice();
        new ExternalServices().setMailOffice(null);
        execute(false, false, PROPER_ZIP);
        new ExternalServices().setMailOffice(saved);
    }

    @Test
    public void submit() throws Exception
    {
        execute(false, false, PROPER_ZIP);
    }

    @Test
    public void submitVerboseNoOp() throws Exception
    {
        execute(true, true, PROPER_ZIP);
    }

    @Test
    public void submitNoOp() throws Exception
    {
        execute(false, true, PROPER_ZIP);
    }

    @Test (expected=SWORDErrorException.class)
    public void invalidZip() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/" + "metadata.xml").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void neitherMetaDataNorData() throws Throwable
    {
        execute(false, true, getZip("datasetPictures"));
    }

    @Test (expected=SWORDErrorException.class)
    public void noMetaData() throws Throwable
    {
        execute(false, true, getZip("data-only"));
    }

    @Test (expected=SWORDErrorException.class)
    public void emptyZip() throws Throwable
    {
        execute(false, true, getZip("empty"));
    }

    @Test (expected=SWORDErrorException.class)
    public void dataIsFile() throws Throwable
    {
        execute(false, true, getZip("data-is-file"));
    }

    @Test (expected=SWORDErrorException.class)
    public void metaIsFolder() throws Throwable
    {
        execute(false, true, getZip("meta-is-folder"));
    }

    @Test (expected=SWORDErrorException.class)
    public void justFolders() throws Throwable
    {
        execute(false, true, getZip("no-files-infolders"));
    }

    @Test (expected=SWORDErrorException.class)
    public void tooManyRootFolders() throws Throwable
    {
        execute(false, true, getZip("too-many-root-folers"));
    }

    @Test (expected=SWORDErrorException.class)
    public void missingMetadataFields() throws Throwable
    {
        execute(false, true, getZip("data-plus-missing-meta-fields"));
    }

    @Ignore // FIXME
    @Test 
    public void spatialMetadata() throws Throwable
    {
        execute(false, true, getZip("data-plus-spatial-metadata"));
    }

    @Test (expected=SWORDErrorException.class)
    public void unknowUser() throws Throwable
    {
        execute(MockUtil.INVALID_USER_ID,MockUtil.PASSWORD,LOCATION);
    }

    @Test (expected=SWORDErrorException.class)
    public void invalidPassword() throws Throwable
    {
        execute(MockUtil.INVALID_USER_ID,MockUtil.PASSWORD,LOCATION);
    }

    @Test (expected=SWORDErrorException.class)
    public void invalidLocationProtocol() throws Throwable
    {
        execute(MockUtil.VALID_USER_ID,MockUtil.PASSWORD,"invalid"+LOCATION);
    }

    @Test (expected=SWORDErrorException.class)
    public void locationWithoutProtocol() throws Throwable
    {
        execute(MockUtil.VALID_USER_ID,MockUtil.PASSWORD,"//"+LOCATION);
    }

    private String getZip(String string)
    {
        return new File("src/test/resources/input/" + string + ".zip").getPath();
    }

    private void execute(String invalidUserId, String password, String location) throws Exception
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(invalidUserId);
        deposit.setPassword(password);
        deposit.setLocation(location);
        deposit.setVerbose(false);
        deposit.setNoOp(true);
        deposit.setFile(new FileInputStream(PROPER_ZIP));
        
        execute(deposit);
    }

    private void execute(boolean verbose, boolean noOp, final String zip) throws Exception,
            SWORDException
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        deposit.setFile(new FileInputStream(zip));
        
        execute(deposit);
    }

    private void execute(final Deposit deposit) throws Exception
    {
        final String regexp = "-- CreationDate: .*--"; // iText generates creation date as comment, ignore that
        final String actualResults = easySwordServer.doDeposit(deposit).toString().replaceAll(regexp, "");
        assertAsExpected(actualResults, "deposit_"+deposit.isVerbose()+deposit.isNoOp()+".xml");
    }
}
