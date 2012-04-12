package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.behavior.FocusOnLoadBehavior;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;

import org.apache.wicket.Session;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class FederativeToEasyUserAccountCouplingPage extends AbstractEasyNavPage
{
    private static Logger       logger                  = LoggerFactory.getLogger(FederativeToEasyUserAccountCouplingPage.class);

    private String federativeUserId = null;
    public String getFederativeUserId() { return federativeUserId;}
    
    public FederativeToEasyUserAccountCouplingPage()
    {
        super();
//        init();
    }

    public FederativeToEasyUserAccountCouplingPage(String federativeUserId)
    {
        super();
        this.federativeUserId = federativeUserId;
//        init();
    }

    private void init()
    {
        // TODO have a login form like thing with a username and password, 
        // the submit will have the text 'couple' ??  with my EASY my account

        logger.debug("Coupling page for " + getFederativeUserId());
        
        // need an login like form to make the coupling, 
        // the submit should retrieve the EasyUser an then make the coupling!
        
        //add( new CouplingForm("easyLoginForm", new PropertyModel(this, "fedId")));
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
        add( new CouplingForm("easyLoginForm", authentication));
    }
    
    // TODO maybe make separate file for this class?
    // NOTE: Is a lot like the LoginForm
    public class CouplingForm extends AbstractEasyStatelessForm  implements EasyResources
    {

        public CouplingForm(final String wicketId, final UsernamePasswordAuthentication authentication)
        {
            super(wicketId, new CompoundPropertyModel(authentication));

            addCommonFeedbackPanel();

            RequiredTextField useridTextField = new RequiredTextField(Authentication.PROP_USER_ID);
            useridTextField.add(new FocusOnLoadBehavior());
            addWithComponentFeedback(useridTextField, new ResourceModel(USER_USER_ID));

            PasswordTextField passwordTextField = new PasswordTextField(Authentication.PROP_CREDENTIALS);
            passwordTextField.setRequired(true);
            addWithComponentFeedback(passwordTextField, new ResourceModel(USER_PASSWORD));

            add(new SubmitLink("login"));
        }

        @Override
        protected void onSubmit()
        {
            final UsernamePasswordAuthentication authentication = (UsernamePasswordAuthentication) getModelObject();
            logger.info("Coupling attempt of federative user: " + getFederativeUserId() + ", with easy id: " + authentication.getUserId());
            
            if (UserLocking.isUserLocked(authentication.getUserId()))
            {
                warningMessage("state.TemporarilyLocked");
                return;
            }
            
            try
            {
                Services.getUserService().authenticate(authentication);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message);
                throw new InternalWebError();
            }
            
            // test if Easy User was correctly authenticated first before making the coupling
            if (authentication.isCompleted())
            {
                // create the coupling
                try
                {
                    Services.getFederativeUserService().addFedUserToEasyUserIdCoupling(getFederativeUserId(), authentication.getUserId());
                }
                catch (ServiceException e)
                {
                    // We didn't make a coupling!
                    
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new InternalWebError();
                }
                
                // Only login if we made a coupling
                getEasySession().setLoggedIn(authentication);
                logger.info("Session (" + (Session.exists() ? Session.get().getId() : "null") + ") of user (" + EasyWicketApplication.getUserIpAddress()
                        + ") authenticated.");
                infoMessage(USER_WELCOME, authentication.getUser().getDisplayName());
                // logging for statistics
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

                WicketUtil.commonMessage(this, "federative.coupling_succesful", FeedbackMessage.INFO);
                setResponsePage(new InfoPage(EasyWicketApplication.getProperty("federative.coupling_succesful.infoPageTitle")));
                
                // Or Just go home, but no continueToOriginalDestination(), 
                // because we changed the user information by coupling and want to inform of the change!
                //setResponsePage(this.getApplication().getHomePage());
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
        
    }
}
