package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.Map;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.web.deposit.repeasy.IsoDateListWrapper.IsoDateModel;
import nl.knaw.dans.easy.web.deposit.repeasy.QualifiedModel;
import nl.knaw.dans.easy.web.deposit.repeasy.SingleISODateWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.SingleISODateWrapper.DateModel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class IsoDatePanel extends AbstractChoicePanel<IsoDateModel> {

    private static final long serialVersionUID = -822413494904086019L;
    // private static Logger logger = LoggerFactory.getLogger(RelationPanel.class);

    private boolean dropdownVisible;
    private String defaultKey;

    /**
     * Constructor that takes a model with a ListWrapper&lt;RelationWrapper> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @param choices
     *        a list of choices
     */
    public IsoDatePanel(final String wicketId, final IModel model, final ChoiceList choiceList) {
        super(wicketId, model, choiceList);
    }

    public void setDropdownVisible(boolean dropdownVisible) {
        this.dropdownVisible = dropdownVisible;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
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

            final DropDownChoice dropDownChoice = new DropDownChoice("schemeChoice", new PropertyModel(item.getModelObject(), "scheme"), getChoiceList()
                    .getChoices(), getRenderer());
            dropDownChoice.setNullValid(isNullValid());
            if (defaultKey != null && !defaultKey.equals("")) {
                dropDownChoice.setModelValue(new String[] {defaultKey});
            }
            dropDownChoice.setVisible(dropdownVisible);

            final TextField dateField = new TextField("valueField", new PropertyModel(item.getModelObject(), "value"));

            DatePicker datePicker = new DatePicker() {
                /**
                 *
                 */
                private static final long serialVersionUID = 6435372550472817746L;

                @Override
                protected void configure(Map arg0) {
                    arg0.put("title", "DANS");
                    super.configure(arg0);
                }

                @Override
                protected String getDatePattern() {
                    // wicket 1.4 bug!
                    return "yyyy-MM-dd"; // or "dd/MM/yyyy". the stupid thing always sets a short year
                                         // format: yy.
                }
            };
            dateField.add(datePicker);
            add(dateField);
            add(dropDownChoice);
        }

    }

    class RepeatingViewModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        public RepeatingViewModePanel(final ListItem item) {
            super(REPEATING_PANEL_ID);
            QualifiedModel qModel = (QualifiedModel) item.getDefaultModelObject();
            String qualifierKey = qModel.getQualifier();
            String qualifier = getChoiceList().getValue(qualifierKey);

            Label labelscheme = new Label("datescheme", qualifier);
            Label labelvalue = new Label("datevalue", new PropertyModel(item.getModel(), "value"));
            labelscheme.setVisible(StringUtils.isNotBlank(qualifierKey));
            add(labelscheme);
            add(labelvalue);
        }

    }
}
