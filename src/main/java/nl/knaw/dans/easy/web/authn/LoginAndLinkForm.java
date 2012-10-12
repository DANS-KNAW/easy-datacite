package nl.knaw.dans.easy.web.authn;

import org.apache.wicket.RestartResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.InfoPage;

public class LoginAndLinkForm extends LoginForm
{
    private static final Logger logger = LoggerFactory.getLogger(LoginAndLinkForm.class);

    private String federationUserId;
    private String institution;
    private String easyUserId;

    public LoginAndLinkForm(String wicketId, UsernamePasswordAuthentication authentication, String federationUserId, String institution)
    {
        super(wicketId, authentication);
        this.federationUserId = federationUserId;
        this.institution = institution;
    }

    @Override
    protected void handleSuccessfulLogin()
    {
        setEasyUserId();
        createLinkBetweenCurrentEasyUserAndFederationUser();
        infoMessage("login-and-link.link-created", federationUserId, institution, easyUserId);
        throw new RestartResponseException(new InfoPage("Link created"));
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
