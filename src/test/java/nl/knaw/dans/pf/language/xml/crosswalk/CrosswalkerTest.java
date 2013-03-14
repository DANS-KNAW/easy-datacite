package nl.knaw.dans.pf.language.xml.crosswalk;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.xml.XMLConstants;

import nl.knaw.dans.pf.language.xml.validation.AbstractValidator2;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosswalkerTest
{
    private static final String XSD = "file://"+new File("src/test/resources/input/abstract.xsd").getAbsolutePath();

    private static CrosswalkHandler<StringBuffer> createSimpleHandler()
    {
        return new CrosswalkHandler<StringBuffer>()
        {
            @Override
            protected void finishElement(final String uri, final String localName) throws SAXException
            {
                getTarget().append("-" + getAttribute(XMLConstants.XML_NS_URI, "lang") + "-" + getCharsSinceStart());
                if ("xyz".equals(getCharsSinceStart()))
                {
                    error("no xyz allowed");
                    fatalError("we do not allow xyz");
                }
            }

        };
    }

    private static CrosswalkHandler<StringBuffer> createComplexHandler()
    {
        return new CrosswalkHandler<StringBuffer>()
        {
            @Override
            protected void finishElement(final String uri, final String localName) throws SAXException
            {
                if ("use".equals(localName))
                    getTarget().append("-" + getAttribute(XMLConstants.XML_NS_URI, "lang") + "-" + getCharsSinceStart());
                else
                    warning("skipping " + localName + " " + getCharsSinceStart());
            }

        };
    }

    private static CrosswalkHandlerMap<StringBuffer> createHandlerMap()
    {
        return new CrosswalkHandlerMap<StringBuffer>()
        {
            @Override
            public CrosswalkHandler<StringBuffer> getHandler(String uri, String localName, Attributes attributes) throws SAXException
            {
                if ("simple".equals(localName))
                    return createSimpleHandler();
                else if ("complex".equals(localName))
                    return createComplexHandler();
                return null;
            }

            @Override
            public boolean reportMissingHandler(String uri, String localName, Attributes attributes)
            {
                return false;
            }
        };
    }

    private class CW extends Crosswalker<StringBuffer, AbstractValidator2>
    {

        public CW(AbstractValidator2 validator)
        {
            super(validator, createHandlerMap());
        }

        public StringBuffer createFrom(final File file) throws CrosswalkException
        {
            return gurardedWalk(file, new StringBuffer());
        }

        public StringBuffer createFrom(final String xml) throws CrosswalkException
        {
            return guardedWalk(xml, new StringBuffer());
        }
    }

    private final CW crosswalk = new CW(new AbstractValidator2(XSD)
    {
    });

    @Test(expected = NullPointerException.class)
    public void noXSD() throws Exception
    {
        new CW(new AbstractValidator2((String[]) null)
        {
        }).createFrom("");
    }

    @Test(expected = CrosswalkException.class)
    public void emptyXml() throws Exception
    {
        crosswalk.createFrom("");
    }

    @Test
    public void invalidXml() throws Exception
    {
        StringBuffer result = crosswalk.createFrom("<noroot></noroot>");
        assertThat(result, nullValue());
    }

    @Test(expected = CrosswalkException.class)
    public void noFile() throws Exception
    {
        crosswalk.createFrom(new File("doesNot.Exist"));
    }

    @Test
    public void simple() throws Exception
    {
        StringBuffer result = crosswalk.createFrom("<?xml version='1.0' encoding='UTF-8'?><root><simple>abc</simple></root>");
        assertThat(result.toString(), is("-null-abc"));
    }

    @Test
    public void complex() throws Exception
    {
        StringBuffer result = crosswalk.createFrom(new File("src/test/resources/input/abstract.xml"));
        assertThat(result.toString(), is("-en-abc-nl-jkl"));
        assertThat(crosswalk.getXmlErrorHandler().getWarnings().size(), is(2));
    }

    @Test
    public void errors() throws Exception
    {
        StringBuffer result = crosswalk.createFrom("<?xml version='1.0' encoding='UTF-8'?><root><simple>xyz</simple></root>");
        assertThat(crosswalk.getXmlErrorHandler().getFatalErrors().size(), is(1));
        assertThat(crosswalk.getXmlErrorHandler().getErrors().size(), is(1));
        assertThat(result, nullValue());
    }

    @Before
    public void resetErrorHandler()
    {
        crosswalk.getXmlErrorHandler().reset();
    }

    @Before
    public void setLogLevel()
    {
        crosswalk.setReporter(Reporter.debug);
    }
}
