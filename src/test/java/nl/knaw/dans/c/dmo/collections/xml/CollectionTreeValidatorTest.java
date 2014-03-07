package nl.knaw.dans.c.dmo.collections.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import nl.knaw.dans.common.lang.xml.XMLErrorHandler;

import org.junit.Test;

public class CollectionTreeValidatorTest
{
    
    @Test
    public void validateFromURL() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/jibcol.xml");
        // XmlValidator closes the input stream ...
        assertTrue(CollectionTreeValidator.instance().validate(url.openStream(), null).passed());
        // but it can be reopened
        assertTrue(CollectionTreeValidator.instance().validate(url.openStream(), null).passed());
    }
    
    @Test
    public void validateWrongXml() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/jibcol-wrong1.xml");
        XMLErrorHandler handler = CollectionTreeValidator.instance().validate(url.openStream(), null);
        
        assertFalse(handler.passed());
    }

}
