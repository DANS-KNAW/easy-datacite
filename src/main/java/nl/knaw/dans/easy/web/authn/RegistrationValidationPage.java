package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.RegistrationMailAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.InfoPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page to validate a registration.
 * 
 * @author Herman Suijs
 */
@RequireHttps
public class RegistrationValidationPage extends AbstractAuthenticationPage
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegistrationValidationPage.class);

    /**
     * Parameter id.
     */
    public static final String PARAM_NAME_USERID = "userId";

    /**
     * Parameter id.
     */
    public static final String PARAM_NAME_DATE_TIME = "dateTime";

    /**
     * Parameter id.
     */
    public static final String PARAM_NAME_TOKEN = "token";

    public static final String WI_GOTO_LOGIN_LINK = "gotoLogin";

    public static final String RI_GOTO_LOGIN_LABEL = "gotoLoginLabel";

    public static final String WI_SUCCESS = "validationSuccesful";

    public static final String RI_SUCCESS = "validationSuccesful";

    /**
     * Default Constructor.
     * 
     * @param parameters
     *        pageParameters.
     */
    public RegistrationValidationPage(final PageParameters parameters)
    {
        super(parameters);
        init(parameters);
    }

    /**
     * Constructor with pageMap.
     * 
     * @param pageMap
     *        pagemap
     * @param parameters
     *        pageParameters
     */
    public RegistrationValidationPage(final IPageMap pageMap, final PageParameters parameters)
    {
        super(pageMap, parameters);
        init(parameters);
    }

    /**
     * Initialize the same for every constructor.
     * 
     * @param parameters
     *        pageParameters
     */
    private void init(final PageParameters parameters)
    {
        // Make this page stateless.
        this.setStatelessHint(true);

        String userId = parameters.getString(PARAM_NAME_USERID);
        String requestTime = parameters.getString(PARAM_NAME_DATE_TIME);
        String requestToken = parameters.getString(PARAM_NAME_TOKEN);

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(requestTime) || StringUtils.isBlank(requestToken))
        {
            final String message = errorMessage(EasyResources.FORM_INVALID_PARAMETERS);
            logger.error(message);
            throw new InternalWebError();
        }

        RegistrationMailAuthentication authentication;
        try
        {
            authentication = Services.getUserService().newRegistrationMailAuthentication(userId, requestTime, requestToken);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }

        if (signIn(authentication))
        {
            try
            {
                EasyUser user = EasySession.getSessionUser();
                user.setState(State.ACTIVE);
                Services.getUserService().update(user, user);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new InternalWebError();
            }

            WicketUtil.commonMessage(this, EasyResources.REGISTRATION_ACCOUNT_VALIDATED, FeedbackMessage.INFO);
            setResponsePage(new InfoPage(EasyWicketApplication.getProperty(EasyResources.REGISTRATION_ACCOUNT_VALIDATED_TITLE)));
        }
        else
        {
            errorMessage(EasyResources.URL_AUTHENTICATION);
            logger.error("An invalid URL is tried for validating a registration: " + authentication.toString());
            this.setResponsePage(ErrorPage.class);
            return; // exit immediately
        }

    }
}
