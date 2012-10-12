package nl.knaw.dans.easy.web.authn;

import org.apache.wicket.markup.html.basic.Label;

import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

public class FederationUserInfoPanel extends AbstractEasyStatelessPanel
{
    public FederationUserInfoPanel(String wicketId, ApplicationUser appUser)
    {
        super(wicketId);
        add(new Label("institution", appUser.getOrganization()));
        add(new Label("username", appUser.getUserId()));
    }
}
