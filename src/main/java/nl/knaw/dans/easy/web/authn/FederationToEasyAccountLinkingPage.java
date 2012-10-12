package nl.knaw.dans.easy.web.authn;

import org.apache.wicket.markup.html.link.PageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

public class FederationToEasyAccountLinkingPage extends AbstractAuthenticationPage
{
    private static final Logger logger = LoggerFactory.getLogger(FederationToEasyAccountLinkingPage.class);

    public FederationToEasyAccountLinkingPage(ApplicationUser appUser)
    {
        add(new FederationUserInfoPanel("federationUserInfoPanel", appUser));
        UsernamePasswordAuthentication authentication;
        try
        {
            authentication = Services.getUserService().newUsernamePasswordAuthentication();
        }
        catch (ServiceException e1)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e1);
            throw new InternalWebError();
        }
        add(new LoginPanelRegular("loginPanelRegular", new LoginAndLinkForm("loginForm", authentication, appUser.getUserId(), appUser.getOrganization())));
        addRegisterLink(appUser);
    }

    private void addRegisterLink(ApplicationUser appUser)
    {
        add(new PageLink("registration", new RegistrationPage(appUser.getUserId(), appUser.getOrganization()))
        {
            /**
             * Serial version uid.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Check if visible.
             * 
             * @return true if visible
             */
            @Override
            public boolean isVisible()
            {
                return !isAuthenticated();
            }

            /**
             * Always stateless.
             * 
             * @return true
             */
            @Override
            public boolean getStatelessHint() // NOPMD: wicket method.
            {
                return true;
            }
        });
    }

}
