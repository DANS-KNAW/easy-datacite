package nl.knaw.dans.easy.web.wicket;

import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class BootstrapRadioListPanel<T> extends BootstrapChoiceListPanel<T> {
    private static final long serialVersionUID = 1L;

    final private ChoiceRenderer<T> renderer;

    public BootstrapRadioListPanel(String id, IModel<T> model, List<T> choices, ChoiceRenderer<T> choiceRenderer) {
        super(id, model, choices);
        renderer = choiceRenderer;
    }

    @Override
    protected void init() {
        RadioGroup radioList = new RadioGroup("choiceList", getDefaultModel());
        ListView<T> radioButtons = new ListView<T>("choices", choices) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<T> choice) {
                final Radio<T> radioButton = createRadioButton(choice);
                final Label radioText = createChoiceText(radioButton);
                final FormComponentLabel containerLabel = createContainerLabel(radioButton);
                containerLabel.add(radioButton);
                containerLabel.add(radioText);
                choice.add(containerLabel);
                if (isInline()) {
                    choice.add(new SimpleAttributeModifier("class", "radio-inline"));
                } else {
                    choice.add(new SimpleAttributeModifier("class", "radio"));
                }
            }
        };
        radioButtons.setReuseItems(true);
        radioList.add(radioButtons);
        add(radioList);
    }

    private Radio<T> createRadioButton(ListItem<T> choice) {
        final String choiceText = (String) renderer.getDisplayValue(choice.getModelObject());
        Radio<T> radioButton = new Radio<T>("choice", choice.getModel());
        radioButton.setOutputMarkupId(true);
        radioButton.setLabel(new Model<String>(choiceText));
        radioButton.setEnabled(isEnabledInHierarchy());
        return radioButton;
    }
}
