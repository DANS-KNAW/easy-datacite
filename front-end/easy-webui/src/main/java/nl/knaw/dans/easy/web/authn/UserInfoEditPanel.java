package nl.knaw.dans.easy.web.authn;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.TelephoneNumberValidator;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
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

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoEditPanel extends AbstractEasyStatelessPanel implements EasyResources {

    private static final String WI_USER_INFO_FORM = "userInfoForm";

    private static final long serialVersionUID = 2798115070952029278L;

    private static Logger logger = LoggerFactory.getLogger(UserInfoEditPanel.class);

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;
    private boolean hasPassword = false;

    @SpringBean(name = "userService")
    private UserService userService;

    @SpringBean(name = "federativeUserRepo")
    private FederativeUserRepo federativeUserRepo;

    public UserInfoEditPanel(final SwitchPanel parent, final String userId, final boolean enableModeSwitch) {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        init(userId);
    }

    private void init(final String userId) {
        EasyUser user = null;
        try {
            user = userService.getUserById(getSessionUser(), userId);
        }
        catch (ServiceException e) {
            final String message = errorMessage(USER_NOT_FOUND, userId);
            logger.error(message, e);
        }

        if (user == null) {
            throw new RestartResponseException(new ErrorPage());
        }

        // check if user has a password, federative users might not have it.
        try {
            hasPassword = userService.isUserWithStoredPassword(user);
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }

        constructPanel(user);
    }

    private void constructPanel(final EasyUser user) {
        UserInfoForm infoForm = new UserInfoForm(WI_USER_INFO_FORM, user);
        add(infoForm);
        // AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    class UserInfoForm extends AbstractEasyStatelessForm {
        private static final long serialVersionUID = 6429049682947798419L;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public UserInfoForm(final String wicketId, final EasyUser user) {
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
            add(createDisiplineDropDown(proxy, UserProperties.DISCIPLINE1));
            add(createDisiplineDropDown(proxy, UserProperties.DISCIPLINE2));
            add(createDisiplineDropDown(proxy, UserProperties.DISCIPLINE3));

            addWithComponentFeedback(new RequiredTextField(UserProperties.ADDRESS), new ResourceModel("user.address"));
            addWithComponentFeedback(new RequiredTextField(UserProperties.POSTALCODE), new ResourceModel("user.postalCode"));
            addWithComponentFeedback(new RequiredTextField(UserProperties.CITY), new ResourceModel("user.city"));
            addWithComponentFeedback(new TextField(UserProperties.COUNTRY), new ResourceModel("user.country"));

            addWithComponentFeedback(createEmailField(), new ResourceModel("user.email"));
            addWithComponentFeedback(createTelephoneField(), new ResourceModel("user.telephone"));
            addWithComponentFeedback(createDaiField(), new ResourceModel(RegistrationPage.USER_DAI));

            // inform by email newsletter selection (Yes/No radio buttons)
            String optsForNewsletter = UserProperties.OPTS_FOR_NEWSLETTER;
            RadioGroup informByEmailSelection = new RadioGroup(optsForNewsletter);
            informByEmailSelection.add(new Radio("news-yes", new Model(true)));
            informByEmailSelection.add(new Radio("news-no", new Model(false)));
            add(informByEmailSelection);

            String logMyActions = ApplicationUser.LOG_MY_ACTIONS;
            RadioGroup logMyActionsSelection = new RadioGroup(logMyActions);
            logMyActionsSelection.add(new Radio<Boolean>("log-yes", new Model<Boolean>(true)));
            logMyActionsSelection.add(new Radio<Boolean>("log-no", new Model<Boolean>(false)));
            add(logMyActionsSelection);

            add(new SubmitLink(UPDATE_BUTTON));
            add(createCancelButton());
        }

        private FormComponent createEmailField() {
            return new RequiredTextField(UserProperties.EMAIL).add(EmailAddressValidator.getInstance());
        }

        private FormComponent createTelephoneField() {
            return new TextField(UserProperties.TELEPHONE).add(TelephoneNumberValidator.instance());
        }

        private FormComponent<String> createDaiField() {
            return new TextField<String>(ApplicationUser.DAI) {
                private static final long serialVersionUID = 1L;

                protected boolean shouldTrimInput() {
                    return true;
                };
            }.add(DAIValidator.instance());
        }

        private AbstractSingleSelectChoice<KeyValuePair> createDisiplineDropDown(ApplicationUser proxy, String discipline) {
            PropertyModel<KeyValuePair> propertyModel = new PropertyModel<KeyValuePair>(proxy, discipline);
            List<KeyValuePair> choices = DisciplineUtils.getDisciplinesChoiceList().getChoices();
            return new DropDownChoice<KeyValuePair>(discipline, propertyModel, choices, new KvpChoiceRenderer()).setNullValid(true);
        }

        private List<FederativeUserIdMap> getLinkedFederationAccounts(EasyUser user) {
            try {
                return federativeUserRepo.findByDansUserId(user.getId().toString());
            }
            catch (RepositoryException e) {
                logger.error(errorMessage(EasyResources.INTERNAL_ERROR), e);
                throw new InternalWebError();
            }
        }

        private Component createCancelButton() {
            return new Link<String>(CANCEL_BUTTON) {
                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onClick() {
                    handleCancelButtonClicked();
                }
            };
        }

        @Override
        protected void onSubmit() {
            handleUpdateButtonClicked();
        }

        private void handleDeleteInstitutionAccountButtonClicked(final List<FederativeUserIdMap> list) {
            try {
                for (FederativeUserIdMap idMap : list)
                    federativeUserRepo.delete(idMap);
            }
            catch (RepositoryException e) {
                logger.error(errorMessage(EasyResources.INTERNAL_ERROR), e);
                throw new InternalWebError();
            }
        }

        private void handleUpdateButtonClicked() {
            final EasyUser user = (EasyUser) getModelObject();
            try {
                EasyUser sessionUser = getSessionUser();
                boolean firstLogin = sessionUser.isFirstLogin();

                // update the user in persistence layer
                Services.getUserService().update(sessionUser, user);

                // The user we got back from the modelObject is not the same object as the
                // one we put in the CompoundPropertyModel (see constructor).
                // If the sessionUser is updating her own info we need to synchronize
                // the sessionUser on the updated user.
                if (sessionUser.getId().equals(user.getId())) {
                    sessionUser.synchronizeOn(user);
                    logger.debug("Session user updated. Synchronizing " + sessionUser + " on " + user);
                }

                if (firstLogin) {
                    if (!getPage().continueToOriginalDestination()) {
                        setResponsePage(this.getApplication().getHomePage());
                    }
                }

                if (enableModeSwitch) {
                    parent.switchMode();
                }

                final String message = infoMessage(SUCCESFUL_UPDATE);
                logger.info(message);
            }
            catch (ServiceException e) {
                final String message = fatalMessage(USER_UPDATE_FAILED);
                logger.error(message, e);
            }
        }

        private void handleCancelButtonClicked() {
            if (enableModeSwitch) {
                parent.switchMode();
            } else {
                setResponsePage(HomePage.class);
            }
        }

    }

}
