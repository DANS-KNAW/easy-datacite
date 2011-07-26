package nl.knaw.dans.easy.domain.form;


public class ChoiceListDefinition extends AbstractInheritableDefinition<ChoiceListDefinition>
{

    private static final long serialVersionUID = 767251261447410517L;
    
    private String schemeName;
    private boolean nullValid;
    
    protected ChoiceListDefinition()
    {
        super();
    }
    
    public ChoiceListDefinition(String listId)
    {
        super(listId);
    }

    public boolean isNullValid()
    {
        return nullValid;
    }

    public void setNullValid(boolean nullValid)
    {
        this.nullValid = nullValid;
    }
    
    public String getSchemeName()
    {
        return schemeName;
    }

    public void setSchemeName(String schemeName)
    {
        this.schemeName = schemeName;
    }

    protected synchronized ChoiceListDefinition clone()
    {
        ChoiceListDefinition clone = new ChoiceListDefinition(getId());
        super.clone(clone);
        clone.schemeName = schemeName;
        clone.nullValid = nullValid;
        return clone;
    }
}
