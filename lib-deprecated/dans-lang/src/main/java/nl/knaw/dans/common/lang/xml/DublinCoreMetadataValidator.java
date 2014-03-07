package nl.knaw.dans.common.lang.xml;

import java.net.URL;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;

public final class DublinCoreMetadataValidator extends AbstractXMLBeanValidator<DublinCoreMetadata>
{

    public static final String SCHEMA_LOCATION = "dc-xsd/oai_dc.xsd";

    private static DublinCoreMetadataValidator instance;

    private DublinCoreMetadataValidator()
    {

    }

    @Override
    public URL getSchemaURL(String version)
    {
        return this.getClass().getResource(SCHEMA_LOCATION);
    }

    public static DublinCoreMetadataValidator instance()
    {
        if (instance == null)
        {
            instance = new DublinCoreMetadataValidator();
        }
        return instance;
    }

}
