package nl.knaw.dans.pf.language.ddm.api;

import java.net.MalformedURLException;
import java.net.URL;

import nl.knaw.dans.pf.language.xml.validation.AbstractValidator;

/**
 * Utility class for validating Dans Dataset Metadata.
 * 
 * @author ecco
 */
public final class DDMValidator extends AbstractValidator
{

    /**
     * The version token for version {@value} .
     */
    public static final String VERSION_0_1 = "0.1";

    public static final String SCHEMA_LOCATION = "http://easy.dans.knaw.nl/schemas/md/2012/11/ddm.xsd";

    private static final DDMValidator instance = new DDMValidator();

    private String schemaLocation;

    // singleton
    private DDMValidator()
    {

    }

    public static DDMValidator instance()
    {
        return instance;
    }

    /**
     * Parameter <code>version</code> is silently ignored.
     */
    @Override
    public URL getSchemaURL(final String version)
    {
        try
        {
            return new URL(getSchemaLocation());
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
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
