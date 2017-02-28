package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.sword.util.Fixture;
import nl.knaw.dans.easy.sword.util.MockUtil;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;

import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestEasyBusinessFacade extends Fixture {
    static File tempDirectory;

    @Before
    public void setupMocking() throws Exception {
        MockUtil.mockAll();
    }

    @BeforeClass
    public static void createTempDir() throws Exception {
        final File basePath = new File("target/tmp");
        basePath.mkdirs();
        tempDirectory = FileUtil.createTempDirectory(basePath, "unzip");
    }

    @AfterClass
    public static void clearTempDir() throws Exception {
        // TODO clear target/tmp/unzip;
    }

    @Test
    public void invalidMetadataByMM() throws Throwable {
        new Context().setUnzip("target");
        try {
            // zip contains (invalid?) EMD which is no longer accepted
            RequestContent rc = new RequestContent(new FileInputStream("src/test/resources/input/invalidMetadata.zip"));
        }
        catch (final SWORDErrorException se) {
            assertThat(se.getMessage(),
                    is("Expecting a folder with files and a file with one of the names: DansDatasetMetadata.xml (preferred metadata format)"));
            return;
        }
        fail("expecting a SWORDException");
    }

    @Test
    public void newFormDefinition() throws Throwable {
        try {
            EasyBusinessFacade.getFormDefinition();
        }
        catch (final SWORDErrorException se) {
            fail("not expecting an exception: " + se);
        }
    }

    @Test
    public void deprecatedFormDefinition() throws Throwable {
        final EasyMetadata emd = new EasyMetadataImpl(ApplicationSpecific.MetadataFormat.ARCHAEOLOGY);
        try {
            FormDefinition formDefinition = EasyBusinessFacade.getFormDefinition();
        }
        catch (final SWORDErrorException se) {
            fail("not expecting an exception: " + se);
        }
    }

    private static List<File> unzip(File zipFile) throws Exception {
        final UnzipListener unzipListener = new UnzipListener() {
            @Override
            public boolean onUnzipUpdate(final long bytesUnzipped, final long total) {
                return true; // continue unzip
            }

            @Override
            public void onUnzipStarted(final long totalBytes) {}

            @Override
            public void onUnzipComplete(final List<File> files, final boolean canceled) {}
        };
        return new UnzipUtil(zipFile, tempDirectory.getPath(), unzipListener).run();
    }
}
