package nl.knaw.dans.easy.domain.emd.validation.base;

import java.util.ArrayList;
import java.util.List;
import static nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator.*;

public class CommonValidators
{
    private static List<Validator> VALIDATORS;

    static
    {
        VALIDATORS = new ArrayList<Validator>();
        VALIDATORS.add(new RightsValidator("common.dcterms.accessrights"));
        VALIDATORS.add(new RelationsValidator("common.dcterms.relation"));
    }

    /**
     * @return validators for non-archaeology FormatValidators
     */
    public static List<Validator> getList()
    {
        return VALIDATORS;
    }
}
