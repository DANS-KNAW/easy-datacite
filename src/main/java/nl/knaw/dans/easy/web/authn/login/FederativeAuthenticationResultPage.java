package nl.knaw.dans.easy.web.authn.login;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class FederativeAuthenticationResultPage extends AbstractEasyNavPage
{
    private static Logger logger = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    private String federativeUserId = null;

    @SpringBean(name = "federativeUserService")
    private FederativeUserService federativeUserService;

    public String getFederativeUserId()
    {
        return federativeUserId;
    }

    private FederationUser fedUser;

    public FederativeAuthenticationResultPage()
    {
        super();
        init();
    }

    private void init()
    {
        setStatelessHint(true);
        if (((AbstractEasyNavPage) getPage()).isAuthenticated())
        {
            setResponsePage(HomePage.class);
            return;
        }
        HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
        try
        {
            fedUser = new FederationUserFactory().create(request);
        }
        catch (IllegalArgumentException e)
        {
            logger.error(e.getMessage());
            setInfoResponePage();
            return;
        }
        EasyUser easyUser;
        try
        {
            easyUser = federativeUserService.getUserById(getSessionUser(), fedUser.getUserId());
        }
        catch (ObjectNotAvailableException e)
        {
            logger.info("There is no mapping for the given federative user id: {}", fedUser.getUserId());
            setResponsePage(new FederationToEasyAccountLinkingPage(fedUser));
            return;
        }
        catch (ServiceException e)
        {
            logger.error("Could not get easy user with the given federative user id: {}", fedUser.getUserId(), e);
            setInfoResponePage();
            return;
        }
        Authentication authentication = new Authentication();
        authentication.setState(Authentication.State.Authenticated);
        authentication.setUser(easyUser);
        if (easyUser.isActive())
        {
            getEasySession().setLoggedIn(authentication);
            // TODO why no statistics for a normal login?
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

            logger.info("login via the federation was succesfull");
            throw new RestartResponseAtInterceptPageException(HomePage.class);
        }
        else
        {
            if (!easyUser.isBlocked())
                warningMessage("state.NotBlocked");
            else
                errorMessage("state.Blocked");
            setResponsePage(new LoginPage());
        }
    }

    private void setInfoResponePage()
    {
        warningMessage("federative.error_during_federation_login");
        setResponsePage(new InfoPage("Error during federation login"));
    }
}
