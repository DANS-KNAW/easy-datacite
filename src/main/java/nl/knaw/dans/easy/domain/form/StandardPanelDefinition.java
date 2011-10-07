package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.List;


public class StandardPanelDefinition extends PanelDefinition
{

    private static final long serialVersionUID = 6081545214101852051L;
    
    // keep modifier protected, JiBX needs it.
    protected String modelClass;
    // keep modifier protected, JiBX needs it.
    protected String panelClass;
    // keep modifier protected, JiBX needs it.
    protected boolean required;
    // keep modifier protected, JiBX needs it.
    protected boolean repeating;
    // keep modifier protected, JiBX needs it.
    protected String validatorClassName;
    
    protected List<ChoiceListDefinition> choiceListDefinitions = new ArrayList<ChoiceListDefinition>();
    
    protected StandardPanelDefinition()
    {
        super();
    }
    
    public StandardPanelDefinition(String id)
    {
        super(id);
    }
    
    public String getPanelClass()
    {
        return panelClass;
    }

    public void setPanelClass(String panelClass)
    {
        this.panelClass = panelClass;
    }

    public String getModelClass()
    {
        return modelClass;
    }

    public void setDefaultModelClass(String modelClass)
    {
        this.modelClass = modelClass;
    }

    /**
     * @return the required
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }
    
    public boolean isRepeating()
    {
        return repeating;
    }

    public void setRepeating(boolean repeating)
    {
        this.repeating = repeating;
    }

    public String getValidatorClassName()
    {
        return validatorClassName;
    }

    public List<ChoiceListDefinition> getChoiceListDefinitions()
    {
        return choiceListDefinitions;
    }

    public void addChoiceListDefinition(ChoiceListDefinition clDef)
    {
        clDef.setParent(this);
        choiceListDefinitions.add(clDef);
    }
    
    public ChoiceListDefinition getChoiceListDefinition(String listId)
    {
        ChoiceListDefinition clDef = null;
        for (ChoiceListDefinition def : choiceListDefinitions)
        {
            if (def.getId().equals(listId))
            {
                clDef = def;
                break;
            }
        }
        return clDef;
    }
    
    public boolean hasChoicelistDefinition()
    {
        return choiceListDefinitions.size() > 0;
    }

    protected StandardPanelDefinition clone()
    {
        StandardPanelDefinition clone = new StandardPanelDefinition(getId());
        clone(clone);
        return clone;
    }

    protected void clone(StandardPanelDefinition clone)
    {
        super.clone(clone);
        clone.modelClass = modelClass;
        clone.panelClass = panelClass;
        clone.required = required;
        clone.repeating = repeating;
        clone.validatorClassName = validatorClassName;
        
        for (ChoiceListDefinition clDef : choiceListDefinitions)
        {
            clone.addChoiceListDefinition(clDef.clone());
        }
    }

}
