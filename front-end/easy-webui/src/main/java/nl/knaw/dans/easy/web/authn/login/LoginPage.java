package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.authn.AbstractAuthenticationPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page for logging into the application.
 */
@RequireHttps
public class LoginPage extends AbstractAuthenticationPage {
    private static Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private static final long serialVersionUID = 8501036308620025067L;
    private static final String LOGIN_PANEL_REGULAR = "loginPanelRegular";
    private static final String LOGIN_PANEL_FEDERATION = "loginPanelFederation";

    @SpringBean(name = "userService")
    private UserService userService;

    @SpringBean(name = "federativeUserService")
    private FederativeUserService federativeUserService;

    private void init() {
        setStatelessHint(true);
        UsernamePasswordAuthentication authentication;
        try {
            authentication = userService.newUsernamePasswordAuthentication();
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
        if (!isAuthenticated()) {
            add(new LoginPanelFederation(LOGIN_PANEL_FEDERATION).setVisible(federativeUserService.isFederationLoginEnabled()));
            add(new LoginPanelRegular(LOGIN_PANEL_REGULAR, new LoginForm("loginForm", authentication)));
        } else {
            hide(LOGIN_PANEL_REGULAR);
            hide(LOGIN_PANEL_FEDERATION);
        }
    }

    public LoginPage() {
        init();
    }

    public LoginPage(PageParameters parameters) {
        super(parameters);
        init();
    }
}
