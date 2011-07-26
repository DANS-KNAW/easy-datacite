package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.List;

public class FormPage extends AbstractInheritableDefinition<FormPage>
{

    private static final long serialVersionUID = 6601889061998952330L;
    // keep modifier protected, JiBX needs it.
    protected boolean editable = true;

    private String cssContainerClassName;
	private List<String>      panelIds         = new ArrayList<String>();
	private List<PanelDefinition> panelDefinitions;

    protected FormPage()
    {
        super();
    }

    public FormPage(String id)
    {
        super(id);
    }

    public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
    public String getCssContainerClassName()
    {
        return cssContainerClassName;
    }

    public void setCssContainerClassName(String cssContainerClassName)
    {
        this.cssContainerClassName = cssContainerClassName;
    }

    public List<String> getPanelIds()
    {
        return panelIds;
    }

    /**
     * Get the PanelDefinitions for this FormPage with their parent set to this FormPage.
     * 
     * @return PanelDefinitions for this FormPage
     */
    public List<PanelDefinition> getPanelDefinitions()
    {
        if (panelDefinitions == null)
        {
            panelDefinitions = new ArrayList<PanelDefinition>();
            for (String panelId : panelIds)
            {
                PanelDefinition clone = (PanelDefinition) getPanelDefinition(panelId).clone();
                clone.setParent(this);
                panelDefinitions.add(clone);
            }
        }
        return panelDefinitions;
    }
    
    public List<TermPanelDefinition> getTermPanelDefinitions()
    {
        List<TermPanelDefinition> list = new ArrayList<TermPanelDefinition>();
        for (PanelDefinition pDef : getPanelDefinitions())
        {
            if (pDef instanceof TermPanelDefinition)
            {
                list.add((TermPanelDefinition) pDef);
            }
        }
        return list;
    }
    
    public boolean hasErrors()
    {
        boolean hasErrors = false;
        for (PanelDefinition pDef : getPanelDefinitions())
        {
            if (pDef.hasErrors())
            {
                hasErrors = true;
                break;
            }
        }
        return hasErrors;
    }
    
    public void clearErrorMessages()
    {
        for (PanelDefinition pDef : getPanelDefinitions())
        {
            pDef.clearErrorMessages();
        }
    }

    protected synchronized FormPage clone()
    {
        FormPage clone = new FormPage(getId());
        super.clone(clone);
        clone.panelIds.addAll(panelIds);
        clone.editable = editable;
        clone.cssContainerClassName = cssContainerClassName;
        return clone;
    }

}
