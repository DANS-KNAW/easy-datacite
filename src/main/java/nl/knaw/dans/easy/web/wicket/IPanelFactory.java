package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;

import org.apache.wicket.markup.html.panel.Panel;

public interface IPanelFactory extends Serializable
{
    
    Panel createPanel(StandardPanelDefinition panelDefinition) throws PanelFactoryException;
    
    String getPanelWicketId();

}
