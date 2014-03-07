package nl.knaw.dans.common.lang.xml;

import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// ecco: CHECKSTYLE: OFF

public class XMLErrorHandlerTest
{

    private static final Logger logger = LoggerFactory.getLogger(XMLErrorHandlerTest.class);

    @Test
    public void testReporter() throws SAXException
    {
        Locator locator = new Locator()
        {

            public int getColumnNumber()
            {
                return 0;
            }

            public int getLineNumber()
            {
                return 0;
            }

            public String getPublicId()
            {
                return null;
            }

            public String getSystemId()
            {
                return null;
            }

        };
        SAXParseException parseException = new SAXParseException("bla bla", locator);

        XMLErrorHandler handler = new XMLErrorHandler();
        logger.debug("expected 3 messages at DEBUG level.");
        handler.warning(parseException);
        handler.error(parseException);
        handler.fatalError(parseException);
        Assert.assertEquals(3, handler.getNotificationCount());

        handler = new XMLErrorHandler(Reporter.off);
        logger.debug("expected no messages at all.");
        handler.warning(parseException);
        handler.error(parseException);
        handler.fatalError(parseException);
        Assert.assertEquals(3, handler.getNotificationCount());

        handler = new XMLErrorHandler(Reporter.warn);
        logger.debug("expected 3 messages at WARN level.");
        handler.warning(parseException);
        handler.error(parseException);
        handler.fatalError(parseException);
        Assert.assertEquals(3, handler.getNotificationCount());

        handler = new XMLErrorHandler(Reporter.error);
        logger.debug("expected 3 messages at ERROR level.");
        handler.warning(parseException);
        handler.error(parseException);
        handler.fatalError(parseException);
        Assert.assertEquals(3, handler.getNotificationCount());

        Reporter reporter = Reporter.debug;
        logger.debug("expected a peculiar DEBUG message.");
        reporter.evaluate(parseException, "peculiar", 100000);
    }

}
