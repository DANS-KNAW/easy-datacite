package nl.knaw.dans.pf.language.xml.crosswalk;


import java.net.URL;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.validation.AbstractValidator;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.junit.Test;
import org.xml.sax.SAXParseException;

public class CrosswalkerTest2
{
    
    @Test
    public void setReporter() throws Exception
    {
        
        Crosswalker<Object, AbstractValidator> crossWalker 
        = new Crosswalker<Object, AbstractValidator>(null, null);
        
        crossWalker.setReporter(Reporter.warn);
        XMLErrorHandler handler = crossWalker.getXmlErrorHandler();
        handler.error(new SAXParseException("error", "publicId", "systemId", 1, 2));
    }
    
    private class V extends nl.knaw.dans.pf.language.xml.validation.AbstractValidator
    {

        @Override
        public URL getSchemaURL(String version) throws SchemaCreationException
        {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

}
