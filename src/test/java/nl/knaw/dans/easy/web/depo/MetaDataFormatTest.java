package nl.knaw.dans.easy.web.depo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.easy.domain.model.emd.EasyMetadataValidator;

import org.apache.wicket.util.file.Folder;
import org.junit.Ignore;
import org.junit.Test;

public class MetaDataFormatTest
{
    private static final String VERSION     = EasyMetadataValidator.VERSION_0_1;
    private static final String SVN         = "https://develop01.dans.knaw.nl/svn/mixed/eof/trunk/easy/easy-application/";
    private static final String XSD         = "easy-business/src/main/java/nl/knaw/dans/easy/domain/model/emd/xsd-files/" + VERSION + "/";
    private static final String DOC_DIR     = "src/main/webapp/metadataformat/";
    private static final String EMD_DIR     = "src/main/resources/conf/discipline/emd/";
    private static final String DISIPLINES  = EMD_DIR + "form-description/";
    private static final String CHOICELISTS = EMD_DIR + "choicelist/";
    private static final String XML_FORMAT  = "<?xml version='1.0'?>" + //
                                                    "<discipline xmlns:xi='http://www.w3.org/2001/XInclude' >" + //
                                                    "<xi:include href='../../" + DISIPLINES + "%1$s.xml'/>" + //
                                                    "</discipline>";
    private static final String INDEX_LINK  = "<tr><td>%1$s</td><td><a href='%1$s.html'>html</a>" + //
                                                    "</td><td><a href='../../../../" + DISIPLINES + "%1$s.xml'>xml</a></td></tr>";
    private static final String INDEX_PAGE  = "<html>" + //
                                                    " <head><title>Easy-II Meta Data Format</title></head>" + //
                                                    " <body><h1>Easy-II Meta Data Format</h1>" + //
                                                    "  <p><a href='" + SVN + XSD + "'>xsd</a> files</p>" + //
                                                    "  <h2>Additional requirements by discipline</h2>" + //
                                                    "  <table>%s</table>" + //
                                                    "  <p> workaround a broken link: " + //
                                                    "   <a href='common.dcterms.audience.html'>custom.disciplines.html</a>" + //
                                                    "  </p>" + //
                                                    " </body>" + //
                                                    "</html>";

    @Ignore("Cannot be run offline. And more: it is not a test!")
    @Test
    public void generateHtmlDoc() throws Exception
    {
        String s = "";
        for (final File file : new Folder(DISIPLINES).getFiles())
        {
            final String discipline = file.getName().split("\\.")[0];
            transform(file, discipline, "src/test/resources/metaDataFormat.xsl");
            s += String.format(INDEX_LINK, discipline);
            assembleXml(discipline);
        }
        write(String.format(INDEX_PAGE, s), new File(DOC_DIR + "index.html"));
        for (final File file : new Folder(CHOICELISTS).getNestedFiles())
        {
            final String[] parts = file.getName().split("\\.");
            if ("xml".equals(parts[parts.length - 1].toLowerCase()))
            {
                final String outputBaseName = file.getAbsolutePath().replaceAll(".*choicelist/", "").split("\\.")[0].replaceAll("/", ".");
                transform(file, outputBaseName, "src/test/resources/choicelist.xsl");
            }
        }
    }

    @SuppressWarnings("unused")
    private void assembleXml(final String discipline)
    {
        // TODO try to include XML files together
        final InputStream xmlInputStream = new ByteArrayInputStream(String.format(XML_FORMAT, discipline).getBytes());
        final StreamSource xml = new StreamSource(xmlInputStream);
    }

    private void transform(final File input, final String outputBaseName, final String xlsFileName) throws Exception
    {

        final StreamSource xml = new StreamSource(input);

        final StreamSource xsl = new StreamSource(xlsFileName);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final File file = new File(DOC_DIR + outputBaseName + ".html");
        transformer.transform(xml, new StreamResult(outputStream));
        write(outputStream.toString(), file);
        outputStream.flush();
        outputStream.close();
    }

    private void write(final String string, final File file) throws FileNotFoundException, IOException
    {
        final FileOutputStream stream = new FileOutputStream(file);
        try
        {
            stream.write(string.getBytes());
            stream.flush();
        }
        finally
        {
            stream.close();
        }
    }
}
