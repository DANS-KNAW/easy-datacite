package nl.knaw.dans.easy.web.admin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.TelephoneNumberValidator;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.wicket.BootstrapCheckboxListPanel;
import nl.knaw.dans.easy.web.wicket.FormListener;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDetailsEditPanel extends AbstractEasyPanel implements EasyResources {
    private static Logger logger = LoggerFactory.getLogger(UserDetailsEditPanel.class);

    private static final String WI_USER_INFO_FORM = "userInfoForm";

    private static final long serialVersionUID = 3677596835048406356L;

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;

    @SpringBean(name = "userService")
    UserService userService;

    public UserDetailsEditPanel(final SwitchPanel parent, final IModel<EasyUser> model, final boolean enableModeSwitch, final FormListener listener) {
        super(SwitchPanel.SWITCH_PANEL_WI, model);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        constructPanel(listener);
    }

    private void constructPanel(final FormListener listener) {
        UserInfoForm infoForm = new UserInfoForm(this, WI_USER_INFO_FORM, (IModel<EasyUser>) getDefaultModel(), listener);
        add(infoForm);
        // AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    private List<String> getGroupIds() {
        List<String> groups = null;
        try {
            groups = userService.getAllGroupIds();
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.ERROR_IN_GETTING_GROUPS);
            logger.error(message, e);
            throw new InternalWebError();
        }
        return groups;
    }

    private static class UserInfoForm extends AbstractEasyStatelessForm<EasyUser> {
        private final UserDetailsEditPanel userDetailsEditPanel;
        private final FormListener listener;
        private final boolean addMode;
        private static final String ADD_BUTTON_LABEL = "addButtonLabel";
        private static final String UPDATE_BUTTON_LABEL = "updateButtonLabel";

        private static final long serialVersionUID = -2318428687360947765L;

        public UserInfoForm(UserDetailsEditPanel userDetailsEditPanel, final String wicketId, final IModel<EasyUser> model, final FormListener listener) {
            super(wicketId, model);
            this.userDetailsEditPanel = userDetailsEditPanel;
            this.listener = listener;

            EasyUser displayedUser = (EasyUser) model.getObject();
            addMode = displayedUser.getId() == null;

            addCommonFeedbackPanel();

            Label userIdLabel = new Label("userIdLabel", new PropertyModel<String>(displayedUser, UserProperties.USER_ID));
            userIdLabel.setVisible(!addMode);
            add(userIdLabel);

            Label displayNameLabel = new Label(UserProperties.DISPLAYNAME);
            displayNameLabel.setVisible(!addMode);
            add(displayNameLabel);

            RequiredTextField<String> userIdTextField = new RequiredTextField<String>(UserProperties.USER_ID);
            userIdTextField.add(StringValidator.minimumLength(UserProperties.MINIMUM_USER_ID_LENGTH));
            userIdTextField.setVisible(addMode);
            addWithComponentFeedback(userIdTextField, new ResourceModel("user.userid"));

            addWithComponentFeedback(new TextField<String>(UserProperties.TITLE), new ResourceModel("user.title"));

            addWithComponentFeedback(new RequiredTextField<String>(UserProperties.INITIALS), new ResourceModel("user.initials"));

            addWithComponentFeedback(new TextField<String>(UserProperties.PREFIXES), new ResourceModel("user.prefixes"));

            addWithComponentFeedback(new RequiredTextField<String>(UserProperties.SURNAME), new ResourceModel("user.surname"));

            addWithComponentFeedback(new TextField<String>(UserProperties.ORGANIZATION), new ResourceModel("user.organization"));

            addWithComponentFeedback(new TextField<String>(UserProperties.DEPARTMENT), new ResourceModel("user.department"));

            addWithComponentFeedback(new TextField<String>(UserProperties.FUNCTION), new ResourceModel("user.function"));

            addWithComponentFeedback(new RequiredTextField<String>(UserProperties.ADDRESS), new ResourceModel("user.address"));

            addWithComponentFeedback(new RequiredTextField<String>(UserProperties.POSTALCODE), new ResourceModel("user.postalCode"));

            addWithComponentFeedback(new RequiredTextField<String>(UserProperties.CITY), new ResourceModel("user.city"));

            addWithComponentFeedback(new TextField<String>(UserProperties.COUNTRY), new ResourceModel("user.country"));

            addWithComponentFeedback(new TextField<String>(UserProperties.DAI).add(new PatternValidator(Pattern.compile("\\d{8}[A-Z0-9]"))), new ResourceModel(
                    "user.dai"));

            FormComponent email = new RequiredTextField<String>(UserProperties.EMAIL);
            addWithComponentFeedback(email.add(EmailAddressValidator.getInstance()), new ResourceModel("user.email"));

            ApplicationUser proxy = new ApplicationUser(displayedUser);
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE1, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE1),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE2, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE2),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));
            add(new DropDownChoice<KeyValuePair>(UserProperties.DISCIPLINE3, new PropertyModel<KeyValuePair>(proxy, UserProperties.DISCIPLINE3),
                    DisciplineUtils.getDisciplinesChoiceList().getChoices(), new KvpChoiceRenderer()).setNullValid(true));

            FormComponent telephone = new TextField<String>(UserProperties.TELEPHONE);
            telephone.add(TelephoneNumberValidator.instance());
            addWithComponentFeedback(telephone, new ResourceModel("user.telephone"));

            // inform by email newsletter selection (Yes/No radio buttons)
            RadioGroup informByEmailSelection = new RadioGroup(UserProperties.OPTS_FOR_NEWSLETTER);
            informByEmailSelection.add(new Radio("news-yes", new Model(true)));
            informByEmailSelection.add(new Radio("news-no", new Model(false)));
            add(informByEmailSelection);

            RadioGroup swordDepositAllowedSelection = new RadioGroup(UserProperties.SWORD_DEPOSIT_ALLOWED);
            swordDepositAllowedSelection.add(new Radio("sword-yes", new Model(true)));
            swordDepositAllowedSelection.add(new Radio("sword-no", new Model(false)));
            add(swordDepositAllowedSelection);

            RadioGroup logMyActionsSelection = new RadioGroup(ApplicationUser.LOG_MY_ACTIONS);
            logMyActionsSelection.add(new Radio<Boolean>("log-yes", new Model<Boolean>(true)));
            logMyActionsSelection.add(new Radio<Boolean>("log-no", new Model<Boolean>(false)));
            add(logMyActionsSelection);

            List<EasyUser.State> states = Arrays.asList(EasyUser.State.values());
            DropDownChoice stateChoice = new DropDownChoice(UserProperties.STATE, states);
            add(stateChoice, new ResourceModel("user.state"));

            List<EasyUser.Role> roles = Arrays.asList(EasyUser.Role.values());
            // Use the name 'roles' instead of 'roleChecksPanel' because of 'CodedAuthz.java' AspectJ security issues.
            add(new BootstrapCheckboxListPanel("roles", new PropertyModel<EasyUser>(displayedUser, UserProperties.ROLES), roles));

            List<String> groupIds = this.userDetailsEditPanel.getGroupIds();
            add(new BootstrapCheckboxListPanel("groupChecksPanel", new PropertyModel<EasyUser>(displayedUser, UserProperties.GROUP_IDS), groupIds));

            final String buttonLabel = addMode ? ADD_BUTTON_LABEL : UPDATE_BUTTON_LABEL;
            SubmitLink updateButton = new SubmitLink(UserDetailsEditPanel.UPDATE_BUTTON, new ResourceModel(buttonLabel));
            add(updateButton);

            Link cancelButton = new Link(UserDetailsEditPanel.CANCEL_BUTTON) {

                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onClick() {
                    handleCancelButtonClicked();
                }
            };
            add(cancelButton);

        }

        @Override
        protected void onSubmit() {
            if (addMode) {
                handleAddButtonClicked();
            } else {
                handleUpdateButtonClicked();
            }
        }

        private void handleAddButtonClicked() {
            // final User user = (User) getModelObject();
            final String message = errorMessage(EasyResources.ADD_NOT_IMPLEMENTED);
            logger.error(message);
            throw new InternalWebError();
        }

        private void handleUpdateButtonClicked() {
            final EasyUser user = getModelObject();

            try {
                EasyUser sessionUser = getSessionUser();

                // update the user in persistence layer
                this.userDetailsEditPanel.userService.update(sessionUser, user);

                // inform listener
                if (listener != null) {
                    listener.onUpdate(this, user);
                }

                // The user we got back from the modelObject is not the same object as the
                // one we put in the CompoundPropertyModel (see constructor).
                // If the sessionUser is updating her own info we need to synchronize
                // the sessionUser on the updated user.
                if (sessionUser.getId().equals(user.getId())) {
                    sessionUser.synchronizeOn(user);
                    logger.debug("Session user updated. Synchronizing " + sessionUser + " on " + user);
                }
                if (this.userDetailsEditPanel.enableModeSwitch) {
                    this.userDetailsEditPanel.parent.switchMode();
                }

                final String message = infoMessage(EasyResources.SUCCESFUL_UPDATE);
                logger.info(message);
            }
            catch (ServiceException e) {
                final String message = fatalMessage(EasyResources.USER_UPDATE_ERROR);
                logger.error(message, e);
            }
        }

        private void handleCancelButtonClicked() {
            if (this.userDetailsEditPanel.enableModeSwitch && !addMode) {
                this.userDetailsEditPanel.parent.switchMode();
            } else {
                setResponsePage(UsersOverviewPage.class);
            }
        }

    }
}
