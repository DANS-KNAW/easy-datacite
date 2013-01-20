package nl.knaw.dans.pf.language.xml.binding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class JiBXUnmarshaller<T> implements XMLUnmarshaller<T>
{

    private final String bindingName;
    private final Class<? extends T> beanClass;
    private IBindingFactory bindingFactory;
    private IUnmarshallingContext unmarshallingContext;
    private String encoding = Encoding.UTF8;

    public JiBXUnmarshaller(Class<? extends T> beanClass)
    {
        this.beanClass = beanClass;
        this.bindingName = null;
    }

    public JiBXUnmarshaller(String bindingName, Class<? extends T> beanClass)
    {
        this.beanClass = beanClass;
        this.bindingName = bindingName;
    }

    public void setEncoding(String enc)
    {
        this.encoding = enc;
    }

    public String getEncoding()
    {
        return encoding;
    }
    
    @Override
    public T unmarshal(InputStream inStream) throws XMLDeserializationException
    {
        return unmarshal(inStream, encoding);
    }

    @SuppressWarnings("unchecked")
    public T unmarshal(InputStream inStream, String enc) throws XMLDeserializationException
    {
        T bean;
        try
        {
            IUnmarshallingContext uContext = getUnMarshallingContext();
            bean = (T) uContext.unmarshalDocument(inStream, enc);
        }
        catch (JiBXException e)
        {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(Reader reader) throws XMLDeserializationException
    {
        T bean;
        try
        {
            IUnmarshallingContext uContext = getUnMarshallingContext();
            bean = (T) uContext.unmarshalDocument(reader);
        }
        catch (JiBXException e)
        {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }

    public T unmarshal(String xmlString) throws XMLDeserializationException
    {
        return unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
    }
    
    @Override
    public T unmarshal(byte[] bytes) throws XMLDeserializationException
    {
        return unmarshal(new ByteArrayInputStream(bytes));
    }
    
    @Override
    public T unmarshal(Source source) throws XMLDeserializationException
    {
        if (source instanceof StreamSource)
        {
            return unmarshal(((StreamSource)source).getInputStream());
        }
        else
        {
            throw new XMLDeserializationException("Cannot unmarshal from " + source);
        }
    }
    
    @Override
    public T unmarshal(Document document) throws XMLDeserializationException
    {
        String enc = document.getXMLEncoding();
        if (enc != null)
        {
            return unmarshal(new ByteArrayInputStream(document.asXML().getBytes()), enc);
        }
        else
        {
            return unmarshal(document.asXML());
        }
    }
    
    @Override
    public T unmarshal(Element element) throws XMLDeserializationException
    {
        String enc = null;
        Document document = element.getDocument();
        if (document != null)
        {
            enc = document.getXMLEncoding();
        }
        
        if (enc != null)
        {
            return unmarshal(new ByteArrayInputStream(element.asXML().getBytes()), enc);
        }
        else
        {
            return unmarshal(element.asXML());
        }
    }

    protected IUnmarshallingContext getUnMarshallingContext() throws JiBXException
    {
        if (unmarshallingContext == null)
        {
            unmarshallingContext = getBindingFactory().createUnmarshallingContext();
        }
        return unmarshallingContext;
    }

    protected IBindingFactory getBindingFactory() throws JiBXException
    {
        if (bindingFactory == null)
        {
            if (bindingName == null)
            {
                bindingFactory = BindingDirectory.getFactory(beanClass);
            }
            else
            {
                bindingFactory = BindingDirectory.getFactory(bindingName, beanClass);
            }
        }
        return bindingFactory;
    }
}
