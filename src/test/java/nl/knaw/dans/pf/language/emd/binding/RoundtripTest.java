package nl.knaw.dans.pf.language.emd.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.EmdContributor;
import nl.knaw.dans.pf.language.emd.EmdCoverage;
import nl.knaw.dans.pf.language.emd.EmdCreator;
import nl.knaw.dans.pf.language.emd.EmdDate;
import nl.knaw.dans.pf.language.emd.EmdDescription;
import nl.knaw.dans.pf.language.emd.EmdFormat;
import nl.knaw.dans.pf.language.emd.EmdHelper;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.EmdLanguage;
import nl.knaw.dans.pf.language.emd.EmdOther;
import nl.knaw.dans.pf.language.emd.EmdPublisher;
import nl.knaw.dans.pf.language.emd.EmdRelation;
import nl.knaw.dans.pf.language.emd.EmdRights;
import nl.knaw.dans.pf.language.emd.EmdSource;
import nl.knaw.dans.pf.language.emd.EmdSubject;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.EmdType;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;

import org.jibx.runtime.impl.StAXReaderFactory;
import org.jibx.runtime.impl.XMLPullReaderFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserFactory;

public class RoundtripTest
{
    private static final Logger logger = LoggerFactory.getLogger(RoundtripTest.class);
    
    boolean verbose = false;
    
    @Test
    public void printParsers() throws Exception
    {
        if (verbose)
        {
            System.err.println(XmlPullParserFactory.newInstance());
            System.err.println(XMLPullReaderFactory.getInstance());
            System.err.println(StAXReaderFactory.getInstance());
        }
    }
    
    @Test
    public void easyMetadata() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(2, emd);
        
        String xmlString = new EmdMarshaller(emd).getXmlString();
        
        EmdUnmarshaller<EasyMetadata> um = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class);
        EasyMetadata emd2 = um.unmarshal(xmlString);
        String xmlString2 = new EmdMarshaller(emd2).getXmlString();
        
        if (verbose)
            logger.debug(xmlString2);
        
        assertEquals(xmlString, xmlString2);
        assertTrue(EMDValidator.instance().validate(xmlString, null).passed());
        assertTrue(EMDValidator.instance().validate(xmlString2, null).passed());
    }
    
    @Test
    public void easyMetadataEmpty() throws Exception
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        String xmlString = new EmdMarshaller(emd).getXmlString();
        
        EmdUnmarshaller<EasyMetadata> um = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class);
        EasyMetadata emd2 = um.unmarshal(xmlString);
        String xmlString2 = new EmdMarshaller(emd2).getXmlString();
        
        if (verbose)
            logger.debug(xmlString2);
        
        assertEquals(xmlString, xmlString2);
        assertTrue(EMDValidator.instance().validate(xmlString, null).passed());
        assertTrue(EMDValidator.instance().validate(xmlString2, null).passed());
    }
    
    @Test
    public void emdTitle() throws Exception
    {
        EmdTitle bean = new EmdTitle();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdTitle> um = new EmdUnmarshaller<EmdTitle>(EmdTitle.class);
        EmdTitle returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdTitleEmpty() throws Exception
    {
        EmdTitle bean = new EmdTitle();
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdTitle> um = new EmdUnmarshaller<EmdTitle>(EmdTitle.class);
        EmdTitle returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdCreator() throws Exception
    {
        EmdCreator bean = new EmdCreator();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdCreator> um = new EmdUnmarshaller<EmdCreator>(EmdCreator.class);
        EmdCreator returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdCreatorEmpty() throws Exception
    {
        EmdCreator bean = new EmdCreator();
        
        EmdMarshaller em = new EmdMarshaller(bean);
        em.setOmitXmlDeclaration(true);
        String xml = em.getXmlString();
        
        EmdUnmarshaller<EmdCreator> um = new EmdUnmarshaller<EmdCreator>(EmdCreator.class);
        EmdCreator returned = um.unmarshal(xml);
        EmdMarshaller em2 = new EmdMarshaller(returned);
        em2.setOmitXmlDeclaration(true);
        String returnedXml = em2.getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdSubject() throws Exception
    {
        EmdSubject bean = new EmdSubject();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdSubject> um = new EmdUnmarshaller<EmdSubject>(EmdSubject.class);
        EmdSubject returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdDescription() throws Exception
    {
        EmdDescription bean = new EmdDescription();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdDescription> um = new EmdUnmarshaller<EmdDescription>(EmdDescription.class);
        EmdDescription returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdPublisher() throws Exception
    {
        EmdPublisher bean = new EmdPublisher();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdPublisher> um = new EmdUnmarshaller<EmdPublisher>(EmdPublisher.class);
        EmdPublisher returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdContributor() throws Exception
    {
        EmdContributor bean = new EmdContributor();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdContributor> um = new EmdUnmarshaller<EmdContributor>(EmdContributor.class);
        EmdContributor returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdDate() throws Exception
    {
        EmdDate bean = new EmdDate();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdDate> um = new EmdUnmarshaller<EmdDate>(EmdDate.class);
        EmdDate returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdType() throws Exception
    {
        EmdType bean = new EmdType();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdType> um = new EmdUnmarshaller<EmdType>(EmdType.class);
        EmdType returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdFormat() throws Exception
    {
        EmdFormat bean = new EmdFormat();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdFormat> um = new EmdUnmarshaller<EmdFormat>(EmdFormat.class);
        EmdFormat returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdIdentifier() throws Exception
    {
        EmdIdentifier bean = new EmdIdentifier();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdIdentifier> um = new EmdUnmarshaller<EmdIdentifier>(EmdIdentifier.class);
        EmdIdentifier returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }

    @Test
    public void emdSource() throws Exception
    {
        EmdSource bean = new EmdSource();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdSource> um = new EmdUnmarshaller<EmdSource>(EmdSource.class);
        EmdSource returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdLanguage() throws Exception
    {
        EmdLanguage bean = new EmdLanguage();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdLanguage> um = new EmdUnmarshaller<EmdLanguage>(EmdLanguage.class);
        EmdLanguage returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdRelation() throws Exception
    {
        EmdRelation bean = new EmdRelation();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdRelation> um = new EmdUnmarshaller<EmdRelation>(EmdRelation.class);
        EmdRelation returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdCoverage() throws Exception
    {
        EmdCoverage bean = new EmdCoverage();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdCoverage> um = new EmdUnmarshaller<EmdCoverage>(EmdCoverage.class);
        EmdCoverage returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdRights() throws Exception
    {
        EmdRights bean = new EmdRights();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdRights> um = new EmdUnmarshaller<EmdRights>(EmdRights.class);
        EmdRights returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdAudience() throws Exception
    {
        EmdAudience bean = new EmdAudience();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdAudience> um = new EmdUnmarshaller<EmdAudience>(EmdAudience.class);
        EmdAudience returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    
    @Test
    public void emdOther() throws Exception
    {
        EmdOther bean = new EmdOther();
        EmdHelper.populate(2, bean);
        
        String xml = new EmdMarshaller(bean).getXmlString();
        
        EmdUnmarshaller<EmdOther> um = new EmdUnmarshaller<EmdOther>(EmdOther.class);
        EmdOther returned = um.unmarshal(xml);
        String returnedXml = new EmdMarshaller(returned).getXmlString();
        
        if (verbose)
            logger.debug(returnedXml);
        
        assertEquals(xml, returnedXml);
    }
    

}
