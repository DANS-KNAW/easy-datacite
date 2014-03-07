package nl.knaw.dans.easy.domain.dataset;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.xml.AbstractXMLBeanValidator;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;

public class AdministrativeMetadataValidator extends AbstractXMLBeanValidator<AdministrativeMetadata>
{

    /**
     * The version token for version {@value} .
     */
    public static final String VERSION_0_1 = "0.1";

    public static final String SCHEMA_FOLDER = "xsd-files";

    public static final String SCHEMA_FILENAME = "amd.xsd";

    private static final AdministrativeMetadataValidator instance = new AdministrativeMetadataValidator();

    // singleton
    private AdministrativeMetadataValidator()
    {

    }

    public static AdministrativeMetadataValidator instance()
    {
        return instance;
    }

    @Override
    public URL getSchemaURL(String version) throws SchemaCreationException
    {
        final String name = SCHEMA_FOLDER + File.separator + version + File.separator + SCHEMA_FILENAME;
        final URL url = this.getClass().getResource(name);
        return url;
    }
}
