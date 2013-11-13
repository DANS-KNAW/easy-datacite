package nl.knaw.dans.platform.language.pakbon;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class Pakbon2EmsTransformerTest
{

    private static final String PB_VALID = "src/test/resources/test-files/pakbon_valid.xml";

    @Test
    public void transformToString() throws Exception
    {
        Pakbon2EmdTransformer p2e = new Pakbon2EmdTransformer();
        // Note that the xslt used by transform specifies that the output is UTF-8
        String out = new String(p2e.transform(new FileInputStream(PB_VALID)), "UTF-8");
        System.err.println(out);
    }

    @Test
    public void transformToEmd() throws Exception
    {
        Pakbon2EmdTransformer p2e = new Pakbon2EmdTransformer();
        EasyMetadata emd = p2e.transformToEmd(new FileInputStream(PB_VALID));
        XMLErrorHandler handler = EMDValidator.instance().validate(emd);
        assertThat(handler.passed(), is(true));
    }

}
