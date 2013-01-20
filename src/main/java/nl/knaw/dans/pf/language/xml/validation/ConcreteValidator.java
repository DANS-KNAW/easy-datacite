package nl.knaw.dans.pf.language.xml.validation;

import java.net.MalformedURLException;
import java.net.URL;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;

public class ConcreteValidator extends AbstractValidator
{

    private final URL schemaURL;

    public ConcreteValidator(String schemaURLString) throws MalformedURLException
    {
        this(new URL(schemaURLString));
    }

    public ConcreteValidator(URL schemaURL)
    {
        this.schemaURL = schemaURL;
    }

    @Override
    public URL getSchemaURL(String version) throws SchemaCreationException
    {
        return schemaURL;
    }

}
