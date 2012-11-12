package nl.knaw.dans.easy.domain.emd.validation.base;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator.RightsValidator;
import nl.knaw.dans.easy.domain.model.emd.types.EmdScheme;

public class CommonValidators
{
    private static List<Validator> VALIDATORS;

    static
    {
        VALIDATORS = new ArrayList<Validator>();
        VALIDATORS.add(new RightsValidator(EmdScheme.COMMON_DCTERMS_ACCESSRIGHTS.getId()));
        /* 
         * RelationsValidator is validating the wrong values:
         * message=The value 'title=Website NIROV/Nieuwe Kaart van Nederland URI=http://www.nieuwekaart.nl/' of /emd:easymetadata/emd:relation/dcterms:relation/ is not a valid key in the list 'common.dcterms.relation'
         * 
         * xpath=/emd:easymetadata/emd:relation/dcterms:relation/
         * sourceLink=nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator.validate (ChoiceListValidator.java:78)
         */
        //VALIDATORS.add(new RelationsValidator(EmdScheme.COMMON_DCTERMS_RELATION.getId()));
    }

    /**
     * @return validators for non-archaeology FormatValidators
     */
    public static List<Validator> getList()
    {
        return VALIDATORS;
    }
}
