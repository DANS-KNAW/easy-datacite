package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.easy.web.authn.ForgottenPasswordPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoginPanelRegular extends AbstractEasyStatelessPanel {
    private static Logger logger = LoggerFactory.getLogger(LoginPanelRegular.class);

    /**
     * Component wicket id.
     */
    public static final String FORGOTTEN_PASSWORD = "forgottenPassword"; // NOPMD: name is not too long.

    /**
     * Login form wicket id.
     */
    public static final String LOGIN_FORM = "loginForm";

    /**
     * Variation constant for inline display.
     */
    public static final String INLINE = "inline";

    /**
     * Logger for this class.
     */

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 863711471061407764L;

    /**
     * Default constructor for full layout.
     * 
     * @param wicketId
     *        The wicket id for this component.
     */
    public LoginPanelRegular(final String wicketId, LoginForm loginForm) {
        super(wicketId);
        add(loginForm);
        addForgottenPasswordLink();
    }

    private void addForgottenPasswordLink() {
        add(new Link<Void>(FORGOTTEN_PASSWORD) {
            @Override
            public void onClick() {
                logger.debug("Forgotten password link clicked");
                setResponsePage(ForgottenPasswordPage.class); // TODO Auto-generated method stub
            }

            /**
             * Serial version uid.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Check if visible.
             * 
             * @return true if visible
             */
            @Override
            public boolean isVisible() {
                // Only show when not logged in.
                return getSessionUser().isAnonymous();
            }

            /**
             * Always stateless.
             * 
             * @return true
             */
            @Override
            public boolean getStatelessHint() // NOPMD: wicket method
            {
                return true;
            }
        });
    }
}
