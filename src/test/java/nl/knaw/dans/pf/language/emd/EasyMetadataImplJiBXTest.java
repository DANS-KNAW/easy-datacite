package nl.knaw.dans.pf.language.emd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.EasyMetadataValidator;
import nl.knaw.dans.pf.language.emd.EmdOther;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.util.PropertyList;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class EasyMetadataImplJiBXTest
{

    private static final Logger logger = LoggerFactory.getLogger(EasyMetadataImplJiBXTest.class);
    @SuppressWarnings("unused")
    private boolean verbose = true;

    @Test
    public void testEtc() throws XMLException, SAXException, SchemaCreationException
    {
        EasyMetadata emd = new EasyMetadataImpl();
        EmdOther emdOther = emd.getEmdOther();

        List<PropertyList> etc = emdOther.getPropertyListCollection();
        PropertyList propList = new PropertyList();
        etc.add(propList);
        propList.setComment("Conversion from EasyI-matadata to EasyII-metadata");
        propList.addProperty("date", new DateTime().toString());
        propList.addProperty("aipId", "abcde12345");

        logger.debug("\n" + emd.asXMLString(4));

        assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
    }

    @Test
    public void testIdentifier() throws Exception
    {
        EasyMetadata emd = new EasyMetadataImpl();
        BasicIdentifier bi = new BasicIdentifier("123");
        bi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
        bi.setIdentificationSystem(URI.create("http://foo.com"));
        emd.getEmdIdentifier().add(bi);

        //System.out.println(emd.asXMLString(4));
        byte[] bytes = emd.asObjectXML();

        EasyMetadata emd2 = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, bytes);
        //System.out.println(emd2.asXMLString(4));
        BasicIdentifier bi2 = emd2.getEmdIdentifier().getIdentifier(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
        assertEquals("123", bi2.getValue());
        assertEquals(URI.create("http://foo.com"), bi2.getIdentificationSystem());
    }

    @Test
    public void testSerialization() throws Exception
    {
        BasicIdentifier bi = new BasicIdentifier("123");
        bi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
        bi.setIdentificationSystem(URI.create("http://foo.com"));

        serialize(bi, "target/basicIdentifier.so");

        BasicIdentifier bi2 = (BasicIdentifier) deserialize("target/basicIdentifier.so");
        assertEquals(URI.create("http://foo.com"), bi2.getIdentificationSystem());

    }

    private void serialize(Serializable so, String filename) throws IOException
    {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        fos = new FileOutputStream(filename);
        out = new ObjectOutputStream(fos);
        out.writeObject(so);
        out.close();
    }

    private Object deserialize(String filename) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fis);
        Object so = in.readObject();
        in.close();
        return so;
    }

}
