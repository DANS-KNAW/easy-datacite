package nl.knaw.dans.pf.language.emd;

import java.net.MalformedURLException;
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
    public static final String VERSION_0_1 = "0.1";

    public static final String SCHEMA_LOCATION = "http://easy.dans.knaw.nl/schemas/md/emd/2012/11/emd.xsd";

    private static final EasyMetadataValidator instance = new EasyMetadataValidator();
    
    private String schemaLocation;

    // singleton
    private EasyMetadataValidator()
    {

    }

    public static EasyMetadataValidator instance()
    {
        return instance;
    }

    /**
     * Parameter <code>version</code> is silently ignored.
     */
    @Override
    public URL getSchemaURL(final String version)
    {
        URL schemaURL;
        try
        {
            schemaURL = new URL(getSchemaLocation());
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
        return schemaURL;
    }

    public String getSchemaLocation()
    {
        if (schemaLocation == null)
        {
            schemaLocation = SCHEMA_LOCATION;
        }
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation)
    {
        this.schemaLocation = schemaLocation;
    }

}
