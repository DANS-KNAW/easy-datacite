package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.basic.Label;

public class FederationUserInfoPanel extends AbstractEasyStatelessPanel
{
    private static final long serialVersionUID = -1235441463726557290L;

    public FederationUserInfoPanel(String wicketId, ApplicationUser appUser)
    {
        super(wicketId);
        add(new Label("institution", appUser.getOrganization()));
        add(new Label("username", appUser.getUserId()));
    }
}
