package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.web.deposit.repeasy.PointListWrapper.PointModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class PointPanel extends AbstractChoicePanel<PointModel> {

    private static final long serialVersionUID = -822413494904086019L;

    /**
     * Constructor that takes a model with a ListWrapper&lt;PointModel> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;PointModel>>
     * @param choices
     *        a list of choices
     */
    public PointPanel(final String wicketId, final IModel model, final ChoiceList choiceList) {
        super(wicketId, model, choiceList);
    }

    public PointPanel(final String wicketId, final ListWrapper<PointModel> listWrapper, final ChoiceList choiceList) {
        super(wicketId, listWrapper, choiceList);
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

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingEditModePanel(final ListItem item) {
            super(REPEATING_PANEL_ID);
            // add(new FeedbackPanel(REPEATING_PANEL_ID + ".feedback"));
            final DropDownChoice schemeChoice = new DropDownChoice("schemeChoice", new PropertyModel(item.getDefaultModelObject(), "scheme"), getChoiceList()
                    .getChoices(), getRenderer());
            schemeChoice.setNullValid(isNullValid());

            final TextField xField = new TextField("xField", new PropertyModel(item.getDefaultModelObject(), "x"));

            final TextField yField = new TextField("yField", new PropertyModel(item.getDefaultModelObject(), "y"));

            add(schemeChoice);
            add(xField);
            add(yField);
        }

    }

    class RepeatingViewModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item) {
            super(REPEATING_PANEL_ID);
            String s = (String) new PropertyModel(item.getDefaultModel(), "schemeToken").getObject();
            String x = (String) new PropertyModel(item.getDefaultModel(), "x").getObject();
            String y = (String) new PropertyModel(item.getDefaultModel(), "y").getObject();
            Label sLabel = new Label("sLabel", getChoiceList().getValue(s));
            Label xLabel = new Label("xLabel", "X: " + String.valueOf(x));// Put "X" label in here not in
                                                                          // html, otherwise it will
                                                                          // displays if x is empty
            Label yLabel = new Label("yLabel", "Y: " + String.valueOf(y));// Put "Y" label in here not in
                                                                          // html, otherwise it will
                                                                          // displays if y is empty
            add(sLabel);
            add(xLabel.setVisible(x != null));
            add(yLabel.setVisible(y != null));
        }

    }
}
