package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.easy.sword.util.MockUtil;
import nl.knaw.dans.easy.sword.util.SubmitFixture;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

public class TestFailingSubmit extends SubmitFixture {
    @Before
    public void setupMocking() throws Exception {
        MockUtil.mockAll();
    }

    @After
    public void clearTmp() throws Exception {
        new File(Context.getUnzip()).delete();
    }

    @Test(expected = SWORDErrorException.class)
    public void invalidZip() throws Throwable {
        execute(false, true, new File(INPUT_DIR + "metadata.xml").getPath());
    }

    @Test(expected = SWORDErrorException.class)
    public void neitherMetaDataNorData() throws Throwable {
        execute(false, true, getZip("datasetPictures.zip"));
    }

    @Test(expected = SWORDErrorException.class)
    public void emptyZip() throws Throwable {
        execute(false, true, getZip("empty.zip"));
    }

    @Test(expected = SWORDErrorException.class)
    public void justFolders() throws Throwable {
        execute(false, true, getZip("no-files-infolders.zip"));
    }

    @Test(expected = SWORDErrorException.class)
    public void missingMetadataFields() throws Throwable {
        execute(false, true, getZip("data-plus-missing-meta-fields.zip"));
    }

    @Ignore
    @Test(expected = SWORDErrorException.class)
    public void negativeEmbargo() throws Throwable {
        execute(false, true, getZip("negative-embargo.zip"));
    }

    @Ignore
    @Test(expected = SWORDErrorException.class)
    public void longEmbargo() throws Throwable {
        execute(false, true, getZip("long-embargo.zip"));
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void unknowUser() throws Throwable {
        execute(MockUtil.INVALID_USER_ID, MockUtil.PASSWORD, LOCATION);
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void invalidPassword() throws Throwable {
        execute(MockUtil.INVALID_USER_ID, MockUtil.PASSWORD, LOCATION);
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void noUsernoPassword() throws Throwable {
        execute(null, null, LOCATION);
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void noUser() throws Throwable {
        execute(null, MockUtil.PASSWORD, LOCATION);
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void emptyUseremptyPassword() throws Throwable {
        execute("", "", LOCATION);
    }

    @Test(expected = SWORDAuthenticationException.class)
    public void emptyUser() throws Throwable {
        execute("", MockUtil.PASSWORD, LOCATION);
    }

    private void execute(boolean verbose, boolean noOp, final String zip) throws Exception, SWORDException {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        deposit.setFile(new FileInputStream(zip));

        execute(deposit, "_" + new File(zip).getName().replace(".zip", ""));
    }

    private void execute(String userId, String password, String location) throws Exception {
        final Deposit deposit = new Deposit();
        deposit.setUsername(userId);
        deposit.setPassword(password);
        deposit.setLocation(location);
        deposit.setVerbose(false);
        deposit.setNoOp(true);
        deposit.setFile(new FileInputStream(PROPER_ZIP));

        execute(deposit, location.replaceAll("\\/", "_"));
    }
}
