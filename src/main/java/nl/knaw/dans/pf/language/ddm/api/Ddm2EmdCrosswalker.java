package nl.knaw.dans.pf.language.ddm.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;
import nl.knaw.dans.pf.language.xml.exc.XMLException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Ddm2EmdCrosswalker
{
    private static XMLErrorHandler errorHandler;
    private static XMLReader reader;

    public EasyMetadata guardedCreateFrom(String xmlContent) throws CrosswalkException
    {
        try
        {
            DDMValidator.instance().validate(getErrorHandler(),new ByteArrayInputStream(xmlContent.getBytes()));
            return parse(new ByteArrayInputStream(xmlContent.getBytes()));
        }
        catch (XMLException e)
        {
            throw new CrosswalkException(e);
        }
    }

    public EasyMetadata createFrom(final InputStream source) throws CrosswalkException
    {
        return parse(source);
    }

    private EasyMetadata parse(final InputStream source) throws CrosswalkException
    {
        try
        {
            new CrosswalkHandler<EasyMetadata>(newTarget(), getReader(), Ddm2EmdHandlerMap.getInstance());
            getReader().parse(new InputSource(source));
            return newTarget();
        }
        catch (SAXException e)
        {
            throw new CrosswalkException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new CrosswalkException(e);
        }
        catch (IOException e)
        {
            throw new CrosswalkException(e);
        }
    }

    private EasyMetadata newTarget()
    {
        return EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
    }

    private static XMLReader getReader() throws SAXException, ParserConfigurationException
    {
        if (reader == null)
        {
            if (errorHandler==null)
                errorHandler = new XMLErrorHandler(Reporter.off);
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setErrorHandler(errorHandler);
        }
        return reader;
    }

    public static XMLErrorHandler getErrorHandler() throws CrosswalkException
    {
        try
        {
            return (XMLErrorHandler) getReader().getErrorHandler();
        }
        catch (SAXException e)
        {
            throw new CrosswalkException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new CrosswalkException(e);
        }
    }

    public static void setErrorHandler(XMLErrorHandler errorHandler)
    {
        Ddm2EmdCrosswalker.errorHandler = errorHandler;
        reader=null;
    }
}
