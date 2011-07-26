package nl.knaw.dans.easy.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

// ecco: CHECKSTYLE: OFF

public abstract class AbstractJibxTest<T> extends TestHelper
{

    private static final int    INDENT = 4;
    private static final String TEMP   = "src/test/resources/output/jibx/";

    private Class<T>            type;
    private IBindingFactory     bindingFactory;

    public AbstractJibxTest(Class<T> type)
    {
        this.type = type;
    }

    protected String marshal(T object) throws IOException, JiBXException
    {
        return marshal(object, "");
    }

    /**
     * Marshal the given object to a file. The given postfix is incorporated in the filename.
     * 
     * @param object
     *        object to marshal
     * @param postfix
     *        will be in filename
     * @return the name of the file
     * @throws IOException
     *         can happ'n
     * @throws JiBXException
     *         if you work too hard
     */
    protected String marshal(T object, String postfix) throws IOException, JiBXException
    {
        String fileName = TEMP + object.getClass().getSimpleName() + postfix + ".xml";
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(fileName);
            marshal(object, fos);
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
        return fileName;
    }

    protected void marshal(T object, OutputStream outStream) throws JiBXException
    {
        getMarshallingContext().marshalDocument(object, null, true, outStream);
    }

    protected T unmarshal(String filename) throws IOException, JiBXException
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

    @SuppressWarnings("unchecked")
    protected T unmarshal(InputStream inStream) throws JiBXException
    {
        T object = (T) getUnmarshallingContext().unmarshalDocument(inStream, null);
        return object;
    }

    private IMarshallingContext getMarshallingContext() throws JiBXException
    {
        IMarshallingContext context = getBindingFactory().createMarshallingContext();
        context.setIndent(INDENT);
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
     * @return
     * @throws JiBXException
     */
    private IBindingFactory getBindingFactory() throws JiBXException
    {
        if (bindingFactory == null)
        {
            bindingFactory = BindingDirectory.getFactory(type);
        }
        return bindingFactory;
    }

}
