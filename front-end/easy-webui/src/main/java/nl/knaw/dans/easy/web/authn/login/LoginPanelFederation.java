package nl.knaw.dans.easy.web.authn.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoginPanelFederation extends AbstractEasyStatelessPanel {
    private static Logger logger = LoggerFactory.getLogger(LoginPanelFederation.class);
    private static final long serialVersionUID = 1L;

    @SpringBean(name = "federativeUserService")
    private FederativeUserService federativeUserService;

    public LoginPanelFederation(final String wicketId) {
        super(wicketId);
        final ExternalLink federationLink = new ExternalLink("federationLink", constructLinkToFederationLogin(), "Login") {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean getStatelessHint() {
                return true;
            }

            /*
             * WORK-AROUND: Overriding, otherwise Wicket does not accept the wicket:message element in the body of the anchor element. Not clear why this is not
             * allowed.
             */
            @Override
            protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
                renderComponentTagBody(markupStream, openTag);
            }
        };
        add(federationLink);
        federationLink.setVisible(federativeUserService.isFederationLoginEnabled());
    }

    private String constructLinkToFederationLogin() {
        String linkURLString = "";
        try {
            final String returnURLString = getUrlForReturnPage();
            // add the easy return page url as parameter to
            // the Shibboleth url
            final String federationURLString = federativeUserService.getFederationUrl().toString() + "?target=";
            linkURLString = federationURLString + returnURLString;
            logger.debug("link URL: " + linkURLString);
        }
        catch (final UnsupportedEncodingException e) {
            logger.error("Could not construct Federation login link", e);
        }

        return linkURLString;
    }

    private String getUrlForReturnPage() throws UnsupportedEncodingException {
        final String urlForResultPage = RequestCycle.get().urlFor(FederativeAuthenticationResultPage.class, new PageParameters()).toString();
        String returnUrlString = RequestUtils.toAbsolutePath(urlForResultPage);
        logger.debug("Constructed return URL: {}", returnUrlString);
        final String percentEncodedReturnUrlString = URLEncoder.encode(returnUrlString, "UTF-8");
        logger.debug("Percent-encoded return URL with UTF-8 as charset: {}", percentEncodedReturnUrlString);
        return percentEncodedReturnUrlString;
    }
}
