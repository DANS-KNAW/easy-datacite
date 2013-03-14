package nl.knaw.dans.pf.language.ddm.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Ddm2EmdHandlerMapTest
{
    private static final String INPUT_DIR = "src/test/resources/input/";
    private static XMLReader reader;

    @Test
    public void maxDDM() throws Exception
    {
        final EasyMetadata easyMetadata = unguarderdCrosswalk(new FileInputStream(INPUT_DIR + "maxDDM.xml"));
    }

    @Test
    public void rubbish() throws Exception
    {
        final EasyMetadata easyMetadata = unguarderdCrosswalk(new FileInputStream(INPUT_DIR + "abstract.xml"));
    }

    private EasyMetadata unguarderdCrosswalk(final InputStream source) throws SAXException, ParserConfigurationException, IOException
    {
        new CrosswalkHandler<EasyMetadata>(newTarget(), getReader(), Ddm2EmdHandlerMap.getInstance());
        getReader().parse(new InputSource(source));
        return newTarget();
    }

    private EasyMetadata newTarget()
    {
        return EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
    }

    private XMLReader getReader() throws SAXException, ParserConfigurationException
    {
        if (reader == null)
        {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setErrorHandler(new XMLErrorHandler(Reporter.off));
        }
        return reader;
    }
}
