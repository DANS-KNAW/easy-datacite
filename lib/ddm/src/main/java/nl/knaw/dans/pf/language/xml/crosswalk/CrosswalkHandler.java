package nl.knaw.dans.pf.language.xml.crosswalk;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class CrosswalkHandler<T> extends DefaultHandler {
    private T target;
    private XMLReader reader;
    private Locator locator;
    private CrosswalkHandlerMap<T> handlerMap;
    private CrosswalkHandler<T> parentHandler;

    private StringBuffer charsSinceStart = new StringBuffer();
    private Attributes attributes = null;
    private int level;

    /**
     * Create a handler. It may receive control by the startElement of another instance and will return control by the endElement at the same level.
     */
    public CrosswalkHandler() {}

    /**
     * Creates the root handler. Assigns itself as ContentHandler to the reader passed into it. May temporarily hand over control to subclasses or siblings as
     * configured by the handlerMap.
     * 
     * @param target
     * @param reader
     * @param handlerMap
     */
    public CrosswalkHandler(final T target, final XMLReader reader, final CrosswalkHandlerMap<T> handlerMap) {
        this.handlerMap = handlerMap;
        takeControl(target, reader, null);
    }

    private void takeControl(final T target, final XMLReader reader, final CrosswalkHandler<T> parentHandler) {
        this.target = target;
        this.reader = reader;
        this.parentHandler = parentHandler;
        this.reader.setContentHandler(this);
        if (parentHandler != null) {
            handlerMap = parentHandler.handlerMap;

            // only the root handler receives a location notification from the SAX parser
            locator = parentHandler.locator;
        }
        // clear what is left by a previous cycle
        charsSinceStart.delete(0, charsSinceStart.length());
        level = 0;
    }

    private void returnControl() {
        this.reader.setContentHandler(parentHandler);
        this.target = null;
        this.reader = null;
        parentHandler.level--;
        this.parentHandler = null;
    }

    @Override
    public final void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        charsSinceStart = new StringBuffer();
        final CrosswalkHandler<T> handler = handlerMap.getHandler(uri, localName, attributes);
        level++;
        if (handler != null) {
            handler.takeControl(target, reader, this);
            handler.initFirstElement(uri, localName, attributes);
            handler.attributes = attributes;
        } else {
            this.attributes = attributes;
            if (parentHandler == null && handlerMap.reportMissingHandler(uri, localName, attributes))
                warning("skipped " + qName + " at level:" + level);
        }
        initElement(uri, localName, attributes);
    }

    /**
     * Receive notification of the start of the first element treated by this handler. Especially in the case of a complex element: Be aware of remnants of a
     * previous cycle. See also the final startElement
     */
    protected void initFirstElement(final String uri, final String localName, final Attributes attributes) throws SAXException {}

    /**
     * Receive notification of the start of the any element treated by this handler. See also the final startElement
     */
    protected void initElement(final String uri, final String localName, final Attributes attributes) throws SAXException {}

    @Override
    public final void endElement(final String uri, final String localName, final String qName) throws SAXException {
        finishElement(uri, localName);
        if (--level < 0)
            returnControl();
    }

    /**
     * Receive notification of the end of any element treated by this handler. See the final {@link #endElement(String, String, String)}.
     */
    protected void finishElement(final String uri, final String localName) throws SAXException {}

    @Override
    public final void characters(final char ch[], final int start, final int length) throws SAXException {
        charsSinceStart.append(ch, start, length);
        chars(ch, start, length);
    }

    /** See the final {@link DefaultHandler#characters(char[], int, int)}. */
    public void chars(final char ch[], final int start, final int length) throws SAXException {}

    /** @return the target object */
    protected T getTarget() {
        return target;
    }

    /**
     * The characters since the last startElement. Beware of mixed content, for example: <br>
     * &lt;p> Some &lt;b>bold&lt;/b> text &lt;/p><br>
     * 
     * @return If called by {@link #finishElement(String, String)} for &lt;p>: "bold text". The string " Some " should be accessed otherwise through
     *         {@link #chars(char[], int, int)}.
     */
    protected String getCharsSinceStart() {
        return charsSinceStart.toString();
    }

    /**
     * The attributes saved by the last startElement. Beware of mixed/complex content, for example: <br>
     * &lt;p> Some &lt;b>bold&lt;/b> text &lt;/p><br>
     * 
     * @return If called by {@link #finishElement(String, String)} for &lt;p>: you'll get an attribute of &lt;b> if it had any.
     */
    protected String getAttribute(String uri, final String localName) {
        return attributes == null ? null : attributes.getValue(uri, localName);
    }

    @Override
    public final void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    protected void warning(final String message) throws SAXException {
        reader.getErrorHandler().warning(new SAXParseException(message, locator));
    }

    protected void error(final String message) throws SAXException {
        reader.getErrorHandler().error(new SAXParseException(message, locator));
    }

    protected void fatalError(final String message) throws SAXException {
        reader.getErrorHandler().fatalError(new SAXParseException(message, locator));
    }
}
