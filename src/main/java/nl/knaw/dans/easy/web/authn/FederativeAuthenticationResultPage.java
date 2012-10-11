package nl.knaw.dans.easy.web.authn;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class FederativeAuthenticationResultPage extends AbstractEasyNavPage
{
    private static Logger       logger                  = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    
    private Link couplingLink = null;
    private Link newAccountLink = null;
    
    private String federativeUserId = null;
    public String getFederativeUserId() { return federativeUserId;}

    private ApplicationUser appUser = null;
    
    public FederativeAuthenticationResultPage()
    {
        super();
//        init();
    }

    // TODO texts must come from property files
    private void init()
    {
        setStatelessHint(true);
        
        String resultMessage = "";
        addInvisibleLinks();
        
        if (((AbstractEasyNavPage) getPage()).isAuthenticated())
        {
            // Already logged in
            resultMessage = "You are logged in and can start using EASY";
        }
        else
        {
            HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
            // If we have an Federative Id, get the easy user and add to the session
            String retievedFederativeUserId = FederativeUserInfoExtractor.extractFederativeUserId(request);
            if (retievedFederativeUserId != null)
            {
                appUser = FederativeUserInfoExtractor.extractFederativeUser(request);
                try
                {
                    EasyUser easyUser = Services.getFederativeUserService().getUserById(getSessionUser(), retievedFederativeUserId);
                    
                    // NOTE maybe use a FederatedAthentication class
                    // have it set the userId make it in an correct state and put it in the session
                    Authentication authentication = new Authentication() {
                        private static final long serialVersionUID = 1L;
                    };
                    authentication.setState(Authentication.State.Authenticated);
                    authentication.setUser(easyUser);
                    
                    ((EasySession)Session.get()).setLoggedIn(authentication);
                    // logging for statistics
                    StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);
                    
                    // Logged in to EASY!
                    logger.info("login via the federation was succesfull");
                    //setResponsePage(getPage().getClass()); // this only works if the page is stateless
                    // Just go to the home page, without showing a message
                    throw new RestartResponseAtInterceptPageException(HomePage.class);
                }
                catch (ObjectNotAvailableException e)
                {
                    logger.info("There is no mapping for the given federative user id: " + retievedFederativeUserId);
                    
                    resultMessage = "Sorry, you are not logged in in EASY because your federative account is not coupled to an EASY account.";
                    // If there is no mapping to a easy user account further steps are needed: 
                    // create a mapping with an existing account which requires login into easy using passwd
                    // or create a new account plus mapping with a registration page
                    
                    federativeUserId = retievedFederativeUserId; // needed for the links onClick
                    makeInvisibleLinksVisible();
                }
                catch (ServiceException e)
                {
                    logger.error("Could not get easy user with the given federative user id: " + retievedFederativeUserId, e);
                    resultMessage = "Sorry, but you are not logged in."; // ?
                }
            }
            else
            {
                // no fedId should not happen unless this page was requested without Shibboleth redirect!!!
                logger.error("Could not retrieve the Federative user identification from Shibboleth");
                resultMessage = "Sorry, but you are not logged in"; // ?
            }
            
        }
        
        add(new Label("authenticationMessage", resultMessage));
    }
    
    private void addInvisibleLinks()
    {
        addInvisibleCoulpingLink();
        addInvisibleNewAccountLink();
    }
    
    private void makeInvisibleLinksVisible()
    {
        couplingLink.setVisible(true);
        newAccountLink.setVisible(true);
    }
    
    // TODO remove model, no need for that?
    private void addInvisibleCoulpingLink()
    {
        couplingLink = new Link<String>("couplingLink", new PropertyModel(this, "federativeUserId")) //, FederativeToEasyUserAccountCouplingPage.class)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean getStatelessHint()
            {
                return true;
            }

            @Override
            public void onClick()
            {
                String federativeUserId = getModelObject();
                setResponsePage(new FederativeToEasyUserAccountCouplingPage(federativeUserId));
            }
        };
        
        add(couplingLink);
        couplingLink.setVisible(false);
    }
    
    private void addInvisibleNewAccountLink()
    {
        // TODO go to a new account page
        newAccountLink = new Link("newAccountLink")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean getStatelessHint()
            {
                return true;
            }

            @Override
            public void onClick()
            {
                setResponsePage(new FederativeUserRegistrationPage(appUser));
            }
            
        };
        add(newAccountLink);
        newAccountLink.setVisible(false);
    }

}
