package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class ChangePasswordPage extends AbstractAuthenticationPage
{
    public static final String PM_REQUEST_TIME = "requestTime";
    public static final String PM_REQUEST_TOKEN = "requestToken";
    public static final String PM_USER_ID = "userId";
    private static final String WI_CHANGE_PASSWORD_PANEL = "changePasswordPanel";
    private static Logger logger = LoggerFactory.getLogger(ChangePasswordPage.class);

    /**
     * No-argument constructor for displaying the change password page for the current user.
     */
    public ChangePasswordPage()
    {
        EasyUser currentUser = ((EasySession) getSession()).getUser();
        if (currentUser.isAnonymous())
        {
            logger.error(getString(EasyResources.ANONYMOUS_USER).replace("$1", this.getClass().getSimpleName()));
            throw new RestartResponseException(HomePage.class);
        }
        else
        {
            ChangePasswordMessenger messenger = new ChangePasswordMessenger(currentUser, false);
            init(messenger);
        }
    }

    /**
     * Constructor with PageParameters, called from a link in a mail, previously sent to a user.
     * 
     * @see ForgottenPasswordPage
     * @see #PM_USER_ID
     * @see #PM_REQUEST_TIME
     * @see #PM_REQUEST_TOKEN
     * @param paras
     *        parameters from url previously sent by mail
     */
    public ChangePasswordPage(PageParameters paras)
    {
        super(paras);
        final String userId = paras.getString(PM_USER_ID);
        final String requestTime = paras.getString(PM_REQUEST_TIME);
        final String requestToken = paras.getString(PM_REQUEST_TOKEN);

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(requestTime) || StringUtils.isBlank(requestToken))
        {
            final String message = errorMessage(EasyResources.URL_PARAMETERS);
            logger.error(message);
            throw new InternalWebError();
        }

        ForgottenPasswordMailAuthentication authentication;
        try
        {
            authentication = Services.getUserService().newForgottenPasswordMailAuthentication(userId, requestTime, requestToken);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message);
            throw new InternalWebError();
        }

        if (signIn(authentication))
        {
            ChangePasswordMessenger messenger = new ChangePasswordMessenger(authentication.getUser(), true);
            init(messenger);
        }
        else
        {
            final String message = errorMessage(EasyResources.URL_AUTHENTICATION);
            logger.error(message);
            throw new InternalWebError();
        }
    }

    private void init(ChangePasswordMessenger messenger)
    {
        add(new ChangePasswordPanel(WI_CHANGE_PASSWORD_PANEL, messenger));
    }

}
