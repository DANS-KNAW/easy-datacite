/**
 * 
 */
package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

/**
 * Validator to require that exactly one of the fields is filled in.
 * 
 * @author Vesa Åkerman
 */
public class RequireExactlyOneValidator extends AbstractRelatedFormFieldsValidator {
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param components
     *        FormComponents of which just one must be filled.
     */
    public RequireExactlyOneValidator(final FormComponent... components) {
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
        int nrFilledFields = 0;

        for (FormComponent component : this.dependentComponents) {
            if (component.getValue().trim().length() > 0) {
                nrFilledFields++;
            }
        }

        if (nrFilledFields != 1) {
            error(this.dependentComponents[0]);
        }
    }
}
