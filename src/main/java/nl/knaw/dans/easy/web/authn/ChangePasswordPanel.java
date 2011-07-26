package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.ExcludeMessageFilter;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.UnEqualInputValidator;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.util.SecurityUtil;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicketutil.PasswordPolicyValidator;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordPanel extends AbstractEasyStatelessPanel implements EasyResources
{
    private static final String WI_CHANGE_PASSWORD_FORM = "changePasswordForm";

    private static final String WI_RANDOM_TOKEN         = "token";
    private static final String WI_USER_ID         		= "user.userId";
    private static final String LABEL_OLD_PASSWORD      = "label.oldPassword";
    private static final String LABEL_NEW_PASSWORD      = "label.newPassword";
    private static final String LABEL_CONFIRM_PASSWORD  = "label.confirmPassword";

    private static final String OLD_PASSWORD            = "oldPassword";
    private static final String NEW_PASSWORD            = "newPassword";
    private static final String CONFIRM_PASSWORD        = "confirmPassword";

    private static final String INFO_PAGE				 = "changePassword.infoPage";

    private static Logger       logger                  = LoggerFactory.getLogger(ChangePasswordPanel.class);

    private static final long   serialVersionUID        = 6320109414610346669L;

	private ChangePasswordForm	changePasswordForm;

    public ChangePasswordPanel(String wicketId, ChangePasswordMessenger messenger)
    {
        super(wicketId);
        
        add(new Label(WI_USER_ID, new StringResourceModel("label.userId", new Model<ChangePasswordMessenger>(messenger))));
        
        changePasswordForm = new ChangePasswordForm(WI_CHANGE_PASSWORD_FORM, messenger);
		add(changePasswordForm);

        addCommonFeedbackPanel(new ExcludeMessageFilter(changePasswordForm));
    }

    private class ChangePasswordForm extends AbstractEasyStatelessForm
    {
		private static final long serialVersionUID = 6204591036947047986L;

        /**
         * RandomString used as token against XSS attacks.
         */
        private final String      randomString;

        public ChangePasswordForm(String wicketId, ChangePasswordMessenger messenger)
        {
            super(wicketId, new CompoundPropertyModel<ChangePasswordMessenger>(messenger));
            
            this.randomString = SecurityUtil.getRandomString();
            messenger.setToken(this.randomString);

            add(new HiddenField<String>(WI_RANDOM_TOKEN, new Model<String>(this.randomString)));
            
            // old password
            FormComponent<String> oldPassword = new PasswordTextField(OLD_PASSWORD).setRequired(true);
            addWithComponentFeedback(oldPassword, new ResourceModel(LABEL_OLD_PASSWORD));
            oldPassword.setVisible(!messenger.isMailContext());

            // new password
            FormComponent<String> password = new PasswordTextField(NEW_PASSWORD).setRequired(true);
            password.add(PasswordPolicyValidator.getInstance());
            addWithComponentFeedback(password, new ResourceModel(LABEL_NEW_PASSWORD));

            // Confirm new password
            FormComponent<String> confirmPassword = new PasswordTextField(CONFIRM_PASSWORD).setRequired(true);
            addWithComponentFeedback(confirmPassword, new ResourceModel(LABEL_CONFIRM_PASSWORD));

            // Validator for equal passwords
            add(new EqualPasswordInputValidator(password, confirmPassword));

            if (!messenger.isMailContext()) // no oldPassword on mail form variant
            {
                // Validator for unequal passwords. checks if old and new password are by incident identical.
                add(new UnEqualInputValidator(oldPassword, password));
            }

            add(new SubmitLink(UPDATE_BUTTON));

            Link cancelButton = new Link(CANCEL_BUTTON)
            {

                private static final long serialVersionUID = 8826482066530609209L;

                @Override
                public void onClick()
                {
                    handleCancelButtonClicked();
                }
            };
            add(cancelButton);

        }

        @Override
        protected void onSubmit()
        {
        	handleUpdateButtonClicked();
        }

       private void handleUpdateButtonClicked()
        {
            // Check for a valid token
            if (randomString == null)
            {
                errorMessage(EasyResources.FORM_INVALID_PARAMETERS);
            	logger.warn("password form is submitted without a valid token");
                return;
            }

            final ChangePasswordMessenger messenger = (ChangePasswordMessenger) getModelObject();
            if (!this.randomString.equals(messenger.getToken()))
            {
                errorMessage(EasyResources.FORM_INVALID_PARAMETERS);
                logger.warn("password form is submitted with an invalid token. Expected: " + this.randomString + ", got " + messenger.getToken());
                return;
            }

            try
            {
                Services.getUserService().changePassword(messenger);
            }
            catch(ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
    	        throw new InternalWebError();
            }
            if (messenger.isCompleted())
            {
                disableForm(new String[] {});
                final String message = infoMessage(EasyResources.PASSWORD_SUCCESFULLY_CHANGED);
            	logger.info(message);
                final String title = getString(INFO_PAGE);
                setResponsePage(new InfoPage(title));
            }
            else
            {
            	final String message = errorMessage("state." + messenger.getState());
            	logger.warn(message);
                return;
            }
        }

        private void handleCancelButtonClicked()
        {
            setResponsePage(new UserInfoPage(false, true));
        }

    }

}
