package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.easy.util.EasyHome;

import org.junit.Before;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDErrorException;

public class InvalidSubmitTest extends SubmitFixture
//maven should not run this test therefore the name should not start or end with test.
{
 @Before
 public void setupMocking() throws Exception {
     EasyHome.setValue(System.getProperty("easy.home"));
     MockUtil.mockAll();
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
     
     execute(deposit,location.replaceAll("\\/", "_"));
 }

 private void execute(final Deposit deposit, final String zip) throws Exception
 {
     final String regexp = "-- CreationDate: .*--"; // iText generates creation date as comment, ignore that
     final String actualResults = easySwordServer.doDeposit(deposit).toString().replaceAll(regexp, "");
     assertAsExpected(actualResults, "deposit_"+deposit.isVerbose()+deposit.isNoOp()+zip+".xml");
 }
}
