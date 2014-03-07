package nl.knaw.dans.common.lang.xml;

import java.io.InputStream;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Abstract XMLValidator for XMLBeans and their serialized manifestations. Implementations of this
 * abstract class should present themselves as Singleton in order to make economic use of resources (the
 * internal versionMap is not static!).
 * 
 * @author ecco Apr 8, 2009
 * @param <T>
 *        object that is subject of validation
 */
public abstract class AbstractXMLBeanValidator<T extends XMLBean> extends AbstractValidator
{

    /**
     * Validate the given XMLBean against the schema of its version.
     * 
     * @param bean
     *        the xmlBean to validate
     * @return the result of the validation
     * @throws XMLException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public XMLErrorHandler validate(final T bean) throws XMLException, SAXException, SchemaCreationException
    {
        final InputStream xmlStream = bean.asXMLInputStream(0);
        return validate(xmlStream, bean.getVersion());
    }

    /**
     * Validate the given XMLBean against the schema of its version and report to the given handler.
     * 
     * @param handler
     *        receives notification of {@link org.xml.sax.SAXParseException}s
     * @param bean
     *        the XMLBean to validate
     * @throws XMLException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public void validate(final ErrorHandler handler, final T bean) throws XMLException, SAXException, SchemaCreationException
    {
        final InputStream xmlStream = bean.asXMLInputStream(0);
        validate(handler, xmlStream, bean.getVersion());
    }

}
