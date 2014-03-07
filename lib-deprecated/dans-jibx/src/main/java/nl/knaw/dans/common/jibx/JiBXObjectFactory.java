package nl.knaw.dans.common.jibx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.xml.XMLDeserializationException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jibx.runtime.JiBXException;

/**
 * Factory for unmarshalling JiBX-bound objects.
 * 
 * @author ecco Sep 17, 2009
 */
@SuppressWarnings("unchecked")
public class JiBXObjectFactory
{

    private static Map<Class, JiBXUtil> jibuMap = Collections.synchronizedMap(new HashMap<Class, JiBXUtil>());

    private JiBXObjectFactory()
    {
        // never instantiate
    }

    /**
     * Unmarshal from the given byte array.
     * 
     * @param clazz
     *        the implementing JiBX-bound class
     * @param objectXML
     *        byte array to deserialize from
     * @return unmarshalled JiBX-bound object
     * @throws XMLDeserializationException
     *         as a wrapper for {@link JiBXException}s
     */
    public static Object unmarshal(Class clazz, byte[] objectXML) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(objectXML);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
    }

    public static Object unmarshal(Class clazz, Document document) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(document);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
    }

    public static Object unmarshal(Class clazz, Element element) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(element);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
    }

    public static Object unmarshal(Class clazz, File file) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(file);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
        catch (IOException e)
        {
            throw new XMLDeserializationException(e);
        }
    }

    public static Object unmarshalFile(Class clazz, String filename) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshalFile(filename);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
        catch (IOException e)
        {
            throw new XMLDeserializationException(e);
        }
    }

    /**
     * Unmarshal from the given InputStream. The InputStream is closed after usage.
     * 
     * @param clazz
     *        the implementing JiBX-bound class
     * @param inStream
     *        InputStream to deserialize from
     * @return unmarshalled JiBX-bound object
     * @throws XMLDeserializationException
     *         as a wrapper for {@link JiBXException}s
     * @throws IOException
     *         if the InputStream could not be closed
     */
    public static Object unmarshal(Class clazz, InputStream inStream) throws XMLDeserializationException, IOException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(inStream);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
        finally
        {
            inStream.close();
        }
    }

    public static Object unmarshal(Class clazz, String xmlString) throws XMLDeserializationException
    {
        try
        {
            return getJiBXUtil(clazz).unmarshal(xmlString);
        }
        catch (JiBXException e)
        {
            remove(clazz); // no need to keep erronous JiBXUtil
            throw new XMLDeserializationException(e);
        }
    }

    /**
     * Get a {@link JiBXUtil} for the given class.
     * 
     * @param clazz
     *        the implementing JiBX-bound class
     * @return JiBXUtil for the given class
     */
    public static JiBXUtil getJiBXUtil(Class clazz)
    {
        synchronized (jibuMap)
        {
            JiBXUtil jibu = jibuMap.get(clazz);
            if (jibu == null)
            {
                jibu = new JiBXUtil(clazz);
                jibuMap.put(clazz, jibu);
            }
            return jibu;
        }
    }

    /**
     * Remove the {@link JiBXUtil} for the given class.
     * 
     * @param clazz
     *        the implementing JiBX-bound class
     * @return JiBXUtil for the given class or <code>null</code> if it was not here
     */
    public static JiBXUtil remove(Class clazz)
    {
        synchronized (jibuMap)
        {
            return jibuMap.remove(clazz);
        }
    }

}
