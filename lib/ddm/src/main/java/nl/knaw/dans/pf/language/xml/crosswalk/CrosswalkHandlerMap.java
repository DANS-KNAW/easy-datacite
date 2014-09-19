package nl.knaw.dans.pf.language.xml.crosswalk;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface CrosswalkHandlerMap<T> {
    /**
     * Get another handler to take over parsing control at the start of the current element. For description of the arguments see
     * {@link CrosswalkHandler#startElement(String, String, String, Attributes)}
     * 
     * @param uri
     * @param localName
     * @param attributes
     * @return if null the current handler will continue to have parse control
     * @throws SAXException
     */
    public CrosswalkHandler<T> getHandler(final String uri, final String localName, final Attributes attributes) throws SAXException;

    /**
     * Notification of an element without a handler of its own. For description of the arguments see
     * {@link CrosswalkHandler#startElement(String, String, String, Attributes)}
     * 
     * @param uri
     * @param localName
     * @param attributes
     * @return true if a warning should be generated (the configuration is incomplete)
     * @throws SAXException
     */
    boolean reportMissingHandler(final String uri, final String localName, final Attributes attributes) throws SAXException;
}
