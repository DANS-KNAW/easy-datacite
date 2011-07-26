package nl.knaw.dans.easy.web.wicket;

import org.apache.wicket.markup.html.panel.Panel;

public abstract class SwitchPanel extends Panel
{
    
    public static final String SWITCH_PANEL_WI = "switchPanel";
    
    private static final long serialVersionUID = 3543009697100900852L;
    
    private boolean editMode;

    public SwitchPanel(String wicketId)
    {
        super(wicketId);
    }
    
    public SwitchPanel(String wicketId, boolean inEditMode)
    {
        super(wicketId);
        editMode = inEditMode;
        setContentPanel();
    }
    
    public void switchMode()
    {
        editMode = !editMode;
        setContentPanel();
    }
    
    public abstract Panel getEditPanel();
    
    public abstract Panel getDisplayPanel();
    
    private void setContentPanel()
    {
        if (editMode)
        {
            addOrReplace(getEditPanel());
        }
        else
        {
            addOrReplace(getDisplayPanel());
        }
    }

}
