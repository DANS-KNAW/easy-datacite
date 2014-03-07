package nl.knaw.dans.common.lang.xml;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DublinCoreMetadataValidatorOnlineTest
{

    @Test
    public void test() throws SchemaCreationException
    {
        assertNotNull(DublinCoreMetadataValidator.instance().getSchemaURL("0.1"));
        assertNotNull(DublinCoreMetadataValidator.instance().getSchemaURL("bla"));
        assertNotNull(DublinCoreMetadataValidator.instance().getSchema("foo"));
    }

}
