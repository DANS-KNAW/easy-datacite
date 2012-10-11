package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.link.PageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPanelRegular extends AbstractEasyStatelessPanel
{
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
    // private static final Logger logger = LoggerFactory.getLogger(LoginPanel.class);

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
    public LoginPanelRegular(final String wicketId, final UsernamePasswordAuthentication authentication)
    {
        super(wicketId);
        init(authentication);
    }

    /**
     * Initialize the same for every constructor.
     */
    private void init(final UsernamePasswordAuthentication authentication)
    {
        addLoginForm(authentication);

        addForgottenPasswordLink();
    }

    private void addForgottenPasswordLink()
    {
        add(new PageLink(FORGOTTEN_PASSWORD, ForgottenPasswordPage.class)
        {
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
            public boolean isVisible()
            {
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

    /**
     * Add default LoginForm.
     */
    private void addLoginForm(final UsernamePasswordAuthentication authentication)
    {
        add(new LoginForm(LOGIN_FORM, authentication));
    }
}
