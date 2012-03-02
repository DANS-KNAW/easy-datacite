package nl.knaw.dans.easy.domain.emd.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.Validator;

public class ValidatorLists
{
    private static final String ARCHAEOLOGY = "archaeology";
    private static final String COMMON = "common";
    
    /** folders in src/main/resources/easy-business/dicipline/emd/choicelist */
    private static final String[] validatorTypes;
    
    
    private static final Map<String,List<Validator>> validatorLists;
    static {
        validatorTypes = new String[] {COMMON,ARCHAEOLOGY};
        validatorLists = new HashMap<String, List<Validator>>();
        for (String type :validatorTypes){
            validatorLists.put(type,new ArrayList<Validator>());
        }
        for (String type :validatorTypes){
            validatorLists.get(type).add(new ChoiceListValidator(type+".dcterms.accessrights","/emd:easymetadata/emd:rights/dcterms:accessRights/")
            {
                @Override
                public String getValidatedValue(EasyMetadata emd)
                {
                    return emd.getEmdRights().getValues().get(0);
                }
            });
        }
    }

    public static List<Validator> getCommonValidators(){
        return validatorLists.get(COMMON);
    }

    public static List<Validator> getArchaeologyValidators(){
        return validatorLists.get(ARCHAEOLOGY);
    }
}
