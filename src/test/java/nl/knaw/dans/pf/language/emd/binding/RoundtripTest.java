package nl.knaw.dans.pf.language.emd.binding;

import static org.junit.Assert.*;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.EmdHelper;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundtripTest
{
    private static final Logger logger = LoggerFactory.getLogger(RoundtripTest.class);
    
    boolean verbose = true;
    
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
    }

}
