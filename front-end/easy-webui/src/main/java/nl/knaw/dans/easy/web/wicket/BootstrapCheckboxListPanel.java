package nl.knaw.dans.easy.web.wicket;

import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class BootstrapCheckboxListPanel<T> extends Panel {
    private static final long serialVersionUID = 1L;

    private boolean inline = false;
    private List<T> choices;

    public BootstrapCheckboxListPanel(String id, IModel<EasyUser> model, List<T> choices) {
        super(id, model);
        this.choices = choices;

        init();
    }

    protected void init() {
        super.onInitialize();

        CheckGroup checkboxList = new CheckGroup("checkboxList", this.getDefaultModel());
        ListView<T> checkboxes = new ListView<T>("checkboxes", choices) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<T> choice) {
                final Check<T> checkbox = createCheckbox(choice);
                final Label checkboxText = createCheckboxText(checkbox);
                final FormComponentLabel containerLabel = createContainerLabel(checkbox);
                containerLabel.add(checkbox);
                containerLabel.add(checkboxText);
                choice.add(containerLabel);
                if (isInline()) { // Default is class="checkbox"
                    choice.add(new SimpleAttributeModifier("class", "checkbox-inline"));
                }
            }
        };
        checkboxes.setReuseItems(true);
        checkboxList.add(checkboxes);
        add(checkboxList);
    }

    private Check<T> createCheckbox(ListItem<T> choice) {
        final String choiceText = choice.getModelObject().toString();
        Check<T> checkbox = new Check<T>("checkBox", choice.getModel());
        checkbox.setOutputMarkupId(true);
        checkbox.setLabel(new Model<String>(choiceText));
        checkbox.setEnabled(isEnabledInHierarchy());
        return checkbox;
    }

    private Label createCheckboxText(Check<T> checkbox) {
        Label checkboxText = new Label("checkBoxText", checkbox.getLabel());
        checkboxText.setRenderBodyOnly(true);
        return checkboxText;
    }

    private FormComponentLabel createContainerLabel(Check<T> checkbox) {
        FormComponentLabel containerLabel = new FormComponentLabel("containerLabel", checkbox);
        return containerLabel;
    }

    public boolean isInline() {
        return inline;
    }

    public BootstrapCheckboxListPanel<T> setInline(boolean inline) {
        this.inline = inline;
        return this;
    }
}
