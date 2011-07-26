package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.web.deposit.repeasy.QualifiedModel;
import nl.knaw.dans.easy.web.deposit.repeasy.BasicDateListWrapper.BasicDateModel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class BasicDatePanel extends AbstractChoicePanel<BasicDateModel>
{

    private static final long serialVersionUID = -2169562395545469665L;
    
    private boolean                  dropdownVisible;
    private String                   defaultKey;
    
    public BasicDatePanel(final String wicketId, final IModel model, final ChoiceList choiceList)
    {
        super(wicketId, model, choiceList);
    }

    public void setDropdownVisible(boolean dropdownVisible) {
        this.dropdownVisible = dropdownVisible;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
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

        private static final long serialVersionUID = -4387702534243040428L;
        
        public RepeatingEditModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            
            final DropDownChoice dropDownChoice = new DropDownChoice("schemeChoice", new PropertyModel(item
                    .getModelObject(), "scheme"), getChoiceList().getChoices(), getRenderer());
            dropDownChoice.setNullValid(isNullValid());
            if (defaultKey != null && !defaultKey.equals(""))
            {
                dropDownChoice.setModelValue(new String[]{defaultKey});
            }
            dropDownChoice.setVisible(dropdownVisible);
            
            final TextField dateField = new TextField("valueField", new PropertyModel(item.getModelObject(),
                "value"));
            
            add(dateField);
            add(dropDownChoice);
            
        }
        
    }
    
    class RepeatingViewModePanel extends Panel
    {
        
        private static final long serialVersionUID = -1064600333931796440L;

        public RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            QualifiedModel qModel = (QualifiedModel) item.getDefaultModelObject();
            String qualifierKey = qModel.getQualifier();
            String qualifier = getChoiceList().getValue(qualifierKey);
            
            Label labelscheme = new Label("datescheme", qualifier);
            Label labelvalue = new Label("datevalue", new PropertyModel(item.getModel(),"value"));
            labelscheme.setVisible(StringUtils.isNotBlank(qualifierKey));
            add(labelscheme);
            add(labelvalue);
        }
        
    }

}
