package nl.knaw.dans.l.xml.binding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import nl.knaw.dans.l.xml.exc.XMLDeserializationException;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class JiBXUnmarshaller<T>
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

    @SuppressWarnings("unchecked")
    public T unmarshal(InputStream inStream) throws XMLDeserializationException
    {
        T bean;
        try
        {
            IUnmarshallingContext uContext = getUnMarshallingContext();
            bean = (T) uContext.unmarshalDocument(inStream, encoding);
        }
        catch (JiBXException e)
        {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }

    public T unmarshal(final String xmlString) throws XMLDeserializationException
    {
        return unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
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
