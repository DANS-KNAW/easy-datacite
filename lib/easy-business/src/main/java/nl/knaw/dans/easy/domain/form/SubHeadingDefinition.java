package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.List;

public class SubHeadingDefinition extends PanelDefinition {

    private static final long serialVersionUID = -3542376584462566739L;

    // keep modifier protected, JiBX needs it.
    protected boolean editable = true;

    private List<String> panelIds = new ArrayList<String>();
    private List<PanelDefinition> panelDefinitions;

    protected SubHeadingDefinition() {
        super();
    }

    public SubHeadingDefinition(String panelId) {
        super(panelId);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<String> getPanelIds() {
        return panelIds;
    }

    public void setPanelIds(List<String> panelIds) {
        this.panelIds = panelIds;
    }

    /**
     * Get the PanelDefinitions for this SubHeadingDefinition with their parent set to this SubHeadingDefinition.
     * 
     * @return PanelDefinitions for this SubHeadingDefinition
     */
    public List<PanelDefinition> getPanelDefinitions() {
        if (panelDefinitions == null) {
            panelDefinitions = new ArrayList<PanelDefinition>();
            for (String panelId : panelIds) {
                PanelDefinition clone = (PanelDefinition) getPanelDefinition(panelId).clone();
                clone.setParent(this);
                panelDefinitions.add(clone);
            }
        }
        return panelDefinitions;
    }

    @Override
    public boolean hasErrors() {
        boolean hasErrors = false;
        for (PanelDefinition pDef : getPanelDefinitions()) {
            if (pDef.hasErrors()) {
                hasErrors = true;
                break;
            }
        }
        return hasErrors;
    }

    @Override
    public void clearErrorMessages() {
        for (PanelDefinition pDef : getPanelDefinitions()) {
            pDef.clearErrorMessages();
        }
    }

    protected synchronized SubHeadingDefinition clone() {
        SubHeadingDefinition clone = new SubHeadingDefinition(getId());
        super.clone(clone);
        clone.panelIds.addAll(panelIds);
        clone.editable = editable;
        return clone;
    }

}
