package nl.knaw.dans.easy.data.store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;

import javax.xml.transform.Source;

import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;

import org.dom4j.Document;
import org.dom4j.Element;

public class EmdMetadataUnitXMLBeanAdapter extends AbstractTimestampedObject implements MetadataUnitXMLBean
{

    public static final int DEFAULT_INDENT = 4;

    private static final long serialVersionUID = -1495247151003175103L;

    private final transient EmdMarshaller emdMarshaller;
    private final EasyMetadataImpl emd;

    public EmdMetadataUnitXMLBeanAdapter(EasyMetadata emd)
    {
        emdMarshaller = new EmdMarshaller(emd);
        this.emd = (EasyMetadataImpl) emd;
    }

    @Override
    public byte[] asObjectXML() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlByteArray();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public byte[] asObjectXML(int indent) throws XMLSerializationException
    {
        emdMarshaller.setIndent(indent);
        try
        {
            return emdMarshaller.getXmlByteArray();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public Document asDocument() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlDocument();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public String asXMLString() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlString();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public String asXMLString(int indent) throws XMLSerializationException
    {
        emdMarshaller.setIndent(indent);
        try
        {
            return emdMarshaller.getXmlString();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public InputStream asXMLInputStream() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlInputStream();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public InputStream asXMLInputStream(int indent) throws XMLSerializationException
    {
        emdMarshaller.setIndent(indent);
        try
        {
            return emdMarshaller.getXmlInputStream();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public Source asSource() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlSource();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public void serializeTo(OutputStream outStream) throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            emdMarshaller.write(outStream);
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }

    }

    @Override
    public void serializeTo(OutputStream outStream, int indent) throws XMLSerializationException
    {
        emdMarshaller.setIndent(indent);
        try
        {
            emdMarshaller.write(outStream);
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }

    }

    @Override
    public void serializeTo(File file) throws XMLSerializationException
    {
        serializeTo(file, DEFAULT_INDENT);
    }

    @Override
    public void serializeTo(File file, int indent) throws XMLSerializationException
    {
        OutputStream outStream = null;
        emdMarshaller.setIndent(indent);
        try
        {
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            emdMarshaller.write(outStream);
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
        catch (FileNotFoundException e)
        {
            throw new XMLSerializationException(e);
        }
        finally
        {
            if (outStream != null)
            {
                try
                {
                    outStream.close();
                }
                catch (IOException e)
                {
                    throw new XMLSerializationException(e);
                }
            }
        }
    }

    @Override
    public void serializeTo(String encoding, Writer out) throws XMLSerializationException
    {
        serializeTo(encoding, out, DEFAULT_INDENT);
    }

    @Override
    public void serializeTo(String encoding, Writer out, int indent) throws XMLSerializationException
    {
        emdMarshaller.setIndent(indent);
        String enc = emdMarshaller.getEncoding();
        emdMarshaller.setEncoding(encoding);
        try
        {
            emdMarshaller.write(out);
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
        finally
        {
            emdMarshaller.setEncoding(enc);
        }
    }

    @Override
    public Element asElement() throws XMLSerializationException
    {
        emdMarshaller.setIndent(DEFAULT_INDENT);
        try
        {
            return emdMarshaller.getXmlElement();
        }
        catch (nl.knaw.dans.pf.language.xml.exc.XMLSerializationException e)
        {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public String getVersion()
    {
        return emd.getVersion();
    }

    @Override
    public String getUnitFormat()
    {
        return EasyMetadata.UNIT_FORMAT;
    }

    @Override
    public URI getUnitFormatURI()
    {
        return EasyMetadata.UNIT_FORMAT_URI;
    }

    @Override
    public String getUnitId()
    {
        return EasyMetadata.UNIT_ID;
    }

    @Override
    public String getUnitLabel()
    {
        return EasyMetadata.UNIT_LABEL;
    }

    @Override
    public boolean isVersionable()
    {
        return emd.isVersionable();
    }

    @Override
    public void setVersionable(boolean versionable)
    {
        emd.setVersionable(versionable);
    }

}
