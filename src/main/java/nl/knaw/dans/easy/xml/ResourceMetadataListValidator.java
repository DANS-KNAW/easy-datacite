package nl.knaw.dans.easy.xml;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.xml.AbstractXMLBeanValidator;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;

public class ResourceMetadataListValidator extends AbstractXMLBeanValidator<ResourceMetadataList>
{
    
    public static final String SCHEMA_FOLDER = "xsd-files";

    public static final String SCHEMA_FILENAME = "resource-metadata-list.xsd";
    
    private static ResourceMetadataListValidator INSTANCE = new ResourceMetadataListValidator();
    
    // singleton
    private ResourceMetadataListValidator()
    {
        
    }
    
    public static ResourceMetadataListValidator instance()
    {
        return INSTANCE;
    }

    @Override
    public URL getSchemaURL(String version) throws SchemaCreationException
    {
        final String name = SCHEMA_FOLDER + File.separator + SCHEMA_FILENAME;
        final URL url = this.getClass().getResource(name);
        return url;
    }

}
