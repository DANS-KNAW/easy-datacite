package nl.knaw.dans.easy.domain.emd.validation.archaeology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.ValidationReporter;
import nl.knaw.dans.easy.domain.model.emd.Validator;

public class ArchaeologyFormatValidator implements Validator
{
    
    private static ArchaeologyFormatValidator INSTANCE;
    
    private static List<Validator> VALIDATORS 
        = Collections.synchronizedList(new ArrayList<Validator>());
    
    private ArchaeologyFormatValidator()
    {
        VALIDATORS.add(new EasSpatialValidator());
        // TODO add more validators
    }
    
    public static ArchaeologyFormatValidator instance()
    {
        synchronized (VALIDATORS)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new ArchaeologyFormatValidator();
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
