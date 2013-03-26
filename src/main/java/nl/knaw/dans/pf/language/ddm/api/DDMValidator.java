package nl.knaw.dans.pf.language.ddm.api;

import static nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace.*;
import nl.knaw.dans.pf.language.xml.validation.AbstractValidator2;

/**
 * Utility class for validating Dans Dataset Metadata.
 */
public final class DDMValidator extends AbstractValidator2
{
    private static final DDMValidator instance = new DDMValidator();

    // singleton
    private DDMValidator()
    {
        super(DDM.xsd,DCX_GML.xsd,NARCIS_TYPE.xsd,ABR.xsd);
    }

    public static DDMValidator instance()
    {
        return instance;
    }
}
