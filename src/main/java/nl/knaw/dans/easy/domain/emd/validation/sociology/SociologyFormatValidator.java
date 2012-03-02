package nl.knaw.dans.easy.domain.emd.validation.sociology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.ValidatorLists;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.ValidationReporter;
import nl.knaw.dans.easy.domain.model.emd.Validator;

public class SociologyFormatValidator implements Validator
{

    private static SociologyFormatValidator INSTANCE;

    private static List<Validator>          VALIDATORS = Collections.synchronizedList(new ArrayList<Validator>());

    private SociologyFormatValidator()
    {
        VALIDATORS.addAll(ValidatorLists.getCommonValidators());
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
