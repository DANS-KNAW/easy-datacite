package nl.knaw.dans.pf.language.emd.validation;

import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import nl.knaw.dans.l.xml.exc.SchemaCreationException;
import nl.knaw.dans.l.xml.exc.ValidatorException;
import nl.knaw.dans.l.xml.exc.XMLException;
import nl.knaw.dans.l.xml.exc.XMLSerializationException;
import nl.knaw.dans.l.xml.validation.AbstractValidator;
import nl.knaw.dans.l.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;

/**
 * Utility class for validating easymetadata.
 *
 * @author ecco
 */
public final class EasyMetadataValidator extends AbstractValidator
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
    
    public XMLErrorHandler validate(EasyMetadata emd) throws XMLException, SAXException
    {
        return validate(new EmdMarshaller(emd).getXmlString(), null);
    }

}
