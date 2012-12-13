package nl.knaw.dans.l.xml.binding;

import java.io.ByteArrayOutputStream;

import nl.knaw.dans.l.xml.exc.XMLSerializationException;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

public class JiBXMarshaller implements XMLMarshaller
{

    private final String bindingName;
    private final Object bean;

    private IBindingFactory bindingFactory;
    private IMarshallingContext marshallingContext;

    private String encoding = Encoding.UTF8;
    private int indent = 4;
    private boolean standAlone = true;
    private boolean omitXmlDeclaration;
    
    public JiBXMarshaller(Object bean)
    {
        this.bindingName = null;
        this.bean = bean;
    }

    public JiBXMarshaller(String bindingName, Object bean)
    {
        this.bindingName = bindingName;
        this.bean = bean;
    }

    @Override
    public void setEncoding(String enc)
    {
        this.encoding = enc;
    }

    @Override
    public String getEncoding()
    {
        return encoding;
    }

    @Override
    public void setIndent(int indent)
    {
        this.indent = indent;
    }

    @Override
    public int getIndent()
    {
        return indent;
    }

    @Override
    public void setStandAlone(boolean standAlone)
    {
        this.standAlone = standAlone;
    }

    @Override
    public boolean getStandAlone()
    {
        return standAlone;
    }

    public boolean getOmitXmlDeclaration()
    {
        return omitXmlDeclaration;
    }

    public void setOmitXmlDeclaration(boolean omit)
    {
        this.omitXmlDeclaration = omit;
    }

    @Override
    public ByteArrayOutputStream getXmlOutputStream() throws XMLSerializationException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            IMarshallingContext mContext = getMarshallingContext();
            mContext.setIndent(indent);
            if (omitXmlDeclaration)
            {
                mContext.setOutput(out, encoding);
                mContext.marshalDocument(bean);
            }
            else
            {
                mContext.marshalDocument(bean, encoding, standAlone, out);
            }
        }
        catch (JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
        return out;
    }

    @Override
    public String getXmlString() throws XMLSerializationException
    {
        return getXmlOutputStream().toString();
    }
    
    @Override
    public byte[] getXmlByteArray() throws XMLSerializationException
    {
        return getXmlOutputStream().toByteArray();
    }

    protected IMarshallingContext getMarshallingContext() throws JiBXException
    {
        if (marshallingContext == null)
        {
            marshallingContext = getBindingFactory().createMarshallingContext();
        }
        return marshallingContext;
    }

    protected IBindingFactory getBindingFactory() throws JiBXException
    {
        if (bindingFactory == null)
        {
            if (bindingName == null)
            {
                bindingFactory = BindingDirectory.getFactory(bean.getClass());
            }
            else
            {
                bindingFactory = BindingDirectory.getFactory(bindingName, bean.getClass());
            }
        }
        return bindingFactory;
    }

}
