package nl.knaw.dans.easy.web.wicketutil;

import nl.knaw.dans.common.lang.id.DAI;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

public class DAIValidator extends AbstractValidator<String>
{

    private static final long serialVersionUID = -6877474354580072911L;
    
    private static DAIValidator INSTANCE;
    
    public static DAIValidator instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new DAIValidator();
        }
        return INSTANCE;
    }
    
    private DAIValidator()
    {
        
    }

    @Override
    protected void onValidate(IValidatable<String> validatable)
    {
        final String dai = validatable.getValue();
        if (!DAI.isValid(dai))
        {
            error(validatable);
        }
    }
    
    @Override
    public void error(IValidatable<String> validatable)
    {
        final String dai = validatable.getValue();
        ValidationError error = new ValidationError();
        error.setMessage(DAI.explain(dai));
        validatable.error(error);
    }

}
