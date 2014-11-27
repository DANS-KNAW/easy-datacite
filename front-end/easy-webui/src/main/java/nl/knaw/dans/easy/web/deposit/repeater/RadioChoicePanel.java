package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.wicket.BootstrapRadioListPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class RadioChoicePanel extends AbstractChoicePanel<ChoiceList> {

    private static final long serialVersionUID = -3621013693080590601L;

    /**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @param choices
     *        a list of choices
     */
    public RadioChoicePanel(final String wicketId, final IModel model, final ChoiceList choiceList) {
        super(wicketId, model, choiceList);
    }

    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item) {
        if (isInEditMode()) {
            return new RepeatingEditModePanel(item);
        } else {
            return new RepeatingViewModePanel(item);
        }
    }

    class RepeatingEditModePanel extends Panel {

        private static final long serialVersionUID = 1179492912236629562L;

        public RepeatingEditModePanel(final ListItem item) {
            super(REPEATING_PANEL_ID);
            BootstrapRadioListPanel<KeyValuePair> rc = new BootstrapRadioListPanel<KeyValuePair>("radioList", (IModel<KeyValuePair>) item.getDefaultModel(),
                    getChoiceList().getChoices(), getRenderer());
            if (((KeyValuePair) item.getDefaultModelObject()).getKey() == null) {
                throw new IllegalArgumentException("The item model object should contain a key value pair for the selected radio button.");
                // assign default value
                // rc.setDefaultModelObject(getChoiceList().getChoices().get(0));
            }
            add(rc);
        }

    }

    class RepeatingViewModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item) {
            super(REPEATING_PANEL_ID);
            KeyValuePair kvp = (KeyValuePair) item.getDefaultModelObject();
            Label label = new Label("noneditable", getChoiceList().getValue(kvp.getKey()));
            add(label);
        }

    }
}
