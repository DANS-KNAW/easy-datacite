package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.easy.sword.util.MockUtil;
import nl.knaw.dans.easy.sword.util.SubmitFixture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SwordValidationInfo;

public class TestFailingSubmit extends SubmitFixture
//maven should not run this test therefore the name should not start or end with test.
{
 @Before
 public void setupMocking() throws Exception {
     MockUtil.mockAll();
 }
 @After
 public void cleaTmp() throws Exception {
     new File(Context.getUnzip()).delete();
 }
 
 @Test (expected=SWORDErrorException.class)
 public void invalidZip() throws Throwable
 {
     execute(false, true, new File(INPUT_DIR + "metadata.xml").getPath());
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

 @Test (expected=SWORDErrorException.class)
 public void tooLongPath() throws Throwable
 {
     execute(false, true, getZip("max-path-exceeded"));
 }

 @Test (expected=SWORDErrorException.class)
 public void negativeEmbargo() throws Throwable
 {
     execute(false, true, getZip("negative-embargo"));
 }

 @Test (expected=SWORDErrorException.class)
 public void longEmbargo() throws Throwable
 {
     execute(false, true, getZip("long-embargo"));
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void unknowUser() throws Throwable
 {
     execute(MockUtil.INVALID_USER_ID,MockUtil.PASSWORD,LOCATION);
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void invalidPassword() throws Throwable
 {
     execute(MockUtil.INVALID_USER_ID,MockUtil.PASSWORD,LOCATION);
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void noUsernoPassword() throws Throwable
 {
     execute(null,null,LOCATION);
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void noUser() throws Throwable
 {
     execute(null,MockUtil.PASSWORD,LOCATION);
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void emptyUseremptyPassword() throws Throwable
 {
     execute("","",LOCATION);
 }

 @Test (expected=SWORDAuthenticationException.class)
 public void emptyUser() throws Throwable
 {
     execute("",MockUtil.PASSWORD,LOCATION);
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

 private SwordValidationInfo execute(String userId, String password, String location) throws Exception
 {
     final Deposit deposit = new Deposit();
     deposit.setUsername(userId);
     deposit.setPassword(password);
     deposit.setLocation(location);
     deposit.setVerbose(false);
     deposit.setNoOp(true);
     deposit.setFile(new FileInputStream(PROPER_ZIP));
     
     return execute(deposit,location.replaceAll("\\/", "_"));
 }
}
