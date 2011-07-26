package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.PageParameters;
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
