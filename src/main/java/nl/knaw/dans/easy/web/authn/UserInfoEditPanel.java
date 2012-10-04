package nl.knaw.dans.easy.web.authn;

import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.TelephoneNumberValidator;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;
import nl.knaw.dans.easy.web.wicketutil.DAIValidator;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoEditPanel extends AbstractEasyStatelessPanel implements EasyResources
{

    private static final String WI_USER_INFO_FORM = "userInfoForm";

    private static final long serialVersionUID = 2798115070952029278L;

    private static Logger logger = LoggerFactory.getLogger(UserInfoEditPanel.class);

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;
    private boolean hasPassword = false;

    public UserInfoEditPanel(final SwitchPanel parent, final String userId, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        init(userId);
    }

    private void init(final String userId)
    {
        EasyUser user = null;
        try
        {
            user = Services.getUserService().getUserById(getSessionUser(), userId);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(USER_NOT_FOUND, userId);
            logger.error(message, e);
        }

        if (user == null)
        {
            throw new RestartResponseException(new ErrorPage());
        }
        
        // check if user has a password, federative users might not have it.
        try
        {
            hasPassword = Services.getUserService().isUserWithStoredPassword(user);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
        
        constructPanel(user);
    }

    private void constructPanel(final EasyUser user)
    {
        UserInfoForm infoForm = new UserInfoForm(WI_USER_INFO_FORM, user);
        add(infoForm);
        // AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    class UserInfoForm extends AbstractEasyStatelessForm
    {
        private static final long serialVersionUID = 6429049682947798419L;

        @SuppressWarnings({"unchecked", "serial", "rawtypes"})
        public UserInfoForm(final String wicketId, final EasyUser user)
        {
            super(wicketId, new CompoundPropertyModel(user));

            addCommonFeedbackPanel();

            add(new Label(UserProperties.USER_ID).setVisible(hasPassword));

            addWithComponentFeedback(new TextField(UserProperties.TITLE), new ResourceModel("user.title"));

            addWithComponentFeedback(new RequiredTextField(UserProperties.INITIALS), new ResourceModel("user.initials"));

            addWithComponentFeedback(new TextField(UserProperties.PREFIXES), new ResourceModel("user.prefixes"));

            addWithComponentFeedback(new RequiredTextField(UserProperties.SURNAME), new ResourceModel("user.surname"));

            add(new Label(UserProperties.DISPLAYNAME));

            addWithComponentFeedback(new TextField(UserProperties.ORGANIZATION), new ResourceModel("user.organization"));

            addWithComponentFeedback(new TextField(UserProperties.DEPARTMENT), new ResourceModel("user.department"));

            addWithComponentFeedback(new TextField(UserProperties.FUNCTION), new ResourceModel("user.function"));

            ApplicationUser proxy = new ApplicationUser(user);
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE1, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE1),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE2, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE2),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE3, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE3),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));

            addWithComponentFeedback(new RequiredTextField(UserProperties.ADDRESS), new ResourceModel("user.address"));

            addWithComponentFeedback(new RequiredTextField(UserProperties.POSTALCODE), new ResourceModel("user.postalCode"));

            addWithComponentFeedback(new RequiredTextField(UserProperties.CITY), new ResourceModel("user.city"));

            addWithComponentFeedback(new TextField(UserProperties.COUNTRY), new ResourceModel("user.country"));

            FormComponent email = new RequiredTextField(UserProperties.EMAIL);
            addWithComponentFeedback(email.add(EmailAddressValidator.getInstance()), new ResourceModel("user.email"));

            FormComponent telephone = new TextField(UserProperties.TELEPHONE);
            telephone.add(TelephoneNumberValidator.instance());
            addWithComponentFeedback(telephone, new ResourceModel("user.telephone"));

            addWithComponentFeedback(new TextField<String>(ApplicationUser.DAI)
                    {
                        protected boolean shouldTrimInput() {
                            return true;
                        };
                    }.add(DAIValidator.instance()), new ResourceModel(
                    RegistrationPage.USER_DAI));

            // inform by email newsletter selection (Yes/No radio buttons)
            RadioGroup informByEmailSelection = new RadioGroup(UserProperties.OPTS_FOR_NEWSLETTER);
            informByEmailSelection.add(new Radio("news-yes", new Model(true)));
            informByEmailSelection.add(new Radio("news-no", new Model(false)));
            add(informByEmailSelection);

            RadioGroup logMyActionsSelection = new RadioGroup(ApplicationUser.LOG_MY_ACTIONS);
            logMyActionsSelection.add(new Radio<Boolean>("log-yes", new Model<Boolean>(true)));
            logMyActionsSelection.add(new Radio<Boolean>("log-no", new Model<Boolean>(false)));
            add(logMyActionsSelection);

            SubmitLink updateButton = new SubmitLink(UPDATE_BUTTON);
            add(updateButton);

            Link cancelButton = new Link(CANCEL_BUTTON)
            {

                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onClick()
                {
                    handleCancelButtonClicked();
                }
            };
            add(cancelButton);
        }

        private ChoiceList getDisciplinesChoiceList()
        {
            try
            {
                return Services.getDepositService().getChoices("custom.disciplines", null);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new InternalWebError();
            }
        }

        @Override
        protected void onSubmit()
        {
            handleUpdateButtonClicked();
        }

        private void handleUpdateButtonClicked()
        {
            final EasyUser user = (EasyUser) getModelObject();
            try
            {
                EasyUser sessionUser = getSessionUser();
                boolean firstLogin = sessionUser.isFirstLogin();

                // update the user in persistence layer
                Services.getUserService().update(sessionUser, user);

                // The user we got back from the modelObject is not the same object as the
                // one we put in the CompoundPropertyModel (see constructor).
                // If the sessionUser is updating her own info we need to synchronize
                // the sessionUser on the updated user.
                if (sessionUser.getId().equals(user.getId()))
                {
                    sessionUser.synchronizeOn(user);
                    logger.debug("Session user updated. Synchronizing " + sessionUser + " on " + user);
                }

                if (firstLogin)
                {
                    if (!getPage().continueToOriginalDestination())
                    {
                        setResponsePage(this.getApplication().getHomePage());
                    }
                }

                if (enableModeSwitch)
                {
                    parent.switchMode();
                }

                final String message = infoMessage(SUCCESFUL_UPDATE);
                logger.info(message);
            }
            catch (ServiceException e)
            {
                final String message = fatalMessage(USER_UPDATE_FAILED);
                logger.error(message, e);
            }
        }

        private void handleCancelButtonClicked()
        {
            if (enableModeSwitch)
            {
                parent.switchMode();
            }
            else
            {
                setResponsePage(HomePage.class);
            }
        }

    }

}
