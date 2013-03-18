package nl.knaw.dans.pf.language.xml.binding;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;

import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Unmarshaller, capable of creating an object representing xml from various xml sources.
 * 
 * @author ecco
 * @param <T>
 *        the unmarshalled object
 */
public interface XMLUnmarshaller<T>
{
    /**
     * Set the expected encoding of the source xml.
     * <p/>
     * Default is {@link Encoding#UTF8}.
     * 
     * @param enc
     *        expected encoding of the source xml
     */
    void setEncoding(String enc);

    /**
     * Get the expected encoding of the source xml. Defaults to {@link Encoding#UTF8} if not set.
     * 
     * @return expected encoding of the source xml
     */
    String getEncoding();

    /**
     * Unmarshal an object from the given inputStream, in the expected encoding.
     * 
     * @param inStream
     *        xml source
     * @param encoding
     *        expected encoding
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(InputStream inStream, String encoding) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given inputStream.
     * 
     * @param inStream
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(InputStream inStream) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given reader.
     * 
     * @param reader
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(Reader reader) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given string.
     * 
     * @param xmlString
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(String xmlString) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given byte array.
     * 
     * @param bytes
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(byte[] bytes) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given source.
     * 
     * @param source
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(Source source) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given document.
     * 
     * @param document
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(Document document) throws XMLDeserializationException;

    /**
     * Unmarshal an object from the given element.
     * 
     * @param element
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(Element element) throws XMLDeserializationException;
    
    /**
     * Unmarshal an object from the given org.w3c.dom.Document.
     * 
     * @param document
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(org.w3c.dom.Document document) throws XMLDeserializationException;
    
    /**
     * Unmarshal an object from the given org.w3c.dom.Element.
     * 
     * @param element
     *        xml source
     * @return unmarshalled object
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    T unmarshal(org.w3c.dom.Element element) throws XMLDeserializationException;

}
