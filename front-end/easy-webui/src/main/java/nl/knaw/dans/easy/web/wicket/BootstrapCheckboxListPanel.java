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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class BootstrapCheckboxListPanel<T> extends BootstrapChoiceListPanel<T> {
    private static final long serialVersionUID = 1L;

    public BootstrapCheckboxListPanel(String id, IModel<T> model, List<T> choices) {
        super(id, model, choices);
    }

    @Override
    protected void init() {
        CheckGroup checkboxList = new CheckGroup("choiceList", getDefaultModel());
        ListView<T> checkboxes = new ListView<T>("choices", choices) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<T> choice) {
                final Check<T> checkbox = createCheckbox(choice);
                final Label checkboxText = createChoiceText(checkbox);
                final FormComponentLabel containerLabel = createContainerLabel(checkbox);
                containerLabel.add(checkbox);
                containerLabel.add(checkboxText);
                choice.add(containerLabel);
                if (isInline()) {
                    choice.add(new SimpleAttributeModifier("class", "checkbox-inline"));
                } else {
                    choice.add(new SimpleAttributeModifier("class", "checkbox"));
                }
            }
        };
        checkboxes.setReuseItems(true);
        checkboxList.add(checkboxes);
        add(checkboxList);
    }

    private Check<T> createCheckbox(ListItem<T> choice) {
        final String choiceText = choice.getDefaultModelObjectAsString();
        Check<T> checkbox = new Check<T>("choice", choice.getModel());
        checkbox.setOutputMarkupId(true);
        checkbox.setLabel(new Model<String>(choiceText));
        checkbox.setEnabled(isEnabledInHierarchy());
        return checkbox;
    }
}
