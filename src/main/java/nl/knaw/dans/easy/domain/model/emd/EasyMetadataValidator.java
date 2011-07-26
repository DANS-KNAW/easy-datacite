package nl.knaw.dans.easy.domain.model.emd;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.xml.AbstractXMLBeanValidator;

/**
 * Utility class for validating easymetadata.
 *
 * @author ecco
 */
public final class EasyMetadataValidator extends AbstractXMLBeanValidator<EasyMetadata>
{

    /**
     * The version token for version {@value}.
     */
    public static final String               VERSION_0_1          = "0.1";
    
    public static final String SCHEMA_FOLDER = "xsd-files";

    public static final String SCHEMA_FILENAME = "easymetadata.xsd";
    
    private static final EasyMetadataValidator instance = new EasyMetadataValidator();

    // singleton
    private EasyMetadataValidator()
    {
        
    }
    
    public static EasyMetadataValidator instance()
    {
        return instance;
    }
    
    @Override
    public URL getSchemaURL(final String version)
    {
        final String name = SCHEMA_FOLDER + File.separator + version + File.separator + SCHEMA_FILENAME;
        final URL url = this.getClass().getResource(name);
        return url;
    }
    
}
