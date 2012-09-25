package nl.knaw.dans.easy.domain.emd.validation.base;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator.RelationsValidator;
import nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator.RightsValidator;
import nl.knaw.dans.easy.domain.model.emd.types.EmdScheme;

public class CommonValidators
{
    private static List<Validator> VALIDATORS;

    static
    {
        VALIDATORS = new ArrayList<Validator>();
        VALIDATORS.add(new RightsValidator(EmdScheme.COMMON_DCTERMS_ACCESSRIGHTS.getId()));
        VALIDATORS.add(new RelationsValidator(EmdScheme.COMMON_DCTERMS_RELATION.getId()));
        VALIDATORS.add(new EmbargoValidator());
    }

    /**
     * @return validators for non-archaeology FormatValidators
     */
    public static List<Validator> getList()
    {
        return VALIDATORS;
    }
}
