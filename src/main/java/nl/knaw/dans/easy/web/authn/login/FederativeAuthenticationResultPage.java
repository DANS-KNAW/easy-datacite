package nl.knaw.dans.easy.web.authn.login;

import java.io.File;

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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class FederativeAuthenticationResultPage extends AbstractEasyNavPage
{
    private static String propertyNameShibSessionID;
    private static Logger logger = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    private String federativeUserId = null;

    @SpringBean(name = "federationLoginDebugEnabled")
    private Boolean federationLoginDebugEnabled;

    @SpringBean(name = "federationLoginDebugUserFile")
    private String federationLoginDebugFileName;

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
            if (!hasShibbolethSession(request) && !federationLoginDebugEnabled)
            {
                logger.error("Shibboleth does not appear to have sent a session ID");
                infoPageWithError();
            }
            else
            {
                try
                {
                    if (federationLoginDebugEnabled)
                    {
                        logger.debug("Federation login debug enabled");
                        File federationLoginDebugFile = new File(federationLoginDebugFileName);
                        if (federationLoginDebugFile.exists())
                        {
                            fedUser = FederationUser.fromFile(federationLoginDebugFile);
                        }
                        else
                        {
                            logger.error("No federation login debug file found at {}. Cannot proceed.", federationLoginDebugFile);
                        }
                    }
                    else
                    {
                        fedUser = FederationUser.fromHttpRequest(request);
                    }
                }
                catch (IllegalArgumentException e)
                {
                    infoPageWithError();
                    logger.debug(e.getMessage(), e);
                    return;
                }
                try
                {
                    EasyUser easyUser = Services.getFederativeUserService().getUserById(getSessionUser(), fedUser.getUserId());
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
                        // TODO proper error message
                        if (!easyUser.isBlocked())
                            warningMessage("state.NotBlocked");
                        else
                            errorMessage("state.Blocked");
                        setResponsePage(new LoginPage());
                    }
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

    boolean hasShibbolethSession(HttpServletRequest request)
    {
        if (propertyNameShibSessionID == null)
            propertyNameShibSessionID = Services.getFederativeUserService().getPropertyNameShibSessionId();
        logger.debug("propertyNameShibSessionID = {}", propertyNameShibSessionID);
        return request.getAttribute(propertyNameShibSessionID) != null;
    }

    private void infoPageWithError()
    {
        warningMessage("federative.error_during_federation_login");
        setResponsePage(new InfoPage("Error during federation login"));
    }

}
