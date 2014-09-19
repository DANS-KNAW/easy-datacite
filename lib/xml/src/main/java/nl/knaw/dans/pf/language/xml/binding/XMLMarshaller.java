package nl.knaw.dans.pf.language.xml.binding;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.Source;

import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Wrapper for an object representing xml, capable of marshalling the object to xml.
 * 
 * @author ecco
 */
public interface XMLMarshaller {
    /**
     * Value for parameter 'indent' when no new lines and no indent are wanted.
     */
    int NO_INDENT = -1;

    /**
     * Value for parameter 'indent' when only new lines are wanted.
     */
    int NEW_LINE_ONLY = 0;

    /**
     * Set the encoding for the produced xml.
     * <p/>
     * Default is {@link Encoding#UTF8}.
     * 
     * @param enc
     *        encoding for the produced xml
     */
    void setEncoding(String enc);

    /**
     * Get the encoding for the produced xml. Defaults to {@link Encoding#UTF8} if not set.
     * 
     * @return encoding for the produced xml
     */
    String getEncoding();

    /**
     * Set the indent for the produced xml. Setting an indent of {@link #NO_INDENT} or {@link #NEW_LINE_ONLY} will produce xml with no indent or only new lines
     * respectively.
     * <p/>
     * Default is 4 spaces.
     * 
     * @param indent
     *        indent for the produced xml
     */
    void setIndent(int indent);

    /**
     * Get the indent for the produced xml. Defaults to 4 spaces if not set.
     * 
     * @return the indent for the produced xml
     */
    int getIndent();

    /**
     * If there is a xml-declaration, sets the standalone attribute. The standalone declaration indicates whether a document relies on information from an
     * external source, such as external document type definition (DTD), for its content. If the standalone declaration has a value of "yes", for example,
     * <code>&lt;?xml version="1.0" standalone="yes"?></code>, the parser will report an error if the document references an external DTD or external entities.
     * <p/>
     * Default is <code>true</code>, meaning "yes".
     * 
     * @param standalone
     *        <code>true</code> for standalone="yes", <code>false</code> for standalone="no".
     */
    void setStandalone(boolean standalone);

    /**
     * Get the value of the standalone attribute in the xml-declaration.
     * 
     * @return <code>true</code> for standalone="yes", <code>false</code> for standalone="no".
     */
    boolean getStandalone();

    /**
     * Set whether to omit the xml-declaration. Omit the xml-declaration if the output is to be used as part of a parent-document.
     * <p/>
     * Default is false, meaning output starts with:
     * 
     * <pre>
     *    &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * </pre>
     * 
     * @param omit
     *        <code>true</code> if xml-declaration should be omitted, <code>false</code> otherwise.
     */
    void setOmitXmlDeclaration(boolean omit);

    /**
     * Tells whether the xml-declaration will be omitted from the output.
     * 
     * @return <code>true</code> if xml-declaration will be omitted, <code>false</code> otherwise.
     */
    boolean getOmitXmlDeclaration();

    /**
     * Marshal the wrapped bean to the given outputStream.
     * 
     * @param out
     *        outputStream for marshalling
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    void write(OutputStream out) throws XMLSerializationException;

    /**
     * Marshal the wrapped bean on the given writer.
     * 
     * @param out
     *        writer for marshalling
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    void write(Writer out) throws XMLSerializationException;

    /**
     * Get an outputStream with the marshalled wrapped bean.
     * 
     * @return outputStream with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    OutputStream getXmlOutputStream() throws XMLSerializationException;

    /**
     * Get an inputStream with the marshalled wrapped bean.
     * 
     * @return inputStream with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    InputStream getXmlInputStream() throws XMLSerializationException;

    /**
     * Get a byte array with the marshalled wrapped bean.
     * 
     * @return byte array with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    byte[] getXmlByteArray() throws XMLSerializationException;

    /**
     * Get a string with the marshalled wrapped bean.
     * 
     * @return string with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    String getXmlString() throws XMLSerializationException;

    /**
     * Get a source with the marshalled wrapped bean.
     * 
     * @return source with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    Source getXmlSource() throws XMLSerializationException;

    /**
     * Get a org.dom4j.Document with the marshalled wrapped bean. Settings for {@link #setOmitXmlDeclaration(boolean)} have no influence on this serialization,
     * the produced document will always have a xml-declaration. Mark the difference in the following code-snippet.
     * 
     * <pre>
     * (1)   XMarshaller m = new XMarshaller(myBean);
     * (2)   Document doc = m.getXmlDocument();
     * (3)   System.out.println(doc.asXML());
     * (4)   System.out.println(doc.getRootElement().asXML());
     * </pre>
     * 
     * Line (3) produces xml with an xml-declaration, line (4) produces the same xml without an xml-declaration.
     * 
     * @return org.dom4j.Document with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    Document getXmlDocument() throws XMLSerializationException;

    /**
     * Get a org.dom4j.Element with the marshalled wrapped bean. Settings for {@link #setOmitXmlDeclaration(boolean)} have no influence on this serialization,
     * the produced element will never have a xml-declaration.
     * 
     * @return org.dom4j.Element with the marshalled wrapped bean.
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    Element getXmlElement() throws XMLSerializationException;

    org.w3c.dom.Document getW3cDomDocument() throws XMLSerializationException;

    org.w3c.dom.Element getW3cDomElement() throws XMLSerializationException;

}
