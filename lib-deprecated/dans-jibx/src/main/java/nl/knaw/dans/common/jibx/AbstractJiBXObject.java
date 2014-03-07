package nl.knaw.dans.common.jibx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Observable;

import javax.xml.transform.Source;

import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jibx.runtime.JiBXException;

/**
 * Abstract implementation of an XMLBean empowered by JiBX.
 * 
 * @author ecco Sep 30, 2009
 * @param <T>
 *        type of the bean
 */
public abstract class AbstractJiBXObject<T> extends Observable implements XMLBean
{

    private static final long serialVersionUID = -1465724031853057295L;

    /**
     * {@inheritDoc}
     */
    public String getVersion()
    {
        return NOT_VERSIONED;
    }

    @Override
    public byte[] asObjectXML() throws XMLSerializationException
    {
        return asObjectXML(NO_INDENT);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public byte[] asObjectXML(int indent) throws XMLSerializationException
    {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try
        {
            getJiBXUtil().marshalDocument((T) this, outStream, indent);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
        return outStream.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Element asElement() throws XMLSerializationException
    {
        try
        {
            return getJiBXUtil().getElement((T) this);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
        catch (final DocumentException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Document asDocument() throws XMLSerializationException
    {
        try
        {
            return getJiBXUtil().getDocument((T) this);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
        catch (final DocumentException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String asXMLString() throws XMLSerializationException
    {
        return asXMLString(XMLBean.NO_INDENT);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String asXMLString(final int indent) throws XMLSerializationException
    {
        try
        {
            return getJiBXUtil().marshalDocument((T) this, indent);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream asXMLInputStream() throws XMLSerializationException
    {
        return asXMLInputStream(XMLBean.NO_INDENT);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public InputStream asXMLInputStream(final int indent) throws XMLSerializationException
    {
        try
        {
            return getJiBXUtil().getInputStream((T) this, indent);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void serializeTo(final OutputStream outStream) throws XMLSerializationException
    {
        serializeTo(outStream, XMLBean.NO_INDENT);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void serializeTo(final OutputStream outStream, final int indent) throws XMLSerializationException
    {
        try
        {
            getJiBXUtil().marshalDocument((T) this, outStream, indent);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void serializeTo(final File file) throws XMLSerializationException
    {
        serializeTo(file, XMLBean.NO_INDENT);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void serializeTo(final File file, final int indent) throws XMLSerializationException
    {
        try
        {
            getJiBXUtil().marshalDocument((T) this, file, indent);
        }
        catch (final IOException e)
        {
            throw new XMLSerializationException(e);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void serializeTo(final String encoding, final Writer out, final int indent) throws XMLSerializationException
    {
        try
        {
            getJiBXUtil().marshalDocument((T) this, encoding, out, indent);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void serializeTo(final String encoding, final Writer out) throws XMLSerializationException
    {
        serializeTo(encoding, out, XMLBean.NEW_LINE_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Source asSource() throws XMLSerializationException
    {
        try
        {
            return getJiBXUtil().getSource((T) this);
        }
        catch (final JiBXException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private JiBXUtil<T> getJiBXUtil()
    {
        return JiBXObjectFactory.getJiBXUtil(this.getClass());
    }

    public void setModified()
    {
        setChanged();
        notifyObservers();
    }

    public void setModified(Object arg)
    {
        setChanged();
        notifyObservers(arg);
    }

}
