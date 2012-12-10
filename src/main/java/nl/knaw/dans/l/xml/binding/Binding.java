package nl.knaw.dans.l.xml.binding;

import java.io.OutputStream;

import nl.knaw.dans.l.xml.exc.XMLSerializationException;

/**
 * Wrapper for an object representing xml.
 * 
 * @author ecco
 *
 */
public interface Binding
{
    /**
     * Value for parameter 'indent' when no new lines and no indent are wanted.
     */
    int NO_INDENT = -1;

    /**
     * Value for parameter 'indent' when only new lines are wanted.
     */
    int NEW_LINE_ONLY = 0;
    
    String ENC_UTF8 = "UTF-8";
    
    String ENC_UTF16 = "UTF-16";
    
    String ENC_US_ASCII = "US-ASCII";
    
    void setEncoding(String enc);
    
    String getEncoding();
    
    void setIndent(int indent);
    
    int getIndent();
    
    void setStandAlone(boolean standAlone);
    
    boolean getStandAlone();
    
    boolean getOmitXmlDeclaration();
    
    void setOmitXmlDeclaration(boolean omit);
    
    OutputStream getXmlOutputStream() throws XMLSerializationException;
    
    OutputStream getXmlOutputStream(int indent) throws XMLSerializationException;
    
    //byte[] getXmlByteArray(int indent, boolean standAlone) throws XMLSerializationException;
    
    String getXmlString() throws XMLSerializationException;
    
    String getXmlString(int indent) throws XMLSerializationException;
    
    //InputStream getXmlInputStream(T bean, int indent, boolean standAlone) throws XMLSerializationException;
    
    //Source getXmlSource(T bean, int indent, boolean standAlone) throws XMLSerializationException;
    
    //Document getXmlDocument(T bean, int indent, boolean standAlone) throws XMLSerializationException;
    
    //Element getXmlElement(T bean, int indent, boolean standAlone) throws XMLSerializationException;
    
    

}
