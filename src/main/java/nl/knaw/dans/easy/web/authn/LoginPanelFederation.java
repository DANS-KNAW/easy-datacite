package nl.knaw.dans.easy.web.authn;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPanelFederation extends AbstractEasyStatelessPanel
{
    private static Logger logger = LoggerFactory.getLogger(LoginPanelFederation.class);

    public LoginPanelFederation(String wicketId)
    {
        super(wicketId);

        ExternalLink federationLink = new ExternalLink("federationLink", getFederationURLString(), "Login")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean getStatelessHint()
            {
                return true;
            }
        };
        add(federationLink);
        federationLink.setVisible(Services.getFederativeUserService().isFederationLoginEnabled());

    }

    // construct the link for the Federative Athentication
    private String getFederationURLString()
    {
        String linkURLString = "";

        try
        {
            // get URL for FederativeAuthenticationResultPage
            String relStr = RequestCycle.get().urlFor(FederativeAuthenticationResultPage.class, new PageParameters()).toString();
            String returnURLString = org.apache.wicket.protocol.http.RequestUtils.toAbsolutePath(relStr);
            logger.debug("return URL: " + returnURLString);
            returnURLString = URLEncoder.encode(returnURLString, "UTF-8");
            // add the easy return page url as parameter to
            // the Shibboleth url
            String federationURLString = Services.getFederativeUserService().getFederationUrl().toString() + "?target=";
            linkURLString = federationURLString + returnURLString;
            logger.debug("link URL: " + linkURLString);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Could not construct Federation login link", e);
        }

        return linkURLString;
    }

}
