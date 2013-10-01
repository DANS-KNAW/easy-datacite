package nl.knaw.dans.easy.domain.dataset.disciplinecollection;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadataImpl;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineMetadataImplTest
{

    private static final Logger logger = LoggerFactory.getLogger(DisciplineMetadataImplTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DisciplineMetadata dmd = new DisciplineMetadataImpl();

        DisciplineMetadata dmd2 = (DisciplineMetadata) JiBXObjectFactory.unmarshal(DisciplineMetadataImpl.class, dmd.asObjectXML());
        assertEquals(dmd.asXMLString(), dmd2.asXMLString());

        if (verbose)
            logger.debug("\n" + dmd.asXMLString(4) + "\n");
    }

    @Test
    public void serializeDeserializeFull() throws XMLException
    {
        DisciplineMetadataImpl dmd = new DisciplineMetadataImpl();

        dmd.setOrder(55);
        dmd.setOICode("BN4434");

        DisciplineMetadata dmd2 = (DisciplineMetadata) JiBXObjectFactory.unmarshal(DisciplineMetadataImpl.class, dmd.asObjectXML());
        assertEquals(dmd.asXMLString(), dmd2.asXMLString());

        // if (verbose)
        logger.debug("\n" + dmd2.asXMLString(4) + "\n");
    }

}
