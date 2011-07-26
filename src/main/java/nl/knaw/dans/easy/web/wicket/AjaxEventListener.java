package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface AjaxEventListener extends Serializable
{
    
    void handleAjaxEvent(AjaxRequestTarget target);

}
