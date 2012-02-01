package nl.knaw.dans.easy.web.authn;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

@RequireHttps
public class FederativeAuthenticationResultPage extends AbstractEasyNavPage
{
    private static Logger       logger                  = LoggerFactory.getLogger(FederativeAuthenticationResultPage.class);
    
    static final String FEDUSERID_ATTRIBUTE_NAME = "Shib-eduPersonPN";
    // Notes
    // request.getAttributeNames(); will not return the Shibboleth variables
    // Also we can see the variables on eof12: 
    // https://eof12.dans.knaw.nl/cgi-bin/env
    // But when the vars get into tomcat via AJP the prefix "AJP_" is removed 
    // and underscores '_' are translated to minuses '-'. 
    //
    // From our Java servlet we might want to use: 
    // Shib-HomeOrg ->  organisation
    // Shib-eduPersonPN -> fedUserId
    // Shib-email -> email
    // Shib-givenName -> firstName
    // Shib-surName -> lastName
    
    public FederativeAuthenticationResultPage()
    {
        super();
        init();
    }

    // TODO texts must come from property files
    private void init()
    {
        setStatelessHint(true);
        
        String resultMessage = "";
        
        if (((AbstractEasyNavPage) getPage()).isAuthenticated())
        {
            // Already logged in
            resultMessage = "You are logged in and can start using EASY";
        }
        else
        {
            HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
            if (hasFederativeAuthentication(request))
            {
                logger.info("login via the federation was succesfull");
                //setResponsePage(getPage().getClass()); // this only works if the page is stateless
                // Just go to the home page, without showing a message
                throw new RestartResponseAtInterceptPageException(HomePage.class);
            }
            else
            {
                // not authentcated
                
                // Could mean that the federative login was OK, but there is no mapping to an EASY account
                // That is not handled right now!
                resultMessage = "Sorry, but you are not logged in (possibly because your federative account has not been linked to an easy account), pleasy try again";
            }
        }
        
        add(new Label("authenticationMessage", resultMessage));
    }
    
    private boolean hasFederativeAuthentication(HttpServletRequest request)
    {
        boolean result = false;
        
        // If we have an Federative Id, get the easy user and add to the session
        String fedUserId = getFederativeUserId(request);
        if (fedUserId != null)
        {
            // call service
            try
            {
                EasyUser easyUser = Services.getFederativeUserService().getUserById(getSessionUser(), fedUserId);
                
                // TODO if there is no mapping to a easy user account further steps are needed: 
                // create a mapping with an existing account which requires login into easy using passwd
                // or create a new account plus mapping with a registration page
                
                
                // NOTE maybe use a FederatedAthentication class
                // have it set the userId make it in an correct state and put it in the session
                Authentication authentication = new Authentication() {
                    private static final long serialVersionUID = 1L;
                };
                authentication.setState(Authentication.State.Authenticated);
                authentication.setUser(easyUser);
                
                ((EasySession)Session.get()).setLoggedIn(authentication);
                
                result = true; // Logged in to EASY!
            }
            catch (ObjectNotAvailableException e)
            {
                logger.info("There is no mapping for the given federative user id: " + fedUserId);
            }
            catch (ServiceException e)
            {
                logger.error("Could not get easy user with the given federative user id: " + fedUserId, e);
            }
        }
        else
        {
            // no fedId should not happen unless this page was requested without Shibboleth redirect!!!
            logger.error("Could not retrieve the Federative user identification from Shibboleth");
        }
        
        return result;
    }
    
    private String getFederativeUserId(HttpServletRequest request)
    {
        String fedUserId = null;
        // NOTE the request attribute value can be mocked for testing
        
        // NOTE for testing I mapped "paul.boon.as.guest@SURFguest.nl"
        fedUserId = (String)request.getAttribute(FEDUSERID_ATTRIBUTE_NAME);//"paul.boon.as.guest@SURFguest.nl";
        
        /* NOTE could be a list seperated by semi-colon ';', if so split and take the first!
        if (fedUserId != null) 
        {
            
            // get first (if any)
            int firstSeperatorIndex = fedUserId.indexOf(';');
            if (firstSeperatorIndex > 0)
                fedUserId = fedUserId.substring(0, firstSeperatorIndex);
        }
        */
        
        return fedUserId;
    }
    
    // utility for printing header info
    private void printRequest(HttpServletRequest request)
    {
        System.out.println("headers");
        Enumeration e = request.getHeaderNames();
        String value = null;
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            value = request.getHeader(name);
            System.out.println(name + "=" + value);
        }
        
        System.out.println("attributes");
        e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            value = request.getAttribute(name).toString();
            System.out.println(name + "=" + value);
        }
        
        // NOTE the Shibboleth attributes are not available from the getAttributeNames()
    }
}
