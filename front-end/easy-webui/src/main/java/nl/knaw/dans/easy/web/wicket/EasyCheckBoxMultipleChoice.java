package nl.knaw.dans.easy.web.wicket;

import java.util.List;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

@SuppressWarnings("unchecked")
public class EasyCheckBoxMultipleChoice extends CheckBoxMultipleChoice {

    private static final long serialVersionUID = 1082010316284147225L;

    public EasyCheckBoxMultipleChoice(String id) {
        super(id);
    }

    public EasyCheckBoxMultipleChoice(String id, List choices) {
        super(id, choices);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel choices) {
        super(id, choices);
    }

    public EasyCheckBoxMultipleChoice(String id, List choices, IChoiceRenderer renderer) {
        super(id, choices, renderer);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel model, List choices) {
        super(id, model, choices);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel model, IModel choices) {
        super(id, model, choices);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel choices, IChoiceRenderer renderer) {
        super(id, choices, renderer);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel model, List choices, IChoiceRenderer renderer) {
        super(id, model, choices, renderer);
    }

    public EasyCheckBoxMultipleChoice(String id, IModel model, IModel choices, IChoiceRenderer renderer) {
        super(id, model, choices, renderer);
    }

    @Override
    protected void onBeforeRender() {
        // We need to set the enabled-look of the children of this group.
        // Purely cosmetic, if not Action.ENABLE is authorized, the user will not be able to update
        // the model of this SecureCheckBoxMultipleChoice.
        // We have to incorporate enabled state previously set on the component, to prevent overriding
        // this setting.
        setEnabled(isEnableAllowed() && isEnabled());
        super.onBeforeRender();
    }

}
