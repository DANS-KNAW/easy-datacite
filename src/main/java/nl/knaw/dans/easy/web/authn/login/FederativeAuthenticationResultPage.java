package nl.knaw.dans.easy.web.authn.login;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.common.ApplicationUser;
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
    private static Logger logger = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    private String federativeUserId = null;

    public String getFederativeUserId()
    {
        return federativeUserId;
    }

    private ApplicationUser appUser = null;

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
            // If we have an Federative Id, get the easy user and add to the session
            String federationUserId = FederativeUserInfoExtractor.extractFederativeUserId(request);
            if (federationUserId != null)
            {
                appUser = FederativeUserInfoExtractor.extractFederativeUser(request);
                try
                {
                    EasyUser easyUser = Services.getFederativeUserService().getUserById(getSessionUser(), federationUserId);

                    // NOTE maybe use a FederatedAthentication class
                    // have it set the userId make it in an correct state and put it in the session
                    Authentication authentication = new Authentication()
                    {
                        private static final long serialVersionUID = 1L;
                    };
                    authentication.setState(Authentication.State.Authenticated);
                    authentication.setUser(easyUser);

                    getEasySession().setLoggedIn(authentication);
                    StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

                    logger.info("login via the federation was succesfull");
                    throw new RestartResponseAtInterceptPageException(HomePage.class);
                }
                catch (ObjectNotAvailableException e)
                {
                    logger.info("There is no mapping for the given federative user id: " + federationUserId);
                    setResponsePage(new FederationToEasyAccountLinkingPage(appUser));
                }
                catch (ServiceException e)
                {
                    logger.error("Could not get easy user with the given federative user id: " + federationUserId, e);
                    setResponsePage(new InfoPage("federative.error_during_federation_login"));
                }
            }
            else
            {
                logger.error("Could not retrieve the Federative user identification from Shibboleth");
                setResponsePage(new InfoPage("federative.error_during_federation_login"));
            }

        }
    }
}
