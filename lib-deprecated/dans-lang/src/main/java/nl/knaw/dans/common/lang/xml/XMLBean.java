package nl.knaw.dans.common.lang.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.Source;

import org.dom4j.Document;
import org.dom4j.Element;

public interface XMLBean extends MinimalXMLBean {

    /**
     * The return string for beans that are not versioned.
     */
    String NOT_VERSIONED = "not versioned";

    /**
     * Value for parameter 'indent' when no new lines and no indent are wanted.
     */
    int NO_INDENT = -1;

    /**
     * Value for parameter 'indent' when only new lines are wanted.
     */
    int NEW_LINE_ONLY = 0;

    /**
     * Serialize this bean to a byte array.
     * 
     * @param indent
     *        indent while serializing
     * @return bean as byte array
     * @throws XMLSerializationException
     *         if something goes wrong
     */
    byte[] asObjectXML(int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to a Document.
     * 
     * @return bean as Document
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    Document asDocument() throws XMLSerializationException;

    /**
     * Serialize this bean to a String.
     * 
     * @return bean as String
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    String asXMLString() throws XMLSerializationException;

    /**
     * Serialize this bean to a String with the given indent.
     * 
     * @param indent
     *        indent while serializing
     * @return bean as String
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    String asXMLString(int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to an InputStream.
     * 
     * @return bean as InputStream
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    InputStream asXMLInputStream() throws XMLSerializationException;

    /**
     * Serialize this bean to an InputStream with the given indent.
     * 
     * @param indent
     *        indent while serializing
     * @return bean as InputString
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    InputStream asXMLInputStream(int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to a Source.
     * 
     * @return bean as Source
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    Source asSource() throws XMLSerializationException;

    /**
     * Serialize this bean to the given OutputStream.
     * 
     * @param outStream
     *        serialization target
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(OutputStream outStream) throws XMLSerializationException;

    /**
     * Serialize this bean to the given OutputStream with the given indent.
     * 
     * @param outStream
     *        serialization target
     * @param indent
     *        indent while serializing
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(OutputStream outStream, int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to the given File.
     * 
     * @param file
     *        serialization target
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(File file) throws XMLSerializationException;

    /**
     * Serialize this bean to the given File with the given indent.
     * 
     * @param file
     *        serialization target
     * @param indent
     *        indent while serializing
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(File file, int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to the given Writer.
     * 
     * @param encoding
     *        encoding to use
     * @param out
     *        serialization target
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(String encoding, Writer out) throws XMLSerializationException;

    /**
     * Serialize this bean to the given Writer using the given indent.
     * 
     * @param encoding
     *        encoding to use
     * @param out
     *        serialization target
     * @param indent
     *        indent while serializing
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    void serializeTo(String encoding, Writer out, int indent) throws XMLSerializationException;

    /**
     * Serialize this bean to an Element.
     * 
     * @return bean as Element
     * @throws XMLSerializationException
     *         if hmm.. something seems to be wrong...
     */
    Element asElement() throws XMLSerializationException;

    /**
     * Get the version of this XMLBean or the string {@link #NOT_VERSIONED} if this XMLBean is not versioned.
     * 
     * @return the version of this XMLBean
     */
    String getVersion();

}
