package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.basic.Label;

public class FederationUserInfoPanel extends AbstractEasyStatelessPanel {
    private static final long serialVersionUID = -1235441463726557290L;

    public FederationUserInfoPanel(String wicketId, FederationUser user) {
        super(wicketId);
        add(new Label("userdescription", user.getUserDescription().equals("") ? "[No user data found]" : user.getUserDescription()));
        add(new Label("institutiondescription", user.getHomeOrg() == null ? "[No organization description found]" : user.getHomeOrg()));
    }
}
