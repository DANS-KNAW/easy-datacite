package nl.knaw.dans.c.dmo.collections.xml;

import java.net.URL;

import nl.knaw.dans.common.lang.xml.AbstractXMLBeanValidator;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;

public class CollectionTreeValidator extends AbstractXMLBeanValidator<JiBXCollection> {

    public static final String SCHEMA_FILENAME = "xsd-files/dmo-collection.xsd";

    private static final CollectionTreeValidator instance = new CollectionTreeValidator();

    private CollectionTreeValidator() {
        // singleton
    }

    public static CollectionTreeValidator instance() {
        return instance;
    }

    @Override
    public URL getSchemaURL(String version) throws SchemaCreationException {
        final URL url = this.getClass().getResource(SCHEMA_FILENAME);
        return url;
    }

}
