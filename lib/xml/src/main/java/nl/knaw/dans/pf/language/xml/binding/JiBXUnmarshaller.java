package nl.knaw.dans.pf.language.xml.binding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * {@link XMLUnmarshaller} for JiBX-style deserialization
 * 
 * @author ecco
 * @param <T>
 *        the unmarshalled object
 */
public class JiBXUnmarshaller<T> implements XMLUnmarshaller<T> {

    private static TransformerFactory W3C_TRANSFORMER_FACTORY;

    private final String bindingName;
    private final Class<? extends T> beanClass;
    private IBindingFactory bindingFactory;
    private IUnmarshallingContext unmarshallingContext;
    private String encoding = Encoding.UTF8;

    private Transformer w3cTransformer;

    /**
     * Constructs a JiBXUnmarshaller for the given beanClass.
     * <p/>
     * The given beanClass should have a top-level root binding.
     * 
     * @param beanClass
     *        object to be unmarshalled
     */
    public JiBXUnmarshaller(Class<? extends T> beanClass) {
        this.beanClass = beanClass;
        this.bindingName = null;
    }

    /**
     * Constructs a JiBXUnmarshaller for the given bindingName and beanClass. Parameter <code>bindingName</code> is the name of the binding file stripped of its
     * extension. File name <code>my-bean-binding.xml</code> has the bindingName <code>my_bean_binding</code>.
     * <p/>
     * The given beanClass should have a top-level binding. Top-level bindings have a (element) name and do not declare abstract="true".
     * 
     * @param bindingName
     *        the bindingName of the given beanClass or the bindingName of one of the bindings in the same hierarchy.
     * @param beanClass
     *        the class of the bound object
     */
    public JiBXUnmarshaller(String bindingName, Class<? extends T> beanClass) {
        this.beanClass = beanClass;
        this.bindingName = bindingName;
    }

    @Override
    public void setEncoding(String enc) {
        this.encoding = enc;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public T unmarshal(InputStream inStream) throws XMLDeserializationException {
        return unmarshal(inStream, encoding);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(InputStream inStream, String enc) throws XMLDeserializationException {
        T bean;
        try {
            IUnmarshallingContext uContext = getUnMarshallingContext();
            bean = (T) uContext.unmarshalDocument(inStream, enc);
        }
        catch (JiBXException e) {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(Reader reader) throws XMLDeserializationException {
        T bean;
        try {
            IUnmarshallingContext uContext = getUnMarshallingContext();
            bean = (T) uContext.unmarshalDocument(reader);
        }
        catch (JiBXException e) {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }

    @Override
    public T unmarshal(String xmlString) throws XMLDeserializationException {
        return unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
    }

    @Override
    public T unmarshal(byte[] bytes) throws XMLDeserializationException {
        return unmarshal(new ByteArrayInputStream(bytes));
    }

    @Override
    public T unmarshal(Source source) throws XMLDeserializationException {
        if (source instanceof StreamSource) {
            return unmarshal(((StreamSource) source).getInputStream());
        } else {
            throw new XMLDeserializationException("Cannot unmarshal from " + source);
        }
    }

    @Override
    public T unmarshal(Document document) throws XMLDeserializationException {
        String enc = document.getXMLEncoding();
        if (enc != null) {
            return unmarshal(new ByteArrayInputStream(document.asXML().getBytes()), enc);
        } else {
            return unmarshal(document.asXML());
        }
    }

    @Override
    public T unmarshal(Element element) throws XMLDeserializationException {
        String enc = null;
        Document document = element.getDocument();
        if (document != null) {
            enc = document.getXMLEncoding();
        }

        if (enc != null) {
            return unmarshal(new ByteArrayInputStream(element.asXML().getBytes()), enc);
        } else {
            return unmarshal(element.asXML());
        }
    }

    @Override
    public T unmarshal(org.w3c.dom.Document document) throws XMLDeserializationException {
        return unmarshal(document.getDocumentElement());
    }

    @Override
    public T unmarshal(org.w3c.dom.Element element) throws XMLDeserializationException {
        T bean;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DOMSource source = new DOMSource(element);
            StreamResult result = new StreamResult(out);
            getW3cTransformer().transform(source, result);
            bean = unmarshal(out.toByteArray());
        }
        catch (TransformerException e) {
            throw new XMLDeserializationException(e);
        }
        return bean;
    }

    /**
     * Get the unmarshalling context, the actual deserializer from xml.
     * 
     * @return the same instance at each call
     * @throws JiBXException
     */
    protected IUnmarshallingContext getUnMarshallingContext() throws JiBXException {
        if (unmarshallingContext == null) {
            unmarshallingContext = getBindingFactory().createUnmarshallingContext();
        }
        return unmarshallingContext;
    }

    /**
     * Get the binding factory.
     * 
     * @return the same instance at each call
     * @throws JiBXException
     */
    protected IBindingFactory getBindingFactory() throws JiBXException {
        if (bindingFactory == null) {
            if (bindingName == null) {
                bindingFactory = BindingDirectory.getFactory(beanClass);
            } else {
                bindingFactory = BindingDirectory.getFactory(bindingName, beanClass);
            }
        }
        return bindingFactory;
    }

    protected Transformer getW3cTransformer() throws XMLDeserializationException {
        try {
            if (w3cTransformer == null) {
                w3cTransformer = getW3cTransformerFactory().newTransformer();
            } else {
                w3cTransformer.reset();
            }
        }
        catch (TransformerConfigurationException e) {
            throw new XMLDeserializationException(e);
        }
        return w3cTransformer;
    }

    public static TransformerFactory getW3cTransformerFactory() {
        if (W3C_TRANSFORMER_FACTORY == null) {
            W3C_TRANSFORMER_FACTORY = TransformerFactory.newInstance();
        }
        return W3C_TRANSFORMER_FACTORY;
    }

    public static void setW3cTransformerFactory(TransformerFactory factory) {
        W3C_TRANSFORMER_FACTORY = factory;
    }
}
