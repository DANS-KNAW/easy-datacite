package nl.knaw.dans.easy.web.authn;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.LettersAndDigitsValidator;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.FederativeUserRegistration;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.authn.FederativeToEasyUserAccountCouplingPage.CouplingForm;
import nl.knaw.dans.easy.web.authn.RegistrationForm.AcceptConditions;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.easy.web.wicketutil.PasswordPolicyValidator;

/*
 * For this user the authentication is done by the Federation and NOT in EASY, 
 * so for registration with EASY we don't need a username and password. 
 */
@RequireHttps
public class FederativeUserRegistrationPage extends AbstractEasyNavPage
{
    private static Logger       logger                  = LoggerFactory.getLogger(FederativeUserRegistrationPage.class);

    private ApplicationUser appUser = null;
    
    public FederativeUserRegistrationPage(final ApplicationUser appUser)
    {
        super();
        this.appUser = appUser;
//        init();
    }

    private void init()
    {
        add( new FederativeUserRegistrationForm("registrationForm", appUser));//new ApplicationUser()));
    }
    
    public class FederativeUserRegistrationForm extends AbstractEasyStatelessForm  implements EasyResources
    {
        private final SubmitLink registerLink = new SubmitLink("register");

        public FederativeUserRegistrationForm(String wicketId, final ApplicationUser appUser)
        {
            super(wicketId, new CompoundPropertyModel<ApplicationUser>(appUser));

            addCommonFeedbackPanel();
            
            // NOTE No username and password needed for a federative user
// But we need a username for the user repo(LDAP), just use the ePPN
// does an ePPN always validate as easy username, anyway it should be unique!
            
            
            // Add field title
            addWithComponentFeedback(new TextField<String>(ApplicationUser.TITLE), new ResourceModel(RegistrationPage.USER_TITLE));

            // Add field initials
            addWithComponentFeedback(new RequiredTextField(ApplicationUser.INITIALS), new ResourceModel(RegistrationPage.USER_INITIALS));

            // Add field prefixes
            addWithComponentFeedback(new TextField(ApplicationUser.PREFIXES), new ResourceModel(RegistrationPage.USER_PREFIXES));

            // Add field surname
            addWithComponentFeedback(new RequiredTextField(ApplicationUser.SURNAME), new ResourceModel(RegistrationPage.USER_SURNAME));

            // Add email with validator
            FormComponent email = new RequiredTextField(ApplicationUser.EMAIL);
            addWithComponentFeedback(email.add(EmailAddressValidator.getInstance()), new ResourceModel(RegistrationPage.USER_EMAIL));

            addWithComponentFeedback(new TextField<String>(ApplicationUser.FUNCTION), new ResourceModel(RegistrationPage.USER_FUNCTION));
            addWithComponentFeedback(new TextField<String>(ApplicationUser.TELEPHONE), new ResourceModel(RegistrationPage.USER_TELEPHONE));

            add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE1, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE1),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));
            add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE2, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE2),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));
            add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE3, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE3),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));

            addWithComponentFeedback(new TextField<String>(ApplicationUser.DAI).add(new PatternValidator(Pattern.compile("\\d{8}[A-Z0-9]"))), new ResourceModel(
                    RegistrationPage.USER_DAI));
            addWithComponentFeedback(new TextField<String>(ApplicationUser.ORGANIZATION), new ResourceModel(RegistrationPage.USER_ORGANIZATION));
            addWithComponentFeedback(new TextField<String>(ApplicationUser.DEPARTMENT), new ResourceModel(RegistrationPage.USER_DEPARTMENT));
            addWithComponentFeedback(new RequiredTextField<String>(ApplicationUser.ADDRESS), new ResourceModel(RegistrationPage.USER_ADDRESS));
            addWithComponentFeedback(new RequiredTextField<String>(ApplicationUser.POSTAL_CODE), new ResourceModel(RegistrationPage.USER_POSTALCODE));
            addWithComponentFeedback(new RequiredTextField<String>(ApplicationUser.CITY), new ResourceModel(RegistrationPage.USER_CITY));
            addWithComponentFeedback(new TextField<String>(ApplicationUser.COUNTRY), new ResourceModel(RegistrationPage.USER_COUNTRY));

            // inform by email newsletter selection (Yes/No radio buttons)
            RadioGroup informByEmailSelection = new RadioGroup(ApplicationUser.OPTS_FOR_NEWSLETTER);
            informByEmailSelection.add(new Radio<Boolean>("news-yes", new Model<Boolean>(true)));
            informByEmailSelection.add(new Radio<Boolean>("news-no", new Model<Boolean>(false)));
            add(informByEmailSelection);

            RadioGroup logMyActionsSelection = new RadioGroup(ApplicationUser.LOG_MY_ACTIONS);
            logMyActionsSelection.add(new Radio<Boolean>("log-yes", new Model<Boolean>(true)));
            logMyActionsSelection.add(new Radio<Boolean>("log-no", new Model<Boolean>(false)));
            add(logMyActionsSelection);

            add(new EasyEditablePanel("editablePanel", "/editable/Registration.template"));

            final AcceptConditions acceptConditions = new AcceptConditions(ApplicationUser.ACCEPT_CONDITIONS);
            add(acceptConditions);

            registerLink.setEnabled(false);
            add(registerLink);

            SubmitLink cancelLink = new SubmitLink(RegistrationPage.CANCEL_LINK)
            {

                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onSubmit()
                {
                    setResponsePage(HomePage.class);
                }
            };
            cancelLink.setDefaultFormProcessing(false);
            add(cancelLink);
        }

        public class AcceptConditions extends CheckBox
        {
            private static final long serialVersionUID = 5303251895855641726L;

            public AcceptConditions(String id)
            {
                super(id);
            }

            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }

            @Override
            protected void onSelectionChanged(Object newSelection)
            {
                boolean accept = new Boolean(true).equals(newSelection);
                registerLink.setEnabled(accept);
            }
        }

        @Override
        protected void onError()
        {
            super.onError();
        }

        /**
         * Execution after submit of the form.
         */
        @Override
        public void onSubmit()
        {
            final ApplicationUser appUser = (ApplicationUser) getModelObject();

            // Ehhhhh, register and couple this federative user, using a service...
            // fedId is same as userId!!!!
            String federativeUserId = appUser.getUserId();
            //Services.getFederativeUserService().addFedUserToEasyUserIdCoupling(federativeUserId, appUser.getUserId());
            // register this special user
            FederativeUserRegistration registration = new FederativeUserRegistration(federativeUserId, appUser.getBusinessUser());
            try
            {
                registration = Services.getUserService().handleRegistrationRequest(registration);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new InternalWebError();
            }
            
            if (registration.isCompleted())
            {
                // Only login if we made a coupling
                // NOTE maybe use a FederatedAthentication class
                // have it set the userId make it in an correct state and put it in the session
                Authentication authentication = new Authentication() {
                    private static final long serialVersionUID = 1L;
                };
                authentication.setState(Authentication.State.Authenticated);
                authentication.setUser(appUser.getBusinessUser());
                getEasySession().setLoggedIn(authentication);
                logger.info("Session (" + (Session.exists() ? Session.get().getId() : "null") + ") of user (" + EasyWicketApplication.getUserIpAddress()
                        + ") authenticated.");
                infoMessage(USER_WELCOME, authentication.getUser().getDisplayName());
                //authn.federative_registration_complete
                disableForm(new String[] {});
                //infoMessage(RegistrationPage.REGISTRATION_COMPLETE, appUser.getEmail());
                
                
                // logging for statistics
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGIN);

                // logging for statistics
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_REGISTRATION);
                setResponsePage(new InfoPage(getString("authn.validation_succesful.infoPageTitle")));
            }
            else
            {
                for (String stateKey : registration.getAccumulatedStateKeys())
                {
                    final String message = errorMessage(stateKey);
                    logger.error(message);
                }
                // NOW WHAT?
            }
            logger.debug("End onSubmit: " + registration.toString());
            
        }

    }
}
