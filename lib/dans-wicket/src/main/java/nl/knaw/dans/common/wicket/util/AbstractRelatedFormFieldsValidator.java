package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

public abstract class AbstractRelatedFormFieldsValidator extends AbstractFormValidator {
    protected FormComponent[] dependentComponents;

    protected AbstractRelatedFormFieldsValidator(FormComponent... components) {
        this.dependentComponents = components;
        checkAtLeastTwoDependentComponents();
        checkNoNullDependentComponents();
    }

    private void checkAtLeastTwoDependentComponents() {
        if (dependentComponents.length < 2) {
            throw new IllegalArgumentException("This validator needs at least 2 components, otherwise set required on the component.");
        }
    }

    private void checkNoNullDependentComponents() {
        // Check if all parameters are components
        for (FormComponent component : dependentComponents) {
            if (component == null) {
                throw new IllegalArgumentException("This validator needs all arguments to be non-null.");
            }
        }

    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return dependentComponents;
    }

    @Override
    public String toString() {
        StringBuilder componentsString = new StringBuilder();
        boolean first = true;
        for (FormComponent component : this.dependentComponents) {
            if (first) {
                first = false;
            } else {
                componentsString.append(", ");
            }
            componentsString.append(component.toString());
        }

        return "[" + this.getClass().getName() + " for components: " + componentsString.toString() + "]";
    }
}
