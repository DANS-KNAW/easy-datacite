package nl.knaw.dans.easy.domain.emd.validation.other;

import nl.knaw.dans.easy.domain.emd.validation.base.ChoiceListValidator;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OtherFormatValidator implements Validator {

    private static OtherFormatValidator INSTANCE;

    private static List<Validator> VALIDATORS = Collections.synchronizedList(new ArrayList<Validator>());

    private OtherFormatValidator() {
        VALIDATORS.add(new ChoiceListValidator.RightsValidator(EmdScheme.COMMON_DCTERMS_ACCESSRIGHTS.getId()));
    }

    public static OtherFormatValidator instance() {
        synchronized (VALIDATORS) {
            if (INSTANCE == null) {
                INSTANCE = new OtherFormatValidator();
            }
        }
        return INSTANCE;
    }

    @Override
    public void validate(EasyMetadata emd, ValidationReporter reporter) {
        for (Validator validator : VALIDATORS) {
            validator.validate(emd, reporter);
        }
    }

}
