package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.wicket.behavior.FocusOnLoadBehavior;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.util.Messenger;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginForm extends AbstractEasyStatelessForm implements EasyResources
{
 

    /**
     * Constant for wicket id.
     */
    public static final String WI_LOGIN = "login";

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginForm.class);

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3701737275804449456L;


    /**
     * Constructor with wicketId and UsernamePasswordAuthentication.
     * 
     * @param wicketId
     *        id of this component
     * @param authentication
     *        messenger object for authentication
     */
    public LoginForm(final String wicketId, final UsernamePasswordAuthentication authentication)
    {
        super(wicketId, new CompoundPropertyModel(authentication));
        addCommonFeedbackPanel();
        add(new HiddenField(Messenger.PROP_TOKEN));
        RequiredTextField useridTextField = new RequiredTextField(Authentication.PROP_USER_ID);
        useridTextField.add(new FocusOnLoadBehavior());
        addWithComponentFeedback(useridTextField, new ResourceModel(USER_USER_ID));
        PasswordTextField passwordTextField = new PasswordTextField(Authentication.PROP_CREDENTIALS);
        passwordTextField.setRequired(true);
        addWithComponentFeedback(passwordTextField, new ResourceModel(USER_PASSWORD));
        add(new SubmitLink(WI_LOGIN));
    }

    @Override
    protected void onSubmit()
    {
        handleSubmit();
    }

    private void handleSubmit()
    {
        final UsernamePasswordAuthentication authentication = (UsernamePasswordAuthentication) getModelObject();

        if (UserLocking.isUserLocked(authentication.getUserId()))
        {
            warningMessage("state.TemporarilyLocked");
            return;
        }

        logger.info("Login attempt of user: " + authentication.getUserId());

        if (signIn(authentication))
        {
            logger.info("Session (" + (Session.exists() ? Session.get().getId() : "null") + ") of user (" + EasyWicketApplication.getUserIpAddress()
                    + ") authenticated.");
            infoMessage(USER_WELCOME, authentication.getUser().getDisplayName());

            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

            // do we need an upate on user info?
            if (authentication.getUser().isUserInfoUpdateRequired())
            {
                setResponsePage(UserInfoPage.class);
                return;
            }

            //
            if (!getPage().continueToOriginalDestination())
            {
                // Redirection to page viewed before login
                // Only works for DatasetViewPage, but ideally this should be working for all pages!
                Page page = ((EasySession) getSession()).getRedirectPage(LoginPage.class);

                if (page != null && page instanceof DatasetViewPage)
                {
                    // The page stored is not reused,
                    // because the refresh is not rebuilding the page in a logged-in state
                    // Instead of what is done in a 'back page' link handling:
                    // ((AbstractEasyPage) page).refresh();
                    // setResponsePage(page);
                    // just go to a fresh page but use the parameters from the given page
                    setResponsePage(DatasetViewPage.class, page.getPageParameters());
                }
                else
                {
                    setResponsePage(this.getApplication().getHomePage());
                }
            }
        }
        else
        {
            UserLocking.addTry(authentication.getUserId());
            
            logger.info("Failed authentication for: " + authentication);
            for (String stateKey : authentication.getAccumulatedStateKeys())
            {
                final String message = warningMessage(stateKey);
                logger.warn(message);
            }
            setResponsePage(this.getPage());
        }
    }

    /**
     * Sign the user in for the application.
     * 
     * @param authentication
     *        Authentication messenger object
     * @return True if signIn is successful
     */
    boolean signIn(final Authentication authentication)
    {
        boolean signedIn = false;
        AbstractAuthenticationPage authPage = (AbstractAuthenticationPage) getPage();
        signedIn = authPage.signIn(authentication);
        return signedIn;
    }

    /**
     * Only visible when not logged in.
     * 
     * @return false if logged in.
     */
    @Override
    public boolean isVisible()
    {
        return !((AbstractEasyNavPage) getPage()).isAuthenticated();
    }
}
