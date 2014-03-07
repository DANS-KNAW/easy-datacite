package nl.knaw.dans.common.jibx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.common.lang.xml.MinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLBean;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Marshals JiBX-bound objects to and from streams, files, documents, (writers not implemented yet) ...
 * 
 * @author ecco
 * @param <T>
 *        the type of the JiBX-bound object
 */
public class JiBXUtil<T extends Object>
{

    private final Class<? extends T> clazz;
    private IBindingFactory bindingFactory;

    /**
     * Construct a new JiBXUtil for the given class.
     * 
     * @param clazz
     *        the precise implementing class that is bound by a JiBX-binding.
     */
    @SuppressWarnings("unchecked")
    public JiBXUtil(final Class<? extends T> clazz)
    {
        this.clazz = (Class<T>) clazz;
    }

    /**
     * Marshal the <code>root</code>-object to the given output stream. The caller of this method has to handle proper
     * closing of the stream.
     * 
     * @param root
     *        the object to be marshaled
     * @param outStream
     *        the output stream to use
     * @param indent
     *        indent the xml with the given number of spaces
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public void marshalDocument(final T root, final OutputStream outStream, final int indent) throws JiBXException
    {
        try
        {
            getMarshallingContext(indent).marshalDocument(root, "UTF-8", true, outStream);
        }
        catch (JiBXException e)
        {
            throw e;
        }
        catch (Exception e) // also throws "java.io.IOException: Illegal character code 0xdbff in content text" for instance!
        {
            throw new JiBXException("Unable to marshal document: ", e);
        }
    }

    /**
     * Marshal the <code>root</code>-object to the given output stream with no indent. The caller of this method has to
     * handle proper closing of the stream.
     * 
     * @param root
     *        the object to be marshaled
     * @param outStream
     *        the output stream to use
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public void marshalDocument(final T root, final OutputStream outStream) throws JiBXException
    {
        marshalDocument(root, outStream, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to the given writer.
     * 
     * @param root
     *        the object to be marshaled
     * @param encoding
     *        document encoding, null uses UTF-8 default
     * @param writer
     *        writer for document data output
     * @param indent
     *        indent the xml with the given number of spaces
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public void marshalDocument(final T root, final String encoding, final Writer writer, final int indent) throws JiBXException
    {
        try
        {
            getMarshallingContext(indent).marshalDocument(root, encoding, null, writer);
        }
        catch (JiBXException e)
        {
            throw e;
        }
        catch (Exception e) // also throws "java.io.IOException: Illegal character code 0xdbff in content text" for instance!
        {
            throw new JiBXException("Unable to marshal document: ", e);
        }
    }

    /**
     * Marshal the <code>root</code>-object to the given writer.
     * 
     * @param root
     *        the object to be marshaled
     * @param encoding
     *        document encoding, null uses UTF-8 default
     * @param writer
     *        writer for document data output
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public void marshalDocument(final T root, final String encoding, final Writer writer) throws JiBXException
    {
        marshalDocument(root, encoding, writer, XMLBean.NEW_LINE_ONLY);
    }

    /**
     * Marshal the <code>root</code>-object to a string.
     * 
     * @param root
     *        the object to be marshaled
     * @param indent
     *        indent the xml with the given number of spaces
     * @return <code>root</code>-object as xml-string
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public String marshalDocument(final T root, final int indent) throws JiBXException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshalDocument(root, baos, indent);
        return baos.toString();
    }

    /**
     * Marshal the <code>root</code>-object to a string.
     * 
     * @param root
     *        the object to be marshaled
     * @return <code>root</code>-object as xml-string
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public String marshalDocument(final T root) throws JiBXException
    {
        return marshalDocument(root, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to the given file.
     * 
     * @param root
     *        the object to be marshaled
     * @param file
     *        the file to use
     * @param indent
     *        indent the xml with the given number of spaces
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public void marshalDocument(final T root, final File file, final int indent) throws IOException, JiBXException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
            marshalDocument(root, fos, indent);
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
    }

    /**
     * Marshal the <code>root</code>-object to the given file with no xml-indenting.
     * 
     * @param root
     *        the object to be marshaled
     * @param file
     *        the file to use
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     */
    public void marshalDocument(final T root, final File file) throws IOException, JiBXException
    {
        marshalDocument(root, file, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a file with the given filename.
     * 
     * @param root
     *        the object to be marshaled
     * @param filename
     *        name of the file
     * @param indent
     *        indent the xml with the given number of spaces
     * @return the filename of the newly created file
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public String marshalDocument(final T root, final String filename, final int indent) throws IOException, JiBXException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(filename);
            marshalDocument(root, fos, indent);
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
        return filename;
    }

    /**
     * Marshal the <code>root</code>-object to a file with the given filename with no xml-indenting.
     * 
     * @param root
     *        the object to be marshaled
     * @param filename
     *        name of the file
     * @return the filename of the newly created file
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     */
    public String marshalDocument(final T root, final String filename) throws IOException, JiBXException
    {
        return marshalDocument(root, filename, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a {@link InputStream}.
     * 
     * @param root
     *        Object to marshal
     * @param indent
     *        indent the xml with the given number of spaces
     * @return InputStream for root
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public InputStream getInputStream(final T root, final int indent) throws JiBXException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshalDocument(root, baos, indent);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Marshal the <code>root</code>-object to a {@link InputStream}.
     * 
     * @param root
     *        Object to marshal
     * @return InputStream for root
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public InputStream getInputStream(final T root) throws JiBXException
    {
        return getInputStream(root, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a {@link Source}.
     * 
     * @param root
     *        Object to marshal
     * @return Source for root
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public Source getSource(final T root) throws JiBXException
    {
        return new StreamSource(getInputStream(root, XMLBean.NO_INDENT));
    }

    /**
     * Marshal the <code>root</code>-object to a {@link Document}.
     * 
     * @param root
     *        Object to marshal
     * @param indent
     *        indent the xml with the given number of spaces
     * @return Document for root
     * @throws DocumentException
     *         nested Exception which may be thrown during the processing of a DOM4J document
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public Document getDocument(final T root, final int indent) throws DocumentException, JiBXException
    {
        final SAXReader reader = new SAXReader();
        return reader.read(getInputStream(root, indent));
    }

    /**
     * Marshal the <code>root</code>-object to a {@link Document}.
     * 
     * @param root
     *        Object to marshal
     * @return Document for root
     * @throws DocumentException
     *         nested Exception which may be thrown during the processing of a DOM4J document
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public Document getDocument(final T root) throws DocumentException, JiBXException
    {
        return getDocument(root, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to the given output stream without emitting an xml-declaration. The caller
     * of this method has to handle proper closing of the stream.
     * 
     * @param root
     *        the object to be marshaled
     * @param outStream
     *        the output stream to use
     * @param indent
     *        indent the xml with the given number of spaces
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public void marshalElement(final T root, final OutputStream outStream, final int indent) throws JiBXException
    {
        final IMarshallingContext mctx = getMarshallingContext(indent);
        mctx.setOutput(outStream, "UTF-8");
        try
        {
            mctx.marshalDocument(root, "UTF-8", false);
        }
        catch (JiBXException e)
        {
            throw e;
        }
        catch (Exception e) // also throws "java.io.IOException: Illegal character code 0xdbff in content text" for instance!
        {
            throw new JiBXException("Unable to marshal document: ", e);
        }
    }

    /**
     * Marshal the <code>root</code>-object to the given output stream with no indent and without emitting an
     * xml-declaration. The caller of this method has to handle proper closing of the stream.
     * 
     * @param root
     *        the object to be marshaled
     * @param outStream
     *        the output stream to use
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public void marshalElement(final T root, final OutputStream outStream) throws JiBXException
    {
        marshalElement(root, outStream, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a string without emitting an xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @param indent
     *        indent the xml with the given number of spaces
     * @return <code>root</code>-object as xml-string
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public String marshalElement(final T root, final int indent) throws JiBXException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshalElement(root, baos, indent);
        return baos.toString();
    }

    /**
     * Marshal the <code>root</code>-object to a string without emitting an xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @return <code>root</code>-object as xml-string
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public String marshalElement(final T root) throws JiBXException
    {
        return marshalElement(root, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to the given file without emitting an xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @param file
     *        the file to use
     * @param indent
     *        indent the xml with the given number of spaces
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public void marshalElement(final T root, final File file, final int indent) throws IOException, JiBXException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
            marshalElement(root, fos, indent);
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
    }

    /**
     * Marshal the <code>root</code>-object to the given file with no xml-indenting and without emitting an
     * xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @param file
     *        the file to use
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     */
    public void marshalElement(final T root, final File file) throws IOException, JiBXException
    {
        marshalElement(root, file, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a file with the given filename and without emitting an xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @param filename
     *        name of the file
     * @param indent
     *        indent the xml with the given number of spaces
     * @return the filename of the newly created file
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     * @see MinimalXMLBean#NEW_LINE_ONLY
     */
    public String marshalElement(final T root, final String filename, final int indent) throws IOException, JiBXException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(filename);
            marshalElement(root, fos, indent);
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
        return filename;
    }

    /**
     * Marshal the <code>root</code>-object to a file with the given filename with no xml-indenting and without emitting
     * an xml-declaration.
     * 
     * @param root
     *        the object to be marshaled
     * @param filename
     *        name of the file
     * @return the filename of the newly created file
     * @throws IOException
     *         if an IOException occurred
     * @throws JiBXException
     *         if we were unable to marshal the object
     * @see MinimalXMLBean#NO_INDENT
     */
    public String marshalElement(final T root, final String filename) throws IOException, JiBXException
    {
        return marshalElement(root, filename, XMLBean.NO_INDENT);
    }

    /**
     * Marshal the <code>root</code>-object to a {@link Element}.
     * 
     * @param root
     *        Object to marshal
     * @return Element for root
     * @throws DocumentException
     *         nested Exception which may be thrown during the processing of a DOM4J document
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public Element getElement(final T root) throws DocumentException, JiBXException
    {
        return getDocument(root).getRootElement();
    }

    /**
     * Marshal the <code>root</code>-object to a {@link Element}.
     * 
     * @param root
     *        Object to marshal
     * @param indent
     *        indent the xml with the given number of spaces
     * @return Element for root
     * @throws DocumentException
     *         nested Exception which may be thrown during the processing of a DOM4J document
     * @throws JiBXException
     *         if we were unable to marshal the object
     */
    public Element getElement(final T root, final int indent) throws DocumentException, JiBXException
    {
        return getDocument(root, indent).getRootElement();
    }

    /**
     * Unmarshal from the given input stream. The caller of this method has to handle proper closing of the stream.
     * 
     * @param inStream
     *        the input stream to unmarshal from
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     */
    @SuppressWarnings("unchecked")
    public T unmarshal(final InputStream inStream) throws JiBXException
    {
        return (T) getUnmarshallingContext().unmarshalDocument(inStream, "UTF-8");
    }

    /**
     * Unmarshal from the given string.
     * 
     * @param xmlString
     *        the string to unmarshal from
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     */
    public T unmarshal(final String xmlString) throws JiBXException
    {
        return unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
    }

    /**
     * Unmarshal from the given byte array.
     * 
     * @param objectXML
     *        the byte array to unmarshal from
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     */
    public T unmarshal(final byte[] objectXML) throws JiBXException
    {
        return unmarshal(new ByteArrayInputStream(objectXML));
    }

    /**
     * Unmarshal from the given file.
     * 
     * @param file
     *        the file to unmarshal from
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     * @throws IOException
     *         if an IOException occurred
     */
    public T unmarshal(final File file) throws IOException, JiBXException
    {
        InputStream fis = null;
        T object = null;
        try
        {
            fis = new FileInputStream(file);
            object = unmarshal(fis);
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
        return object;
    }

    /**
     * Unmarshal from a file with the given filename.
     * 
     * @param filename
     *        the name of the file to unmarshal from
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     * @throws IOException
     *         if an IOException occurred
     */
    public T unmarshalFile(final String filename) throws IOException, JiBXException
    {
        InputStream fis = null;
        T object = null;
        try
        {
            fis = new FileInputStream(filename);
            object = unmarshal(fis);
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
        return object;
    }

    /**
     * Unmarshal from a {@link Document}.
     * 
     * @param document
     *        the document to unmarshal
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     */
    public T unmarshal(final Document document) throws JiBXException
    {
        return unmarshal(new ByteArrayInputStream(document.asXML().getBytes()));
    }

    /**
     * Unmarshal from a {@link Element}.
     * 
     * @param element
     *        the elemnt to unmarshal
     * @return unmarshalled object
     * @throws JiBXException
     *         if we were unable to unmarshal the object
     */
    public T unmarshal(final Element element) throws JiBXException
    {
        return unmarshal(new ByteArrayInputStream(element.asXML().getBytes()));
    }

    private IMarshallingContext getMarshallingContext(final int indent) throws JiBXException
    {
        final IMarshallingContext context = getBindingFactory().createMarshallingContext();
        context.setIndent(indent);
        return context;
    }

    private IUnmarshallingContext getUnmarshallingContext() throws JiBXException
    {
        return getBindingFactory().createUnmarshallingContext();
    }

    /**
     * From "http://jibx.sourceforge.net/api/org/jibx/runtime/IBindingFactory.html": 'All binding factory instances are
     * guaranteed to be threadsafe and reusable.'
     * 
     * @return BindingFactory for the type used by this class
     * @throws JiBXException
     *         if som't went wrong
     */
    private IBindingFactory getBindingFactory() throws JiBXException
    {
        if (bindingFactory == null)
        {
            bindingFactory = BindingDirectory.getFactory(clazz);
        }
        return bindingFactory;
    }

}
