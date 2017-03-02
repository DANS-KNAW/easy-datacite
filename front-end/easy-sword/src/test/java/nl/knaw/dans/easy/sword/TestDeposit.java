package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.sword.util.InMemoryZip;
import nl.knaw.dans.easy.sword.util.MockUtil;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;

import java.io.ByteArrayInputStream;
import java.io.File;

import static nl.knaw.dans.easy.sword.util.MockUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class TestDeposit {

    private static final String INVALID_ZIP_MESSAGE = "Expecting a folder with files and a file with one of the names: DansDatasetMetadata.xml (preferred metadata format)";
    private static final String PATH_WITH_MAX_LENGTH = "1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/1234567890/12345/blabla.txt";
    private static final String TOO_LONG_PATH = PATH_WITH_MAX_LENGTH.replace("/bla","6/bla");
    private InMemoryZip zip;

    @Test
    public void unknownUser() throws Exception {
        Deposit request = mockRequest(INVALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDAuthenticationException e) {
            assertThat(e.getMessage(), containsString("invalid username [nobody] or password"));
        }
    }

    @Test
    public void userWithoutRights() throws Exception {
        Deposit request = mockRequest(UNAUTHORIZED_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDAuthenticationException e) {
            assertThat(e.getMessage(), containsString("somebodyunauthorized not allowed"));
        }
    }

    @Test
    public void mediated() throws Exception {
        Deposit request = mockRequest(VALID_USER_ID);
        request.setOnBehalfOf("anyone");
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("Mediated deposit not allowed to this collection"));
        }
    }

    @Test
    public void plainTextAsZip() throws Exception {
        Deposit request = mockRequest(VALID_USER_ID);
        request.setFile(new ByteArrayInputStream("blabla".getBytes()));
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            // TODO rather: "Expecting a !!ZIP!! with ...", link to sword packaging document?
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void nonsenseDdmInZip() throws Exception {
        // other DDM problems are tested in a class of its own
        zip.add("DansDatasetMetadata.xml","<ddm>".getBytes());
        zip.add(PATH_WITH_MAX_LENGTH, "blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("Could not create EMD from DDM"));
        }
    }

    @Test
    public void tooLongPathInZip() throws Exception {
        zip.add("DansDatasetMetadata.xml","<ddm>".getBytes());
        zip.add(TOO_LONG_PATH,"blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("path name exceeds 247 characters"));
        }
    }

    @Test
    public void tooManyRootFolders() throws Exception {
        zip.add("DansDatasetMetadata.xml","<ddm>".getBytes());
        zip.add("data/x.txt","blabla".getBytes());
        zip.add("moredata/x.txt","blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void tooManyRootFiles() throws Exception {
        zip.add("DansDatasetMetadata.xml","<ddm>".getBytes());
        zip.add("data/x.txt","blabla".getBytes());
        zip.add("moredata.txt","blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void metadataIsFolder() throws Exception {
        zip.add("DansDatasetMetadata.xml/blabla","<ddm>".getBytes());
        zip.add("data/x.txt","blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void dataIsFile() throws Exception {
        zip.add("DansDatasetMetadata.xml","<ddm>".getBytes());
        zip.add("data","blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void noDDM() throws Exception {
        zip.add("data","blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void nonDataFolderInZip() throws Exception {
        zip.add("DansDatasetMetadata.xml", "blabla".getBytes());
        Deposit request = mockRequest(VALID_USER_ID);
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Before
    public void createInMemoryZip(){
        zip = new InMemoryZip();
    }

    @After
    public void cleanUp() throws Exception {
        FileUtils.deleteDirectory(new File(Context.getUnzip()));
    }

    private Deposit mockRequest(String userId) throws Exception {
        MockUtil.mockUser();
        MockUtil.mockContext();
        new Context().setUnzip("target/tmp");
        Deposit request = new Deposit();
        request.setUsername(userId);
        request.setPassword(PASSWORD);
        request.setLocation("");
        request.setFile(zip.toInputStream());
        return request;
    }
}
