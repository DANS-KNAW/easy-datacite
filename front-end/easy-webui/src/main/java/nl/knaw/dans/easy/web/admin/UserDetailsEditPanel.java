package nl.knaw.dans.easy.web.admin;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.wicket.FormListener;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDetailsEditPanel extends AbstractEasyPanel implements EasyResources
{
    private static Logger logger = LoggerFactory.getLogger(UserDetailsEditPanel.class);

    private static final String WI_USER_INFO_FORM = "userInfoForm";

    private static final long serialVersionUID = 3677596835048406356L;

    final SwitchPanel parent;
    final boolean enableModeSwitch;

    @SpringBean(name = "userService")
    UserService userService;

    public UserDetailsEditPanel(final SwitchPanel parent, final IModel<EasyUser> model, final boolean enableModeSwitch, final FormListener listener)
    {
        super(SwitchPanel.SWITCH_PANEL_WI, model);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        constructPanel(listener);
    }

    private void constructPanel(final FormListener listener)
    {
        UserInfoForm infoForm = new UserInfoForm(this, WI_USER_INFO_FORM, (IModel<EasyUser>) getDefaultModel(), listener);
        add(infoForm);
        // AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    List<String> getGroupIds()
    {
        List<String> groups = null;
        try
        {
            groups = userService.getAllGroupIds();
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.ERROR_IN_GETTING_GROUPS);
            logger.error(message, e);
            throw new InternalWebError();
        }
        return groups;
    }

}
