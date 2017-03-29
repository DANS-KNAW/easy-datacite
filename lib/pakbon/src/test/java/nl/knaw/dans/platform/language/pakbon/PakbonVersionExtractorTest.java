package nl.knaw.dans.platform.language.pakbon;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

public class PakbonVersionExtractorTest {
    private static final String TEST_PATH_DIR = "src/test/resources/test-files";
    private static final String TEST_PATH_FILE_PREFIX = "SIKB0102_Pakbon";

    @Test
    public void extractTest() throws Exception {
        // Note: should we extract the versions from the files in the dir
        String[] supportedVersions = {"2.1.0", "3.1.0", "3.2.0", "3.3.0"};

        for (String fileVersion : supportedVersions) {
            String extractedVersion = extractFromFileWithVersion(fileVersion);
            assertEquals(fileVersion, extractedVersion);
        }
    }

    private String extractFromFileWithVersion(String fileVersion) throws Exception {
        PakbonVersionExtractor extractor = new PakbonVersionExtractor();
        String fileName = TEST_PATH_DIR + "/" + TEST_PATH_FILE_PREFIX + "_" + fileVersion + ".xml";
        InputStream xmlInput = new FileInputStream(fileName);
        return extractor.extract(xmlInput);
    }
}
