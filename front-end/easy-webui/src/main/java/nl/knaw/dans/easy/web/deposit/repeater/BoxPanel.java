package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.web.deposit.repeasy.BoxListWrapper.BoxModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class BoxPanel extends AbstractChoicePanel<BoxModel>
{

    private static final long serialVersionUID = -822413494904086019L;

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
    public BoxPanel(final String wicketId, final IModel model, final ChoiceList choiceList)
    {
        super(wicketId, model, choiceList);
    }

    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item)
    {
        if (isInEditMode())
        {
            return new RepeatingEditModePanel(item);
        }
        else
        {
            return new RepeatingViewModePanel(item);
        }
    }

    class RepeatingEditModePanel extends Panel
    {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingEditModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            final DropDownChoice schemeChoice = new DropDownChoice("schemeChoice", new PropertyModel(item.getDefaultModelObject(), "scheme"), getChoiceList()
                    .getChoices(), getRenderer());
            schemeChoice.setNullValid(isNullValid());
            final TextField northField = new TextField("northField", new PropertyModel(item.getDefaultModelObject(), "north"));
            final TextField eastField = new TextField("eastField", new PropertyModel(item.getDefaultModelObject(), "east"));
            final TextField southField = new TextField("southField", new PropertyModel(item.getDefaultModelObject(), "south"));
            final TextField westField = new TextField("westField", new PropertyModel(item.getDefaultModelObject(), "west"));
            add(schemeChoice);
            add(northField);
            add(eastField);
            add(southField);
            add(westField);
        }

    }

    class RepeatingViewModePanel extends Panel
    {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            String s = (String) new PropertyModel(item.getDefaultModel(), "schemeToken").getObject();
            String northVal = (String) new PropertyModel(item.getDefaultModel(), "north").getObject();
            String eastVal = (String) new PropertyModel(item.getDefaultModel(), "east").getObject();
            String southVal = (String) new PropertyModel(item.getDefaultModel(), "south").getObject();
            String westVal = (String) new PropertyModel(item.getDefaultModel(), "west").getObject();
            Label sLabel = new Label("sLabel", getChoiceList().getValue(s));
            Label northLabel = new Label("northLabel", "North: " + northVal);// Put "North" label in here
                                                                             // not in html, otherwise it
                                                                             // will displays if x is
                                                                             // empty
            Label eastLabel = new Label("eastLabel", "East: " + eastVal);// Put "East" label in here not
                                                                         // in html, otherwise it will
                                                                         // displays if y is empty
            Label southLabel = new Label("southLabel", "South: " + southVal);// Put "South" label in here
                                                                             // not in html, otherwise it
                                                                             // will displays if x is
                                                                             // empty
            Label westLabel = new Label("westLabel", "West: " + westVal);// Put "West" label in here not
                                                                         // in html, otherwise it will
                                                                         // displays if y is empty

            add(sLabel);
            add(northLabel.setVisible(northVal != null));
            add(eastLabel.setVisible(eastVal != null));
            add(southLabel.setVisible(southVal != null));
            add(westLabel.setVisible(westVal != null));
        }

    }
}
