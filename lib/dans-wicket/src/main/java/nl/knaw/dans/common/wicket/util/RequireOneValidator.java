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
public class RequireOneValidator extends AbstractRelatedFormFieldsValidator {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor with all formComponents that need to be validated.
     * 
     * @param components
     *        FormComponents at least two components of which at least one must be filled
     */
    public RequireOneValidator(final FormComponent... components) {
        super(components);
    }

    /**
     * Validate the form.
     * 
     * @param form
     *        The form to validate.
     * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
     */
    public void validate(Form form) {
        boolean anyNotEmpty = false;

        // Check content size for every component
        for (FormComponent component : this.dependentComponents) {
            if (component.getValue().trim().length() > 0) {
                anyNotEmpty = true;
                break;
            }
        }

        // If every component is empty
        if (!anyNotEmpty) {
            error(this.dependentComponents[0]);
        }
    }
}
