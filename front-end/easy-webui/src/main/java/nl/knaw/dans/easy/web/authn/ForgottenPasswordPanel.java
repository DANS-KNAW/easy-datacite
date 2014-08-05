package nl.knaw.dans.easy.web.authn;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.common.wicket.util.RequireExactlyOneValidator;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessForm;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgottenPasswordPanel extends AbstractEasyStatelessPanel implements EasyResources
{
    private static final String WI_FORGOTTEN_PASSWORD_FORM = "forgottenPasswordForm";

    private static final String WI_RANDOM_TOKEN = "token";
    private static final String LABEL_USERID = "user.userId";
    private static final String LABEL_EMAIL = "user.email";

    private static final String USERID = "userId";
    private static final String EMAIL = "email";

    private static final String INFO_PAGE = "forgottenPassword.infoPage";

    @SpringBean(name = "userService")
    private UserService userService;

    /**
     * 
     */
    private static final long serialVersionUID = -2112347315861906706L;

    private static Logger logger = LoggerFactory.getLogger(ForgottenPasswordPanel.class);

    public ForgottenPasswordPanel(String wicketId)
    {
        super(wicketId);
        ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();

        add(new ForgottenPasswordForm(WI_FORGOTTEN_PASSWORD_FORM, messenger));
    }

    private class ForgottenPasswordForm extends AbstractEasyStatelessForm
    {
        private static final long serialVersionUID = -1516859515594272714L;

        private final String randomString;

        public ForgottenPasswordForm(String wicketId, ForgottenPasswordMessenger messenger)
        {
            super(wicketId, new CompoundPropertyModel(messenger));
            this.randomString = messenger.getMailToken();
            addCommonFeedbackPanel();

            // TODO set token on ForgottenPasswordService

            add(new HiddenField(WI_RANDOM_TOKEN, new Model(messenger.getRandomString())));

            // userId
            FormComponent userId = new TextField(USERID);
            addWithComponentFeedback(userId, new ResourceModel(LABEL_USERID));

            // email
            FormComponent email = new TextField(EMAIL);
            email.add(EmailAddressValidator.getInstance());
            addWithComponentFeedback(email, new ResourceModel(LABEL_EMAIL));

            add(new RequireExactlyOneValidator(userId, email));

            add(new SubmitLink(REQUEST_BUTTON));

            Link cancelButton = new Link(CANCEL_BUTTON)
            {

                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onClick()
                {
                    setResponsePage(HomePage.class);
                }
            };
            add(cancelButton);
        }

        @Override
        protected void onSubmit()
        {
            handleRequestButtonClicked();
        }

        private void handleRequestButtonClicked()
        {
            // Check for a valid token
            if (randomString == null)
            {
                errorMessage(EasyResources.FORM_INVALID_PARAMETERS);
                logger.warn(getString("password form is submitted without a valid token"));
                return;
            }
            final ForgottenPasswordMessenger messenger = (ForgottenPasswordMessenger) getModelObject();
            if (!this.randomString.equals(messenger.getMailToken()))
            {
                errorMessage(EasyResources.FORM_INVALID_PARAMETERS);
                logger.warn("password form is submitted with an invalid token. Expected: " + this.randomString + ", got " + messenger.getMailToken());
                return; // NOPMD
            }

            // for updating with url, we need extra data
            Map<String, String> paras = new HashMap<String, String>();
            paras.put(ChangePasswordPage.PM_REQUEST_TIME, messenger.getRequestTimeAsString());
            paras.put(ChangePasswordPage.PM_REQUEST_TOKEN, messenger.getMailToken());
            messenger.setUserIdParamKey(ChangePasswordPage.PM_USER_ID);
            final String updateURL = createPageURL(ChangePasswordPage.class, paras);
            messenger.setUpdateURL(updateURL);
            //

            try
            {
                userService.handleForgottenPasswordRequest(messenger);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new InternalWebError();
            }

            if (messenger.isCompleted())
            {
                for (EasyUser user : messenger.getUsers())
                {
                    info(getString(messenger.getStateKey(), new Model(user)));
                }
                this.disableForm(new String[] {});
                final String title = getString(INFO_PAGE);
                setResponsePage(new InfoPage(title));
            }
            else
            {
                final String message = errorMessage("state." + messenger.getState());
                logger.warn(message);
            }
        }

    }

}
