package nl.knaw.dans.easy.domain.form.validation;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.easy.domain.emd.validation.archaeology.EasSpatialValidator;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;

public class EmdValidatorFactory {

    private static Map<String, Validator> VALIDATOR_MAP;

    public static Validator getValidator(String className) {
        return getValidatorMap().get(className);
    }

    private static Map<String, Validator> getValidatorMap() {
        if (VALIDATOR_MAP == null) {
            VALIDATOR_MAP = new HashMap<String, Validator>();
            VALIDATOR_MAP.put(EasSpatialValidator.class.getName(), new EasSpatialValidator());
        }
        return VALIDATOR_MAP;
    }

}
