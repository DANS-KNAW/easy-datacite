package nl.knaw.dans.easy.web.authn;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page for logging into the application.
 */
@RequireHttps
public class LoginPage extends AbstractAuthenticationPage
{
    private static Logger       logger                  = LoggerFactory.getLogger(LoginPage.class);

    /**
     * LoginPanel wicket id.
     */
    static final String       LOGIN_PANEL_BIG  = "loginPanelBig";

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 8501036308620025067L;
    
    /**
     * Initialize the same for every constructor.
     */
    private void init()
    {
        setStatelessHint(true);
        
        // TODO enable this if functionality is completed
        // Federative Authentication Link
        /*
        add(new ExternalLink("federationLink", getFederationURLString(), "Login")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean getStatelessHint()
            {
                return true;
            }
            
        });
        */
        // Note: add an invisible link if you don't want it: add(new ExternalLink("federationLink","","").setVisible(false));
        add(new ExternalLink("federationLink","","").setVisible(false));
        
        UsernamePasswordAuthentication authentication;
		try
		{
			authentication = Services.getUserService().newUsernamePasswordAuthentication();
		}
		catch (ServiceException e)
		{
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
	        throw new InternalWebError();
		}
        this.add(new LoginPanel(LOGIN_PANEL_BIG, authentication));
    }

    // construct the link for the Federative Athentication
    private String getFederationURLString()
    {
      String linkURLString = "";
      
      try
      {
          // get URL for FederativeAuthenticationResultPage
          String relStr = RequestCycle.get().urlFor(FederativeAuthenticationResultPage.class, new PageParameters()).toString();
          String returnURLString = org.apache.wicket.protocol.http.RequestUtils.toAbsolutePath(relStr);
          logger.debug("return URL: " + returnURLString);

          returnURLString = URLEncoder.encode(returnURLString, "UTF-8");
          
          // add the easy return page url as parameter to 
          // the Shibboleth url
          String federationURLString = "https://eof12.dans.knaw.nl/Shibboleth.sso/Login?target=";
          linkURLString = federationURLString + returnURLString;
          logger.debug("link URL: " + linkURLString);
      }
      catch (UnsupportedEncodingException e)
      {
          logger.error("Could not construct Federative login link", e);
      }
      

      return linkURLString;
    }
    
    /**
     * Default constructor.
     */
    public LoginPage()
    {
        super();
        init();
    }
    
    public LoginPage(PageParameters parameters)
    {
        super(parameters);
        init();
    }

}
