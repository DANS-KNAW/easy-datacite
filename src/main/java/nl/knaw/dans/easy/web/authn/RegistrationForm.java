package nl.knaw.dans.easy.web.authn;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.LettersAndDigitsValidator;
import nl.knaw.dans.common.wicket.util.TelephoneNumberValidator;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.easy.web.wicketutil.DAIValidator;
import nl.knaw.dans.easy.web.wicketutil.PasswordPolicyValidator;

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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationForm extends AbstractEasyStatelessForm<ApplicationUser>
{
    private static final long serialVersionUID = 3036525128056985280L;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationPage.class);
    private static final String INFO_PAGE = "registrationpage.header";
    private static final String WI_REGISTER = "register";

    private final SubmitLink registerLink = new SubmitLink(WI_REGISTER);

    private String paramUserId;
    private String paramDateTime;
    private String paramToken;

    private String federationUserId;
    private String federationUserDescription;
    private String institute;
    private String easyUserId;

    public RegistrationForm(final String wicketId)
    {
        this(wicketId, new ApplicationUser(), null, null, null);
    }

    public RegistrationForm(final String wicketId, final ApplicationUser appUser, final String federationUserId, String federationUserDescription,
            String institute)
    {
        super(wicketId, new CompoundPropertyModel<ApplicationUser>(appUser));
        this.federationUserId = federationUserId;
        this.federationUserDescription = federationUserDescription;
        this.institute = institute;
        addCommonFeedbackPanel();

        RequiredTextField<String> userIdTextField = new RequiredTextField<String>(ApplicationUser.USER_ID);
        userIdTextField.add(StringValidator.minimumLength(UserProperties.MINIMUM_USER_ID_LENGTH));
        userIdTextField.add(LettersAndDigitsValidator.instance());
        addWithComponentFeedback(userIdTextField, new ResourceModel(RegistrationPage.USER_USER_ID));

        // Add Password
        PasswordTextField password = new PasswordTextField(ApplicationUser.PASSWORD);
        addWithComponentFeedback(password, new ResourceModel(RegistrationPage.USER_PASSWORD));
        password.setRequired(true);
        password.setResetPassword(false);
        password.add(PasswordPolicyValidator.getInstance());

        // Add confirm password
        PasswordTextField confirmPassword = new PasswordTextField(ApplicationUser.CONFIRM_PASSWORD);
        addWithComponentFeedback(confirmPassword, new ResourceModel(RegistrationPage.USER_CONFIRM_PASSWORD));
        confirmPassword.setRequired(true);
        confirmPassword.setResetPassword(false);

        // Validator for equal passwords
        add(new EqualPasswordInputValidator(password, confirmPassword));

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
        TextField<String> userTelephoneTextField = new TextField<String>(ApplicationUser.TELEPHONE);
        userTelephoneTextField.add(TelephoneNumberValidator.instance());
        addWithComponentFeedback(userTelephoneTextField, new ResourceModel(RegistrationPage.USER_TELEPHONE));

        add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE1, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE1),
                DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));
        add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE2, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE2),
                DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));
        add(new DropDownChoice<KeyValuePair>(ApplicationUser.DISCIPLINE3, new PropertyModel<KeyValuePair>(appUser, ApplicationUser.DISCIPLINE3),
                DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()));

        addWithComponentFeedback(new TextField<String>(ApplicationUser.DAI)
        {

            private static final long serialVersionUID = 1L;

            protected boolean shouldTrimInput()
            {
                return true;
            };

        }.add(DAIValidator.instance()), new ResourceModel(RegistrationPage.USER_DAI));
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

        add(new EasyEditablePanel("editablePanel", "/pages/Registration.template"));

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

        Registration registration = new Registration(appUser.getBusinessUser());
        paramUserId = registration.getUserId();
        paramDateTime = registration.getRequestTimeAsString();
        paramToken = registration.getMailToken();
        // Make a map of parameters
        Map<String, String> parameterMap = createParameterMap(RegistrationValidationPage.PARAM_NAME_USERID, this.paramUserId,
                RegistrationValidationPage.PARAM_NAME_DATE_TIME, this.paramDateTime, RegistrationValidationPage.PARAM_NAME_TOKEN, this.paramToken);

        // Make the url
        final String validationUrl = createPageURL(RegistrationValidationPage.class, parameterMap);
        registration.setValidationUrl(validationUrl);

        // do all the work: read the returned registration object. Tests can be swap registrations.
        try
        {
            registration = Services.getUserService().handleRegistrationRequest(registration);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            RegistrationForm.logger.error(message, e);
            throw new InternalWebError();
        }

        if (registration.isCompleted())
        {
            disableForm(new String[] {});
            infoMessage(RegistrationPage.REGISTRATION_COMPLETE, appUser.getEmail());

            if (federationUserId != null)
            {
                easyUserId = registration.getUserId();
                createLinkBetweenCurrentEasyUserAndFederationUser();
                infoMessage("register-and-link.link-created", federationUserDescription, institute, easyUserId);
            }

            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_REGISTRATION);
            setResponsePage(new InfoPage(getString(RegistrationForm.INFO_PAGE)));
        }
        else
        {
            for (String stateKey : registration.getAccumulatedStateKeys())
            {
                final String message = errorMessage(stateKey);
                RegistrationForm.logger.error(message);
            }
        }
        RegistrationForm.logger.debug("End onSubmit: " + registration.toString());
    }

    private void createLinkBetweenCurrentEasyUserAndFederationUser()
    {
        try
        {
            Services.getFederativeUserService().addFedUserToEasyUserIdCoupling(federationUserId, easyUserId);
        }
        catch (ServiceException e)
        {
            final String message = warningMessage("register-and-link.link-not-created");
            logger.warn(message, e);
        }
    }

    /**
     * Create a parameterMap for given parameters to a page accessible with a token.
     * 
     * @param paramNameUserId
     *        name of the parameter for userId
     * @param paramUserId
     *        user id
     * @param paramNameDateTime
     *        name of the parameter for dateTime
     * @param paramDateTime
     *        date time of the request
     * @param paramNameToken
     *        name of the parameter for the token
     * @param paramToken
     *        token
     * @return parameterMap
     */
    protected Map<String, String> createParameterMap(final String paramNameUserId, final String paramUserId, final String paramNameDateTime,
            final String paramDateTime, final String paramNameToken, final String paramToken)
    {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(paramNameUserId, paramUserId);
        parameterMap.put(paramNameDateTime, paramDateTime);
        parameterMap.put(paramNameToken, paramToken);
        return parameterMap;
    }

}
