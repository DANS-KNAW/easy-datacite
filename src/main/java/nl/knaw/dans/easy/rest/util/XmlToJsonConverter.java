package nl.knaw.dans.easy.rest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;

/**
 * An utility class to convert data to other formats.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class XmlToJsonConverter
{

    /**
     * Throw an AssertionError if this class or one of it's subclasses is ever
     * instantiated.
     */
    protected XmlToJsonConverter()
    {
        throw new AssertionError("Instantiating utility class...");
    }

    /**
     * Converts a byte array containing XML to JSON.
     * 
     * @param xml
     *            The byte array containing XML.
     * @return String containing JSON representation of the given XML.
     * @throws IOException
     *             Thrown if reading input as InputStream goes wrong.
     * @throws XMLStreamException
     *             Thrown if XML parsing goes wrong.
     */
    public static String convert(byte[] xml) throws IOException, XMLStreamException
    {
        return convert(new String(xml));
    }

    /**
     * Converts a String containing XML to JSON.
     * 
     * @param xml
     *            The XML containing String.
     * @return String containing JSON representation of the given XML.
     * @throws IOException
     *             Thrown if reading input as InputStream goes wrong.
     * @throws XMLStreamException
     *             Thrown if XML parsing goes wrong.
     */
    public static String convert(String xml) throws IOException, XMLStreamException
    {
        String json = "";

        InputStream input = new ByteArrayInputStream(xml.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).prettyPrint(true).build();
        try
        {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);

            XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);

            writer.add(reader);

            reader.close();
            writer.close();

            json = new String(output.toByteArray());
        }
        finally
        {
            output.close();
            input.close();
        }

        return json;
    }

}
