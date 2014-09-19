package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.ctc.wstx.exc.WstxParsingException;

public class XmlToJsonConverterTest {

    @Test(expected = AssertionError.class)
    public void notInstantiable() {
        new XmlToJsonConverter();
    }

    @Test
    public void simpleXmlToJson() throws IOException, XMLStreamException {
        byte[] xml = "<root>text</root>".getBytes();
        String json = XmlToJsonConverter.convert(xml);
        String expectedJson = "{\n\t\"root\" : \"text\"\n}";
        assertEquals(expectedJson, json);
    }

    @Test
    public void nestedXmlToJson() throws IOException, XMLStreamException {
        byte[] xml = "<parent><child>text</child></parent>".getBytes();
        String json = XmlToJsonConverter.convert(xml);
        String expectedJson = "{\n\t\"parent\" : {\n\t\t\"child\" : \"text\"\n\t}\n}";
        assertEquals(expectedJson, json);
    }

    @Test(expected = WstxParsingException.class)
    public void incorrectXml() throws IOException, XMLStreamException {
        String xml = "<root>text</roo>";
        XmlToJsonConverter.convert(xml);
    }

}
