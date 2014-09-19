package nl.knaw.dans.pf.language.xml.binding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.xml.sax.SAXException;

/**
 * {@link XMLMarshaller} for JiBX-style serialization.
 * 
 * @author ecco
 */
public class JiBXMarshaller implements XMLMarshaller {

    private static DocumentBuilderFactory W3C_DOCUMENT_BUILDER_FACTORY;

    private final String bindingName;
    private final Object bean;

    private IBindingFactory bindingFactory;
    private IMarshallingContext marshallingContext;

    private String encoding = Encoding.UTF8;
    private int indent = 4;
    private boolean standalone = true;
    private boolean omitXmlDeclaration;

    private DocumentBuilder w3cDomBuilder;

    /**
     * Constructs a JiBXMarshaller with the given Object as bean for serialization.
     * <p/>
     * If the given object has no binding, serialization will cause a org.jibx.runtime.JiBXException with 'Unable to access binding information for class ...'.
     * <p/>
     * If the given object is not the root of the binding, serialization will cause a org.jibx.runtime.JiBXException with 'Multiple bindings defined for class
     * ...'. See {@link #JiBXMarshaller(String, Object)} for serialization of none-root bindings.
     * 
     * @param bean
     *        the object to serialize.
     */
    public JiBXMarshaller(Object bean) {
        this.bindingName = null;
        this.bean = bean;
    }

    /**
     * Constructs a JiBXMarshaller for the given bindingName, with the given Object as bean for serialization. Parameter <code>bindingName</code> is the name of
     * the binding file stripped of its extension. File name <code>my-bean-binding.xml</code> has the bindingName <code>my_bean_binding</code>.
     * <p/>
     * If the given object has no binding, serialization will cause a org.jibx.runtime.JiBXException with 'Unable to access binding information for class ...'.
     * <p/>
     * If the given object has no top-level mapping (i.e. abstract="true" in the binding-file), serialization will cause a org.jibx.runtime.JiBXException with
     * 'Supplied root object of class ... cannot be marshalled without top-level mapping'.
     * 
     * @param bindingName
     *        the bindingName of the given object or the bindingName of one of the bindings in the same hierarchy.
     * @param bean
     *        the object to serialize.
     */
    public JiBXMarshaller(String bindingName, Object bean) {
        this.bindingName = bindingName;
        this.bean = bean;
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
    public void setIndent(int indent) {
        this.indent = indent;
    }

    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public void setStandalone(boolean standAlone) {
        this.standalone = standAlone;
    }

    @Override
    public boolean getStandalone() {
        return standalone;
    }

    public boolean getOmitXmlDeclaration() {
        return omitXmlDeclaration;
    }

    public void setOmitXmlDeclaration(boolean omit) {
        this.omitXmlDeclaration = omit;
    }

    @Override
    public ByteArrayOutputStream getXmlOutputStream() throws XMLSerializationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out);
        return out;
    }

    @Override
    public void write(OutputStream out) throws XMLSerializationException {
        try {
            IMarshallingContext mContext = getMarshallingContext();
            mContext.setIndent(indent);
            if (omitXmlDeclaration) {
                mContext.setOutput(out, encoding);
                mContext.marshalDocument(bean);
            } else {
                mContext.marshalDocument(bean, encoding, standalone, out);
            }
        }
        catch (JiBXException e) {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public void write(Writer out) throws XMLSerializationException {
        try {
            IMarshallingContext mContext = getMarshallingContext();
            mContext.setIndent(indent);
            if (omitXmlDeclaration) {
                mContext.setOutput(out);
                mContext.marshalDocument(bean);
            } else {
                mContext.marshalDocument(bean, encoding, standalone, out);
            }
        }
        catch (JiBXException e) {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public String getXmlString() throws XMLSerializationException {
        return getXmlOutputStream().toString();
    }

    @Override
    public byte[] getXmlByteArray() throws XMLSerializationException {
        return getXmlOutputStream().toByteArray();
    }

    @Override
    public InputStream getXmlInputStream() throws XMLSerializationException {
        return new ByteArrayInputStream(getXmlByteArray());
    }

    @Override
    public Source getXmlSource() throws XMLSerializationException {
        return new StreamSource(getXmlInputStream());
    }

    @Override
    public Document getXmlDocument() throws XMLSerializationException {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(getXmlInputStream());
        }
        catch (DocumentException e) {
            throw new XMLSerializationException(e);
        }
    }

    @Override
    public Element getXmlElement() throws XMLSerializationException {
        return getXmlDocument().getRootElement();
    }

    @Override
    public org.w3c.dom.Document getW3cDomDocument() throws XMLSerializationException {
        org.w3c.dom.Document document;
        try {
            document = getW3cDocumentBuilder().parse(getXmlInputStream());
        }
        catch (SAXException e) {
            throw new XMLSerializationException(e);
        }
        catch (IOException e) {
            throw new XMLSerializationException(e);
        }
        return document;
    }

    @Override
    public org.w3c.dom.Element getW3cDomElement() throws XMLSerializationException {
        return getW3cDomDocument().getDocumentElement();
    }

    /**
     * Get the marshalling context, the actual serializer to xml.
     * 
     * @return the same instance at each call
     * @throws JiBXException
     */
    protected IMarshallingContext getMarshallingContext() throws JiBXException {
        if (marshallingContext == null) {
            marshallingContext = getBindingFactory().createMarshallingContext();
        }
        return marshallingContext;
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
                bindingFactory = BindingDirectory.getFactory(bean.getClass());
            } else {
                bindingFactory = BindingDirectory.getFactory(bindingName, bean.getClass());
            }
        }
        return bindingFactory;
    }

    protected DocumentBuilder getW3cDocumentBuilder() throws XMLSerializationException {
        try {
            if (w3cDomBuilder == null) {
                w3cDomBuilder = getW3cDocumentBuilderFactory().newDocumentBuilder();
            } else {
                w3cDomBuilder.reset();
            }
        }
        catch (ParserConfigurationException e) {
            throw new XMLSerializationException(e);
        }
        return w3cDomBuilder;
    }

    public static DocumentBuilderFactory getW3cDocumentBuilderFactory() {
        if (W3C_DOCUMENT_BUILDER_FACTORY == null) {
            W3C_DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        }
        return W3C_DOCUMENT_BUILDER_FACTORY;
    }

    public static void setW3cDocumentBuilderFactory(DocumentBuilderFactory factory) {
        W3C_DOCUMENT_BUILDER_FACTORY = factory;
    }

}
