package nl.knaw.dans.easy.web.admin;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage2;
import nl.knaw.dans.easy.web.wicket.FormListener;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class UserDetailsPage extends AbstractEasyNavPage2 implements FormListener
{

    public static final String PM_USER_ID = "uid";

    private static final String WI_USER_DETAILS_PANEL = "userDetailsPanel";

    private static Logger logger = LoggerFactory.getLogger(UserDetailsPage.class);

    private EasyUser displayedUser;

    private ContextParameters contextParameters;

    @SpringBean(name = "userService")
    private UserService userService;

    public UserDetailsPage(PageParameters parameters)
    {
        super(parameters);
        String displayedUserId = parameters.getString(PM_USER_ID);
        if (StringUtils.isBlank(displayedUserId))
        {
            errorMessage(EasyResources.NO_USER_SELECTED);
            logger.error("Insufficient parameters: uid == null");
            throw new InternalWebError();
        }
        setDisplayedUser(displayedUserId);
        constructSwitchPanel(false, true, this);
    }

    /**
     * Constructor with existing (and persisted) user.
     * 
     * @param user
     *        user to show or edit
     * @param inEditMode
     *        start in edit mode
     * @param enableModeSwitch
     *        enable switching between edit and display mode
     */
    public UserDetailsPage(String displayedUserId, boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        setDisplayedUser(displayedUserId);
        constructSwitchPanel(inEditMode, enableModeSwitch, this);
    }

    @Override
    public ContextParameters getContextParameters()
    {
        return contextParameters;
    }

    private void constructSwitchPanel(final boolean inEditMode, final boolean enableModeSwitch, final FormListener listener)
    {
        add(new SwitchPanel(WI_USER_DETAILS_PANEL, inEditMode)
        {

            private static final long serialVersionUID = -3728298099724755092L;

            @Override
            public Panel getDisplayPanel()
            {
                return new UserDetailsDisplayPanel(this, getUserModel(), enableModeSwitch);
            }

            @Override
            public Panel getEditPanel()
            {
                return new UserDetailsEditPanel(this, getUserModel(), enableModeSwitch, listener);
            }

        });
    }

    private IModel getUserModel()
    {
        return new CompoundPropertyModel(displayedUser);
    }

    private void setDisplayedUser(String displayedUserId)
    {
        if (displayedUserId == null)
        {
            displayedUser = new EasyUserImpl();
        }
        else
        {
            displayedUser = getUser(displayedUserId);
        }
        contextParameters = new ContextParameters(getSessionUser(), displayedUser);
    }

    private EasyUser getUser(String userId)
    {
        EasyUser user;
        try
        {
            user = userService.getUserById(getSessionUser(), userId);
        }
        catch (CommonSecurityException e)
        {
            errorMessage(EasyResources.ILLEGAL_ACCESS);
            logger.error("Illegal Access: ", e);
            throw new InternalWebError();
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.USER_NOT_FOUND, userId);
            logger.error(message, e);
            throw new InternalWebError();
        }
        return user;
    }

    public void onUpdate(Form form, Object object)
    {
        String displayedUserId = ((EasyUser) object).getId();
        setDisplayedUser(displayedUserId);
    }
}
