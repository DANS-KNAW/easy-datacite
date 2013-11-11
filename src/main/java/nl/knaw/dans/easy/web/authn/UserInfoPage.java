package nl.knaw.dans.easy.web.authn;

import javax.mail.Session;

import nl.knaw.dans.easy.business.bean.SystemStatus;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class UserInfoPage extends AbstractEasyNavPage
{
    /**
     * The key for parameter 'userId'.
     */
    public static final String PM_USERID = "userId";

    /**
     * The key for parameter 'inEditMode'.
     */
    public static final String PM_IN_EDIT_MODE = "inEditMode";

    /**
     * The key for parameter 'enableModeSwitch'.
     */
    public static final String PM_ENABLE_MODESWITCH = "enableModeSwitch";

    private static final String WI_USER_INFO_PANEL = "userInfoPanel";

    private static final String WI_PAGE_HEADER = "page.header";

    private static final String WR_FIRSTLOGIN = "firstLogin";

    // Used when client views her own info.
    private static final String RI_PERSONAL_POSTFIX = "personal.title.postfix";

    // Used when displayed user is not on the session user.
    private static final String RI_USER_POSTFIX = "user.title.postfix";

    private final String displayedUserId;

    private final boolean isFirstLogin;

    private static Logger logger = LoggerFactory.getLogger(UserInfoPage.class);

    /**
     * No-argument constructor for displaying information on the current user, switch to edit-mode is
     * enabled.
     */
    public UserInfoPage()
    {
        this(true, !isReadOnly());
    }

    private static boolean isReadOnly()
    {
        return SystemStatus.INSTANCE.getReadOnly() && !getSessionUser().hasRole(Role.ADMIN, Role.ARCHIVIST);
    }

    /**
     * Displays information on the current user.
     * 
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the
     *        switch, <code>false</code> otherwise
     */
    public UserInfoPage(boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        EasyUser currentUser = getSessionUser();
        if (currentUser == null)
        {
            logger.error(this.getClass().getName() + " called without the user being logged in. Redirecting to HomePage.");
            throw new RestartResponseException(HomePage.class);
        }
        else
        {
            displayedUserId = currentUser.getId();
            isFirstLogin = currentUser.isFirstLogin();
            init(inEditMode && !isReadOnly(), enableModeSwitch && !isReadOnly());
        }
    }

    /**
     * Displays information on a user.
     * 
     * @param params
     *        parameters to use
     * @see #PM_USERID
     * @see #PM_IN_EDIT_MODE
     * @see #PM_ENABLE_MODESWITCH
     * @see #UserInfoPage(String, boolean, boolean)
     */
    public UserInfoPage(PageParameters params)
    {
        super();
        displayedUserId = params.getKey(PM_USERID);
        isFirstLogin = false;
        boolean inEditMode = params.getBoolean(PM_IN_EDIT_MODE);
        boolean enableModeSwitch = params.getBoolean(PM_ENABLE_MODESWITCH);
        init(inEditMode, enableModeSwitch);
    }

    /**
     * Displays information on the user with the given userId.
     * 
     * @param userId
     *        the id of the user to show info on
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the
     *        switch, <code>false</code> otherwise
     */
    public UserInfoPage(String userId, boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        displayedUserId = userId;
        isFirstLogin = false;
        init(inEditMode, enableModeSwitch);
    }

    /**
     * Get 'personal information' or 'user information' as title postfix, depending on displayed user.
     */
    @Override
    public String getPageTitlePostfix()
    {
        String pageTitlePostfix = "";
        final EasyUser user = ((EasySession) getSession()).getUser();
        if (user.getId().equals(displayedUserId))
        {
            pageTitlePostfix = getLocalizer().getString(RI_PERSONAL_POSTFIX, this);
        }
        else
        {
            pageTitlePostfix = getLocalizer().getString(RI_USER_POSTFIX, this);
        }
        return pageTitlePostfix;
    }

    // same for all constructors.
    private void init(final boolean inEditMode, final boolean enableModeSwitch)
    {
        add(new Label(WI_PAGE_HEADER, getPageTitlePostfix()));
        add(new Label(WR_FIRSTLOGIN, new ResourceModel(WR_FIRSTLOGIN))
        {
            private static final long serialVersionUID = 5400998016143701384L;

            @Override
            public boolean isVisible()
            {
                return isFirstLogin;
            }
        });

        add(new SwitchPanel(WI_USER_INFO_PANEL, inEditMode)
        {

            private static final long serialVersionUID = -5561111015378292565L;

            @Override
            public Panel getDisplayPanel()
            {
                return new UserInfoDisplayPanel(this, displayedUserId, enableModeSwitch);
            }

            @Override
            public Panel getEditPanel()
            {
                return new UserInfoEditPanel(this, displayedUserId, enableModeSwitch);
            }

        });
    }

}
