package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.InfoPage;

import org.apache.wicket.RestartResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoginAndLinkForm extends LoginForm
{
    private static final long serialVersionUID = 8045436625492633679L;
    private static final Logger logger = LoggerFactory.getLogger(LoginAndLinkForm.class);

    private String federationUserId;
    private String institution;
    private String easyUserId;

    public LoginAndLinkForm(String wicketId, UsernamePasswordAuthentication authentication, String federationUserId, String institution)
    {
        super(wicketId, authentication);
        this.federationUserId = federationUserId;
        /*
         * Replace < and > by their entities, just to be sure. Otherwise, theoretically there could be
         * cross site scripting.
         */
        this.institution = institution.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    @Override
    protected void handleSuccessfulLogin()
    {
        setEasyUserId();
        createLinkBetweenCurrentEasyUserAndFederationUser();
        infoMessage("login-and-link.link-created", institution);
        InfoPage info = new InfoPage("Link created");
        info.setAllowHtml(true);
        throw new RestartResponseException(info);
    }

    private void setEasyUserId()
    {
        easyUserId = EasySession.getSessionUser().getId();
    }

    private void createLinkBetweenCurrentEasyUserAndFederationUser()
    {
        try
        {
            Services.getFederativeUserService().addFedUserToEasyUserIdCoupling(federationUserId, easyUserId);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage("login-and-link.link-not-created");
            logger.error(message, e);
            throw new InternalWebError();
        }
    }

}
