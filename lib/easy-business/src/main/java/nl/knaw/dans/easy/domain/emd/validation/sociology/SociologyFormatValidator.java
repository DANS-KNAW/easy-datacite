package nl.knaw.dans.easy.domain.emd.validation.sociology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.base.CommonValidators;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

public class SociologyFormatValidator implements Validator
{

    private static SociologyFormatValidator INSTANCE;

    private static List<Validator> VALIDATORS = Collections.synchronizedList(new ArrayList<Validator>());

    private SociologyFormatValidator()
    {
        VALIDATORS.addAll(CommonValidators.getList());
    }

    public static SociologyFormatValidator instance()
    {
        synchronized (VALIDATORS)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new SociologyFormatValidator();
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
