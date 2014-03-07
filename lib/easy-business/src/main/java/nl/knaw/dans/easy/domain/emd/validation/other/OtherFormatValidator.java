package nl.knaw.dans.easy.domain.emd.validation.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.base.CommonValidators;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

public class OtherFormatValidator implements Validator
{

    private static OtherFormatValidator INSTANCE;

    private static List<Validator> VALIDATORS = Collections.synchronizedList(new ArrayList<Validator>());

    private OtherFormatValidator()
    {
        VALIDATORS.addAll(CommonValidators.getList());
    }

    public static OtherFormatValidator instance()
    {
        synchronized (VALIDATORS)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new OtherFormatValidator();
            }
        }
        return INSTANCE;
    }

    @Override
    public void validate(EasyMetadata emd, ValidationReporter reporter)
    {
        for (Validator validator : VALIDATORS)
        {
            validator.validate(emd, reporter);
        }
    }

}
