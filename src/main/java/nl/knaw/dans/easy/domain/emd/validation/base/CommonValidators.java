package nl.knaw.dans.easy.domain.emd.validation.base;

import java.util.ArrayList;
import java.util.List;

public class CommonValidators
{
    private static List<Validator> VALIDATORS;

    static
    {
        VALIDATORS = new ArrayList<Validator>();
        VALIDATORS.add(ChoiceListValidator.createRightsValidator("common.dcterms.accessrights"));
        VALIDATORS.add(ChoiceListValidator.createRelationsValidator("common.dcterms.relation"));
    }

    /**
     * @return validators for non-archaeology FormatValidators
     */
    public static List<Validator> getList()
    {
        return VALIDATORS;
    }
}
