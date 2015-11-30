package nl.knaw.dans.easy.pakbonpreprocess;

import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.easy.pakbonpreprocess.Converter;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.commons.io.FileUtils;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.Validation;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
public class ConverterTest {
    private static final Logger logger = LoggerFactory.getLogger(ConverterTest.class);

    public static final String TESTINPUT_DIR_STR = "src/test/resources/test-files";
    public static final String TESTOUTPUT_DIR_STR = "target/test-output";
    public static final String VALID_DIR_STR = "valid";
    public static final String INVALID_DIR_STR = "invalid";

    public static File admFile;

    @BeforeClass
    public static void beforeTestClass() {
        new ResourceLocator(new FileSystemHomeDirectory(new File("src/test/resources")));
    }

    @BeforeClass
    public static void setup() {
        setupTestFiles();

        // not a mock just a real file
        admFile = new File("src/test/resources/administrative-metadata.xml");
    }

    /**
     * Test if we have a valid pakbon file that it is transformed(converted) to an emd file
     * 
     * @throws Exception
     */
    @Test
    public void convertValidPakbon() throws Exception {
        // mock validation, so we don't connect to the SIKB site
        Validation validationMock = PowerMock.createMock(Validation.class);
        EasyMock.expect(validationMock.getValidXml()).andReturn(true).anyTimes(); // always OK
        EasyMock.expect(validationMock.getMessages()).andReturn(null).anyTimes(); // never any messages

        ValidateXmlResponse responseMock = PowerMock.createMock(ValidateXmlResponse.class);
        EasyMock.expect(responseMock.getValidation()).andReturn(validationMock).anyTimes();

        PakbonValidator pbvalidatorMock = PowerMock.createMock(PakbonValidator.class);
        EasyMock.expect(pbvalidatorMock.validateXml(isA(File.class))).andReturn(responseMock).anyTimes();

        PowerMock.replayAll();

        Converter converter = new Converter(pbvalidatorMock, admFile);

        String dirStr = VALID_DIR_STR;

        converter.batchConvert(new File(TESTOUTPUT_DIR_STR, dirStr));

        // check EMD file for existence and non emptiness, transformer should be tested elsewhere!
        File emdFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/easymetadata.xml");
        assertTrue(emdFile.exists());
        assertTrue(FileUtils.sizeOf(emdFile) > 0);

        // check that the pakbon file is copied
        File pakbonDstFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/filedata/pakbon_valid.xml");
        assertTrue(pakbonDstFile.exists());
        assertTrue(FileUtils.sizeOf(pakbonDstFile) > 0);

        // check that the AMD file is copied
        File amdDstFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/administrative-metadata.xml");
        assertTrue(amdDstFile.exists());
        assertTrue(FileUtils.sizeOf(amdDstFile) > 0);

        // check that metadata relation is saved
        File relsFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/additional-metadatafiles.properties");
        assertTrue(relsFile.exists());
        assertTrue(FileUtils.sizeOf(relsFile) > 0);

        PowerMock.verifyAll();
    }

    /**
     * Test that an invalid file is not converted
     * 
     * @throws Exception
     */
    @Test
    public void convertInvalidPakbon() throws Exception {
        ValidationMessage msg = new ValidationMessage();
        msg.setMessage("Invalid Pakbon Test");
        ValidationMessage[] msgs = {msg};

        // mock validation, so we don't connect to the SIKB site
        PakbonValidator pbvalidatorMock = EasyMock.createMock(PakbonValidator.class);
        ValidateXmlResponse responseMock = EasyMock.createMock(ValidateXmlResponse.class);
        Validation validationMock = EasyMock.createMock(Validation.class);
        EasyMock.expect(validationMock.getValidXml()).andReturn(false).anyTimes(); // always NOT OK
        EasyMock.expect(validationMock.getMessages()).andReturn(msgs).anyTimes(); // always the same
                                                                                  // message
        EasyMock.expect(responseMock.getValidation()).andReturn(validationMock).anyTimes();
        EasyMock.expect(pbvalidatorMock.validateXml(isA(File.class))).andReturn(responseMock).anyTimes();

        PowerMock.replayAll();

        Converter converter = new Converter(pbvalidatorMock, admFile);

        String dirStr = INVALID_DIR_STR;

        converter.batchConvert(new File(TESTOUTPUT_DIR_STR, dirStr));

        // check EMD file has NOT been written
        File emdFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/easymetadata.xml");
        assertFalse(emdFile.exists());

        // check that the pakbon file is NOT copied
        File pakbonDstFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/filedata/pakbon_invalid.xml");
        assertFalse(pakbonDstFile.exists());

        // check that the AMD file is not copied
        File amdDstFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/administrative-metadata.xml");
        assertFalse(amdDstFile.exists());

        // check that metadata relation is not saved
        File relsFile = new File(TESTOUTPUT_DIR_STR, dirStr + "/testdataset/metadata/additional-metadatafiles.properties");
        assertFalse(relsFile.exists());

        PowerMock.verifyAll();
    }

    private static void setupTestFiles() {
        // We clean it first because we leave the output there after the testing for inspection purposes
        cleanupTestOutputFiles();

        File testFilesFolder = new File(TESTINPUT_DIR_STR);

        File testOutputFolder = new File(TESTOUTPUT_DIR_STR);
        try {
            FileUtils.forceMkdir(testOutputFolder);
        }
        catch (IOException e1) {
            fail();
            e1.printStackTrace();
        }

        logger.info("output: " + testOutputFolder.getAbsolutePath());

        try {
            FileUtils.copyDirectory(testFilesFolder, testOutputFolder);
        }
        catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    private static void cleanupTestOutputFiles() {
        File testOutputFolder = new File(TESTOUTPUT_DIR_STR);
        try {
            FileUtils.deleteDirectory(testOutputFolder);
            // FileUtils.forceDelete(testOutputFolder);
        }
        catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }
}
