package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.sword.util.Fixture;
import nl.knaw.dans.easy.sword.util.InMemoryZip;
import nl.knaw.dans.easy.sword.util.MockUtil;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public class TestDdmHandling extends Fixture {

    // @formatter:off
    private static final String philosophyWithGroupAccess = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<ddm:DDM" +
            "    xmlns:dcx='http://easy.dans.knaw.nl/schemas/dcx/'\n" +
            "    xmlns:dc='http://purl.org/dc/elements/1.1/'\n" +
            "    xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/' \n" +
            "    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" +
            "    xsi:schemaLocation='http://easy.dans.knaw.nl/schemas/md/ddm/ http://easy.dans.knaw.nl/schemas/md/2016/ddm.xsd'>\n" +
            "\n" +
            "    <ddm:profile>\n" +
            "        <dc:title>Lorem ipsum</dc:title>\n" +
            "        <dc:description>dolor sit amet</dc:description>\n" +
            "        <dc:creator>consectetur adipiscing elit</dc:creator>\n" +
            "        <ddm:created>2012</ddm:created>\n" +
            "        <ddm:audience>D32000</ddm:audience>\n" +
            "        <ddm:accessRights>GROUP_ACCESS</ddm:accessRights>\n" +
            "    </ddm:profile>\n" +
            "</ddm:DDM>\n";
    // @formatter:on

    @Test
    public void missingMetadata() throws Throwable {
        try {
            createRequestContent(philosophyWithGroupAccess.replace("<dc:creator>consectetur adipiscing elit</dc:creator>", ""));
        }
        catch (final SWORDErrorException se) {
            assertContentError(se);
            assertThat(se.getMessage(), containsString("Could not create EMD from DDM"));
            assertThat(se.getMessage(), containsString("creator}' is expected"));
            return;
        }
        fail("ddm should be rejected");
    }

    @Test
    public void invalidAudience() throws Throwable {
        try {
            createRequestContent(philosophyWithGroupAccess.replace("D32000", "123"));
        }
        catch (final SWORDErrorException se) {
            assertContentError(se);
            assertThat(se.getMessage(), containsString("Could not create EMD from DDM"));
            assertThat(se.getMessage(), containsString("Value '123' is not facet-valid"));
            return;
        }
        fail("ddm should be rejected");
    }

    @Test
    public void invalidGroupAccess() throws Throwable {
        MockUtil.mockUser();
        try {
            createRequestContent(philosophyWithGroupAccess);
        }
        catch (final SWORDErrorException se) {
            assertContentError(se);
            assertThat(se.getMessage(), containsString("invalid meta data"));
            assertThat(se.getMessage().replace("\n", " "), containsString("dcterms.accessrights[0]: [deposit.field_invalid_group]"));
            return;
        }
        fail("ddm should be rejected");
    }

    @Test
    public void validGroupAccess() throws Throwable {
        MockUtil.mockUser();
        createRequestContent(philosophyWithGroupAccess.replace("D32000","D37000"));
    }

    @Test
    public void noGroupAccess() throws Throwable {
        MockUtil.mockUser();
        createRequestContent(philosophyWithGroupAccess.replace("GROUP_ACCESS","OPEN_ACCESS"));
    }

    @Test
    public void invalidXSD() throws Throwable {
        try {
            createRequestContent(philosophyWithGroupAccess.replace("http://easy.dans.knaw.nl", "http://unknown.host.dans.knaw.nl"));
        }
        catch (final SWORDErrorException se) {
            assertContentError(se);
            assertThat(se.getMessage(), containsString("Cannot find the declaration of element 'ddm:DDM'"));
            return;
        }
        fail("ddm should be rejected");
    }

    @Test
    public void invalidXML() throws Throwable {
        try {
            createRequestContent(philosophyWithGroupAccess.replace("</ddm:profile>", "</profile>"));
        }
        catch (final SWORDErrorException se) {
            se.printStackTrace();
            assertContentError(se);
            assertThat(se.getMessage(), containsString("SAXParseException"));
            assertThat(se.getMessage(), containsString("must be terminated by the matching end-tag"));
            return;
        }
        fail("ddm should be rejected");
    }

    @BeforeClass
    public static void init() throws Throwable {
        assumeTrue("can access " + SCHEMAS, canConnect(SCHEMAS));
        new Context().setUnzip("target/tmp");
    }

    @After
    public void cleanUp() throws Exception {
        FileUtils.deleteDirectory(new File(Context.getUnzip()));
    }

    private void assertContentError(SWORDErrorException se) {
        assertThat(se.getErrorURI(), is("http://purl.org/net/sword/error/ErrorContent"));
        assertThat(se.getStatus(), is(415));
    }

    private void createRequestContent(String ddm) throws IOException, SWORDException, SWORDErrorException {
        InMemoryZip zip = new InMemoryZip();
        zip.add("DansDatasetMetadata.xml", ddm.getBytes());
        zip.add("data/blabla.txt", "blabla".getBytes());
        new RequestContent(zip.toInputStream());
    }
}
