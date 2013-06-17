package nl.knaw.dans.easy.web.authn.login;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class FederativeAuthenticationResultPage extends AbstractEasyNavPage
{
    private static final String SHIB_SESSION_ID = Services.getFederativeUserService().getPropertyNameShibSessionId();
    private static Logger logger = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    private String federativeUserId = null;

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
        }
        else
        {
            HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
            if (!hasShibbolethSession(request))
            {
                logger.error("Shibboleth does not appear to have sent a session ID");
                infoPageWithError();
            }
            else
            {
                try
                {
                    fedUser = FederationUser.fromHttpRequest(request);
                }
                catch (IllegalArgumentException e)
                {
                    infoPageWithError();
                    return;
                }
                try
                {
                    EasyUser easyUser = Services.getFederativeUserService().getUserById(getSessionUser(), fedUser.getUserId());
                    Authentication authentication = new Authentication();
                    authentication.setState(Authentication.State.Authenticated);
                    authentication.setUser(easyUser);
                    getEasySession().setLoggedIn(authentication);
                    StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

                    logger.info("login via the federation was succesfull");
                    throw new RestartResponseAtInterceptPageException(HomePage.class);
                }
                catch (ObjectNotAvailableException e)
                {
                    logger.info("There is no mapping for the given federative user id: {}", fedUser.getUserId());
                    setResponsePage(new FederationToEasyAccountLinkingPage(fedUser));
                }
                catch (ServiceException e)
                {
                    logger.error("Could not get easy user with the given federative user id: {}", fedUser.getUserId(), e);
                    errorMessage("federative.error_during_federation_login");
                    setResponsePage(new InfoPage(getString("federative.error_during_federation_login")));
                }
            }

        }
    }

    private boolean hasShibbolethSession(HttpServletRequest request)
    {
        return request.getAttribute(SHIB_SESSION_ID) != null;
    }

    private void infoPageWithError()
    {
        warningMessage("federative.error_during_federation_login");
        setResponsePage(new InfoPage("Error during federation login"));
    }

}
