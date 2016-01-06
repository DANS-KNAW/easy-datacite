package nl.knaw.dans.platform.language.pakbon;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PakbonVersionExtractor {

    public String extract(InputStream in) throws SAXException, IOException, ParserConfigurationException {
        String version = null;
        InputStream xmlInput = in;
        PakbonVersionExtractionHandler handler = new PakbonVersionExtractionHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlInput, handler);
            // Nothing, see break out below
        }
        catch (SaxBreakOutException allDone) {
            version = handler.version; // just OK
        }
        return version;
    }

    class PakbonVersionExtractionHandler extends DefaultHandler {
        public String version = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            //  Note that we mostly have sikb:sikb0102, but what if the namespace prefix is not sikb?
            // if("sikb:sikb0102".equals(qName)){
            if (qName.endsWith(":sikb0102")) {
                // get 'versie' attribute
                version = attributes.getValue("versie");
                // done parsing, No other way to bail out than throwing an exception!
                throw new SaxBreakOutException();
            }
        }

    }

    @SuppressWarnings("serial")
    class SaxBreakOutException extends RuntimeException {}

}
