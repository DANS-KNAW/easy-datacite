package nl.knaw.dans.easy.web.deposit.repeater;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class TextFieldPanel<T extends Object> extends AbstractRepeaterPanel<T>
{

    private static final long serialVersionUID = -8267060816678393222L;
    
    /**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     */
    public TextFieldPanel(String wicketId, IModel<ListWrapper<T>> model)
    {
        super(wicketId, model);
    }

    public TextFieldPanel(String wicketId, ListWrapper<T> listWrapper)
    {
        super(wicketId, listWrapper);
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
            final TextField textField = new TextField("textField", item.getDefaultModel());
            add(textField);
        }
        
    }
    
    class RepeatingViewModePanel extends Panel
    {
        
        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            Label label = new Label("noneditable", item.getDefaultModel());
            add(label);
        }
        
    }
}
