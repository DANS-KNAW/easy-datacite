package nl.knaw.dans.l.xml.binding;

import java.io.InputStream;

import nl.knaw.dans.l.xml.exc.XMLDeserializationException;

public interface XMLUnmarshaller<T>
{
    
    void setEncoding(String enc);
    
    String getEncoding();
    
    T unmarshal(InputStream inStream) throws XMLDeserializationException;
    
    T unmarshal(final String xmlString) throws XMLDeserializationException;

}
