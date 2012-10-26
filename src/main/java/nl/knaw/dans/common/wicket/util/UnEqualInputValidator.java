package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.util.lang.Objects;

/**
 * Validates that the input of two form components is <b>not</b> identical. Errors are reported on the second
 * form component with key 'UnEqualInputValidator' and the variables:
 * <ul>
 * <li>${input(n)}: the user's input</li>
 * <li>${name}: the name of the component</li>
 * <li>${label(n)}: the label of the component - either comes from FormComponent.labelModel or
 * resource key [form-id].[form-component-id] in that order</li>
 * </ul>
 * @author ecco Feb 25, 2009
 *
 */
public class UnEqualInputValidator extends EqualInputValidator
{
    private static final long serialVersionUID = 5956319677142846398L;

    /**
     * Constructor.
     * @param formComponent1 one formComponent
     * @param formComponent2 another formComponent
     */
    public UnEqualInputValidator(FormComponent formComponent1, FormComponent formComponent2)
    {
        super(formComponent1, formComponent2);
    }

    @Override
    public void validate(Form form)
    {
        final FormComponent formComponent1 = getDependentFormComponents()[0];
        final FormComponent formComponent2 = getDependentFormComponents()[1];

        if (Objects.equal(formComponent1.getInput(), formComponent2.getInput()))
        {
            error(formComponent2);
        }
    }

}
