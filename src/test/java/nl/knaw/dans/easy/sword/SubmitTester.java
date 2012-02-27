package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.easy.data.ext.ExternalServices;

import org.junit.Before;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

/** Integration test for the configuration. */
public class SubmitTester extends EasySwordServerTester
// maven should not run this test therefore the name should not start or end with test.
{
    private static final String PROPER_ZIP = new File("src/test/resources/input/data-plus-meta.zip").getPath();

    @Before
    public void setupMocking() throws Exception {
        System.setProperty("easy.home", "../easy-home");
        MockUtil.mockAll();
    }
    
    @Test // FIXME test was supposed to touch AbstractNotification.send(...)
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
        execute(false, true, new File("src/test/resources/input/metadata.xml").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void neitherMetaDataNorData() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/datasetPictures.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void noMetaData() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/data-only.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void emptyZip() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/empty.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void dataIsFile() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/data-is-file.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void metaIsFolder() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/meta-is-folder.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void justFolders() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/no-files-infolders.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void tooManyRootFolders() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/too-many-root-folers.zip").getPath());
    }

    @Test (expected=SWORDErrorException.class)
    public void missingMetadataFields() throws Throwable
    {
        execute(false, true, new File("src/test/resources/input/data-plus-missing-meta-fields.zip").getPath());
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
