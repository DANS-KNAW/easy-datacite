package nl.knaw.dans.pf.language.emd.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.CharArrayReader;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.EmdCreator;
import nl.knaw.dans.pf.language.emd.EmdHelper;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;
import nl.knaw.dans.pf.language.xml.binding.Encoding;
import nl.knaw.dans.pf.language.xml.binding.JiBXMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarshallerTest
{
    private static final Logger logger = LoggerFactory.getLogger(MarshallerTest.class);

    boolean verbose = false;

    @Test
    public void marshallAndUnmarshallMethods() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(2, emd);

        EmdMarshaller m = new EmdMarshaller(emd);
        EmdUnmarshaller<EasyMetadata> um = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class);
        EasyMetadata emd2;

        // inputStream
        emd2 = um.unmarshal(m.getXmlInputStream());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // source
        emd2 = um.unmarshal(m.getXmlSource());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // document
        emd2 = um.unmarshal(m.getXmlDocument());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // element
        emd2 = um.unmarshal(m.getXmlElement());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // writer / reader
        emd2 = um.unmarshal(new CharArrayReader(m.getXmlString().toCharArray()));
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // w3c dom document
        emd2 = um.unmarshal(m.getW3cDomDocument());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

        // w3c dom element
        emd2 = um.unmarshal(m.getW3cDomElement());
        if (verbose)
            logger.debug("\n" + new EmdMarshaller(emd2).getXmlString());
        assertTrue(EMDValidator.instance().validate(emd2).passed());

    }

    @Test
    public void settings() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(2, emd);

        EmdMarshaller m = new EmdMarshaller(emd);
        assertTrue(m.getStandalone());
        assertEquals("UTF-8", m.getEncoding());
        assertFalse(m.getOmitXmlDeclaration());

        String xml = m.getXmlString();
        if (verbose)
            logger.debug("\n" + xml);
        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));

        m.setStandalone(false);
        xml = m.getXmlString();
        if (verbose)
            logger.debug("\n" + xml);
        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"));

        m.setEncoding(Encoding.US_ASCII);
        xml = m.getXmlString();
        if (verbose)
            logger.debug("\n" + xml);
        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"US-ASCII\" standalone=\"no\"?>"));

        m.setOmitXmlDeclaration(true);
        xml = m.getXmlString();
        if (verbose)
            logger.debug("\n" + xml);
        assertFalse(xml.contains("<?xml version=\"1.0\""));
    }

    @Test
    public void w3cDomDocumentSettings() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(2, emd);

        EmdMarshaller m = new EmdMarshaller(emd);
        assertTrue(m.getStandalone());
        assertEquals("UTF-8", m.getEncoding());
        assertFalse(m.getOmitXmlDeclaration());

        org.w3c.dom.Document document = m.getW3cDomDocument();

        assertTrue(document.getXmlStandalone());
        assertEquals("UTF-8", document.getXmlEncoding());
        assertEquals("1.0", document.getXmlVersion());

        m.setStandalone(false);
        document = m.getW3cDomDocument();

        assertFalse(document.getXmlStandalone());
        assertEquals("UTF-8", document.getXmlEncoding());
        assertEquals("1.0", document.getXmlVersion());

        m.setEncoding(Encoding.US_ASCII);
        document = m.getW3cDomDocument();

        assertFalse(document.getXmlStandalone());
        assertEquals("US-ASCII", document.getXmlEncoding());
        assertEquals("1.0", document.getXmlVersion());

        m.setOmitXmlDeclaration(true);
        document = m.getW3cDomDocument();

        assertFalse(document.getXmlStandalone());
        assertNull(document.getXmlEncoding());
        assertEquals("1.0", document.getXmlVersion());

    }

    @Test
    public void w3cDomElementSettings() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(2, emd);

        EmdMarshaller m = new EmdMarshaller(emd);
        assertTrue(m.getStandalone());
        assertEquals("UTF-8", m.getEncoding());
        assertFalse(m.getOmitXmlDeclaration());

        org.w3c.dom.Element element = m.getW3cDomElement();

        assertTrue(element.getOwnerDocument().getXmlStandalone());
        assertEquals("UTF-8", element.getOwnerDocument().getXmlEncoding());
        assertEquals("1.0", element.getOwnerDocument().getXmlVersion());

        m.setStandalone(false);
        element = m.getW3cDomElement();

        assertFalse(element.getOwnerDocument().getXmlStandalone());
        assertEquals("UTF-8", element.getOwnerDocument().getXmlEncoding());
        assertEquals("1.0", element.getOwnerDocument().getXmlVersion());

        m.setEncoding(Encoding.US_ASCII);
        element = m.getW3cDomElement();

        assertFalse(element.getOwnerDocument().getXmlStandalone());
        assertEquals("US-ASCII", element.getOwnerDocument().getXmlEncoding());
        assertEquals("1.0", element.getOwnerDocument().getXmlVersion());

        m.setOmitXmlDeclaration(true);
        element = m.getW3cDomElement();

        assertFalse(element.getOwnerDocument().getXmlStandalone());
        assertNull(element.getOwnerDocument().getXmlEncoding());
        assertEquals("1.0", element.getOwnerDocument().getXmlVersion());

    }

    @Test
    public void jibxMarshaller1ParaConstructor() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        JiBXMarshaller jm = new JiBXMarshaller(emd);

        // root-binding can be serialized
        if (verbose)
            logger.debug("\n" + jm.getXmlString());
    }

    @Test(expected = XMLSerializationException.class)
    public void jibxMarshaller1ParaConstructor2() throws Exception
    {
        EmdCreator ec = new EmdCreator();
        ec.getDcCreator().add(new BasicString("pietje"));
        JiBXMarshaller jm = new JiBXMarshaller(ec);
        // none-root binding cannot.
        jm.getXmlString();
    }

    @Test(expected = XMLSerializationException.class)
    public void noTopLevelMapping() throws Exception
    {
        Author author = new Author();
        JiBXMarshaller jm = new JiBXMarshaller(EmdMarshaller.BINDING_NAME, author);
        jm.getXmlString();
    }

}
