/**
 * 
 */
package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

/**
 * Validator to require at least one of the fields that is added to the validator.
 * 
 * @author Herman Suijs
 */
public class RequireOneValidator extends AbstractFormValidator
{

    /**
     * Serial version uid.
     */
    private static final long     serialVersionUID = 1L;

    /**
     * Components for this validator.
     */
    private final FormComponent[] dependentComponents;

    /**
     * Default constructor with all formComponents that need to be validated.
     * 
     * @param components FormComponents of which at least one must be filled.
     */
    public RequireOneValidator(final FormComponent... components)
    {
        // Check number of parameters
        if (components.length < 2)
        {
            throw new IllegalArgumentException("This validator needs at least 2 components, otherwise set required on the component.");
        }

        // Check if all parameters are components
        for (FormComponent component : components)
        {
            if (component == null)
            {
                throw new IllegalArgumentException("This validator needs all arguments to be non-null.");
            }
        }

        this.dependentComponents = components;
    }

    /**
     * Return all components that depend on this validator.
     * 
     * @return All components that depend.
     * @see org.apache.wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
     */
    public FormComponent[] getDependentFormComponents()
    {
        return this.dependentComponents;
    }

    /**
     * Validate the form.
     * 
     * @param form The form to validate.
     * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
     */
    public void validate(Form form)
    {
        boolean anyNotEmpty = false;

        // Check content size for every component
        for (FormComponent component : this.dependentComponents)
        {
            if (component.getValue().trim().length() > 0)
            {
                anyNotEmpty = true;
                break;
            }
        }

        // If every component is empty
        if (!anyNotEmpty)
        {
            error(this.dependentComponents[0]);
        }
    }

    /**
     * Show string representation.
     * 
     * @return string representation.
     */
    @Override
    public String toString()
    {
        StringBuilder componentsString = new StringBuilder();
        boolean first = true;
        for (FormComponent component : this.dependentComponents)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                componentsString.append(", ");
            }
            componentsString.append(component.toString());
        }

        return "[" + RequireOneValidator.class.getName() + " for components: " + componentsString.toString() + "]";
    }

}
