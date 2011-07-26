package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper;

import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class TextAreaPanel<T extends Object> extends AbstractRepeaterPanel<T>
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
    public TextAreaPanel(String wicketId, IModel model)
    {
        super(wicketId, model);
    }

    public TextAreaPanel(String wicketId, ListWrapper<T> listWrapper)
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
            if (getListWrapper() instanceof IdentifierListWrapper)
	    	{
	    		final TextArea textArea = new TextArea("textArea", new PropertyModel(item.getDefaultModel(), "value"));
	    		add(textArea);
	    	} 
	    	else
	    	{
	    		final TextArea textArea = new TextArea("textArea", item.getDefaultModel());
	    		add(textArea);
	    	}
        }
        
    }
    
    class RepeatingViewModePanel extends Panel
    {
        
        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            String key = "";
    		if (getListWrapper() instanceof IdentifierListWrapper)
        	{
        		key = "value";
        	}
    		MultiLineLabel label = new MultiLineLabel("noneditable", new PropertyModel(item.getDefaultModel(), key));
            add(label);
        }
        
    }

}
