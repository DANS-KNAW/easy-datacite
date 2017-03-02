package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.sword.util.MockUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDErrorException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knaw.dans.easy.sword.util.MockUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class TestDeposit {

    public static final String INVALID_ZIP_MESSAGE = "Expecting a folder with files and a file with one of the names: DansDatasetMetadata.xml (preferred metadata format)";

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
            // TODO rather: Expecting a !!ZIP!! with ..., link to sword packaging document?
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void nonsenseDdmInZip() throws Exception {
        // other DDM problems are tested in a class of its own
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream);
        zip.putNextEntry(new ZipEntry("DansDatasetMetadata.xml"));
        zip.write("<ddm>".getBytes());
        zip.closeEntry();
        zip.putNextEntry(new ZipEntry("data/blabla.txt"));
        zip.write("blabla".getBytes());
        zip.closeEntry();
        zip.close();
        Deposit request = mockRequest(VALID_USER_ID);
        request.setFile(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("Could not create EMD from DDM"));
        }
    }

    @Test
    public void nonDataFolderInZip() throws Exception {
        // other DDM problems are tested in a class of its own
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream);
        zip.putNextEntry(new ZipEntry("DansDatasetMetadata.xml"));
        zip.write("<ddm>".getBytes());
        zip.closeEntry();
        zip.close();
        Deposit request = mockRequest(VALID_USER_ID);
        request.setFile(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString(INVALID_ZIP_MESSAGE));
        }
    }

    @Test
    public void tooLongPathInZip() throws Exception {
        // other DDM problems are tested in a class of its own
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream);
        zip.putNextEntry(new ZipEntry("DansDatasetMetadata.xml"));
        zip.write("<ddm>".getBytes());
        zip.closeEntry();
        zip.putNextEntry(new ZipEntry("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890/blabla.txt"));
        zip.write("blabla".getBytes());
        zip.closeEntry();
        zip.close();
        Deposit request = mockRequest(VALID_USER_ID);
        request.setFile(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        try {
            new EasySwordServer().doDeposit(request);
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("Failed to unzip deposited file"));
        }
    }

    private Deposit mockRequest(String userId) throws Exception {
        MockUtil.mockUser();
        MockUtil.mockContext();
        new Context().setUnzip("target/deposit.zip");
        Deposit request = new Deposit();
        request.setUsername(userId);
        request.setPassword(PASSWORD);
        request.setLocation("");
        return request;
    }
}
