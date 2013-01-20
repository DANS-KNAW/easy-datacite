package nl.knaw.dans.l.xml.binding;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;

import nl.knaw.dans.l.xml.exc.XMLDeserializationException;

import org.dom4j.Document;
import org.dom4j.Element;

public interface XMLUnmarshaller<T>
{
    
    void setEncoding(String enc);
    
    String getEncoding();
    
    T unmarshal(InputStream inStream, String encoding) throws XMLDeserializationException;
    
    T unmarshal(InputStream inStream) throws XMLDeserializationException;
    
    T unmarshal(Reader reader) throws XMLDeserializationException;
    
    T unmarshal(String xmlString) throws XMLDeserializationException;
    
    T unmarshal(byte[] bytes) throws XMLDeserializationException;
    
    T unmarshal(Source source) throws XMLDeserializationException;
    
    T unmarshal(Document document) throws XMLDeserializationException;
    
    T unmarshal(Element element)  throws XMLDeserializationException;

}
