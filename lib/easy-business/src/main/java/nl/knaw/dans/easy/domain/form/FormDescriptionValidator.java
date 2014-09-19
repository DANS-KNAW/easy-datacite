package nl.knaw.dans.easy.domain.form;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.xml.AbstractXMLBeanValidator;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;

/**
 * Utility class for validating {@link DepositDiscipline}s and their xml-representation(s).
 * 
 * @author ecco Apr 8, 2009
 */
public class FormDescriptionValidator extends AbstractXMLBeanValidator<FormDescriptor> {

    /**
     * The version token for version {@value} .
     */
    public static final String VERSION_0_1 = "0.1";

    public static final String SCHEMA_FOLDER = "xsd-files";

    public static final String SCHEMA_FILENAME = "form.xsd";

    private static final FormDescriptionValidator instance = new FormDescriptionValidator();

    private static final Logger logger = LoggerFactory.getLogger(FormDescriptionValidator.class);

    // singleton
    private FormDescriptionValidator() {

    }

    public static FormDescriptionValidator instance() {
        return instance;
    }

    @Override
    public URL getSchemaURL(final String version) {
        final String name = SCHEMA_FOLDER + File.separator + version + File.separator + SCHEMA_FILENAME;
        final URL url = this.getClass().getResource(name);
        if (url == null) {
            logger.warn("No schema at " + name);
        }
        return url;
    }

}
