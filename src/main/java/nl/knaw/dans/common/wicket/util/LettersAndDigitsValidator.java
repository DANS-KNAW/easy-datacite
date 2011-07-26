package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.validation.validator.PatternValidator;

public class LettersAndDigitsValidator extends PatternValidator
{
    
    public static final String PATTERN = "([A-Za-z0-9]+)";
    
    private static final long serialVersionUID = -3770482697068448009L;
    
    private static LettersAndDigitsValidator instance;
    
    public static LettersAndDigitsValidator instance()
    {
        if (instance == null)
        {
            instance = new LettersAndDigitsValidator();
        }
        return instance;
    }

    private LettersAndDigitsValidator()
    {
        super(PATTERN);
    }

    

}
