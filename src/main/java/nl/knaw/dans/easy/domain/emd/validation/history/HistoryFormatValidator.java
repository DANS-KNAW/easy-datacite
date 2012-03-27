package nl.knaw.dans.easy.domain.emd.validation.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.easy.domain.emd.validation.base.CommonValidators;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

public class HistoryFormatValidator implements Validator
{
    
    private static HistoryFormatValidator INSTANCE;
    
    private static List<Validator> VALIDATORS 
        = Collections.synchronizedList(new ArrayList<Validator>());
    
    private HistoryFormatValidator()
    {
        VALIDATORS.addAll(CommonValidators.getList());
    }
    
    public static HistoryFormatValidator instance()
    {
        synchronized (VALIDATORS)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new HistoryFormatValidator();
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
