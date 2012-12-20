package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.behavior.IncludeResourceBehavior;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.authn.AbstractAuthenticationPage;
import nl.knaw.dans.easy.web.authn.RegistrationPage;
import nl.knaw.dans.easy.web.common.ApplicationUser;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederationToEasyAccountLinkingPage extends AbstractAuthenticationPage
{
    private static final Logger logger = LoggerFactory.getLogger(FederationToEasyAccountLinkingPage.class);

    public FederationToEasyAccountLinkingPage(final ApplicationUser appUser)
    {
        add(new IncludeResourceBehavior(LoginPage.class, "styles.css"));
        add(new FederationUserInfoPanel("federationUserInfoPanel", appUser));
        final UsernamePasswordAuthentication authentication = createUsernamePasswordAuthentication();
        add(new LoginPanelRegular("loginPanelRegular", new LoginAndLinkForm("loginForm", authentication, appUser.getUserId(), appUser.getOrganization())));
        final WebMarkupContainer registrationSection = new WebMarkupContainer("registration_and_linking");
        add(registrationSection);
        final Link<Void> registrationLink = new Link<Void>("registration")
        {
            private static final long serialVersionUID = 1L;

            public void onClick()
            {
                setResponsePage(new RegistrationPage(appUser.getUserId(), appUser.getOrganization()));
            };
        };
        registrationSection.add(registrationLink);
        final Label registrationLinkLabel = new Label("registration_link_label_id", new ResourceModel("registration_link_label"));
        registrationLinkLabel.setRenderBodyOnly(true);
        registrationLink.add(registrationLinkLabel);
    }

    private UsernamePasswordAuthentication createUsernamePasswordAuthentication()
    {
        UsernamePasswordAuthentication authentication;
        try
        {
            authentication = Services.getUserService().newUsernamePasswordAuthentication();
        }
        catch (final ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
        return authentication;
    }

}
