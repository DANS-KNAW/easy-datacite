package nl.knaw.dans.pf.language.ddm.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;
import nl.knaw.dans.pf.language.xml.exc.XMLException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Ddm2EmdHandlerMapTest
{
    private static final XMLErrorHandler XML_ERROR_HANDLER = new XMLErrorHandler(Reporter.off);
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

    @Test
    public void guardedRubbish() throws Exception
    {
        final EasyMetadata easyMetadata = guardedCrosswalk(INPUT_DIR + "abstract.xml");
    }

    private EasyMetadata guardedCrosswalk(String fileName) throws XMLException, SAXException, ParserConfigurationException, IOException, FileNotFoundException
    {
        DDMValidator.instance().validate(XML_ERROR_HANDLER,new FileInputStream(fileName));
        return unguarderdCrosswalk(new FileInputStream(fileName));
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
            reader.setErrorHandler(XML_ERROR_HANDLER);
        }
        return reader;
    }
}
