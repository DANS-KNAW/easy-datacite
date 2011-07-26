package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

public abstract class AbstractChoicePanel<T extends Object> extends AbstractRepeaterPanel<T>
{

    private static final long    serialVersionUID = -3621013693080590601L;

    private final ChoiceList 		choiceList;
    private final ChoiceRenderer 	renderer;
    private boolean              	nullValid = true;
    

	/**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @param choices2
     *        a list of choices
     */
	
	public AbstractChoicePanel(final String wicketId, final IModel model, final ChoiceList choiceList)
    {
        super(wicketId, model);
        this.choiceList = choiceList;
        renderer = getListWrapper().getChoiceRenderer();
    }

    public AbstractChoicePanel(final String wicketId, final ListWrapper<T> listWrapper, final ChoiceList choiceList)
    {
        super(wicketId, listWrapper);
        this.choiceList = choiceList;
        renderer = getListWrapper().getChoiceRenderer();
    }
    
    @Override
    public void setDefinition(StandardPanelDefinition definition)
    {
        super.setDefinition(definition);
        if (!definition.getChoiceListDefinitions().isEmpty())
        {
        	this.nullValid = definition.getChoiceListDefinitions().get(0).isNullValid();
        }
    }

    public boolean isNullValid()
    {
        return nullValid;
    }

    public void setNullValid(final boolean nullValid)
    {
        this.nullValid = nullValid;
    }

    public ChoiceList getChoiceList()
    {
        return choiceList;
    }
    
    public List<KeyValuePair> getChoices()
    {
        List<KeyValuePair> list = new ArrayList<KeyValuePair>(choiceList.getChoices());
        return list;
    }

    public ChoiceRenderer getRenderer()
    {
        return renderer;
    }
  
}
