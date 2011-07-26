package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.link.PageLink;


public class LoginPanel extends AbstractEasyStatelessPanel
{

    /**
     * Component wicket id.
     */
    public static final String  REGISTRATION       = "registration";

    /**
     * Login form wicket id.
     */
    public static final String  LOGIN_FORM         = "loginForm";

    /**
     * Variation constant for inline display.
     */
    public static final String  INLINE             = "inline";

    /**
     * Logger for this class.
     */
    //private static final Logger logger             = LoggerFactory.getLogger(LoginPanel.class);

    /**
     * Serial version UID.
     */
    private static final long   serialVersionUID   = 863711471061407764L;
    
    /**
     * Default constructor for full layout.
     * 
     * @param wicketId The wicket id for this component.
     */
    public LoginPanel(final String wicketId, final UsernamePasswordAuthentication authentication)
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
        addRegisterLink();
    }
    
    /**
     * Add link to register.
     */
    private void addRegisterLink()
    {
        add(new PageLink(REGISTRATION, RegistrationPage.class)
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
                return !isAuthenticated();
            }

            /**
             * Always stateless.
             * 
             * @return true
             */
            @Override
            public boolean getStatelessHint() // NOPMD: wicket method.
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
