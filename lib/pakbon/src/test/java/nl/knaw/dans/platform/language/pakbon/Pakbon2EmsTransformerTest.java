package nl.knaw.dans.platform.language.pakbon;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pakbon2EmsTransformerTest {
    private static final Logger logger = LoggerFactory.getLogger(Pakbon2EmsTransformerTest.class);
    private static final String PB_VALID = "src/test/resources/test-files/SIKB0102_Pakbon_3.2.0.xml";

    @BeforeClass
    public static void beforeTestClass() {
        new ResourceLocator(new FileSystemHomeDirectory(new File("src/test/resources")));
    }

    @Test
    public void transformToString() throws Exception {
        Pakbon2EmdTransformer p2e = new Pakbon2EmdTransformer();
        // Note that the xslt used by transform specifies that the output is UTF-8
        String out = new String(p2e.transform(new File(PB_VALID)), "UTF-8");
        // Note that we have no asserts here, so it's just a smoke test
        logger.debug(out);
    }

    @Test
    public void transformToEmd() throws Exception {
        Pakbon2EmdTransformer p2e = new Pakbon2EmdTransformer();
        EasyMetadata emd = p2e.transformToEmd(new File(PB_VALID));
        XMLErrorHandler handler = EMDValidator.instance().validate(emd);
        assertThat(handler.passed(), is(true));
    }

    @Test
    public void getSupportedVersions() throws Exception {
        Pakbon2EmdTransformer p2e = new Pakbon2EmdTransformer();
        List<String> versions = p2e.getSupportedVersions();
        logger.debug("Supported versions: " + StringUtils.join(versions.toArray(), ", "));
        assertFalse(versions.isEmpty());
    }

}
