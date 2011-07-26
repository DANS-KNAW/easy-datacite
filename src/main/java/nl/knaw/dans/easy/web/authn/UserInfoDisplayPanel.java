package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoDisplayPanel extends AbstractEasyStatelessPanel implements EasyResources
{
    private static final String WR_CHANGE_PASSWORD_LINK = "changePasswordLink";

    private static final long   serialVersionUID = 2646103426056079L;

    private static Logger       logger            = LoggerFactory.getLogger(UserInfoDisplayPanel.class);

    private final SwitchPanel parent;
    private final boolean       enableModeSwitch;

    public UserInfoDisplayPanel(final SwitchPanel parent, final String userId, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        init(userId);
    }

    private void init(final String userId)
    {
        EasyUser user = null;
        try
        {
            user = Services.getUserService().getUserById(getSessionUser(), userId);
        }
        catch (ServiceException e)
        {
        	final String message = errorMessage(EasyResources.USER_NOT_FOUND, userId);
        	logger.error(message);
            throw new RestartResponseException(new ErrorPage());
        }

        if (user == null)
        {
            throw new RestartResponseException(new ErrorPage());
        }
        else
        {
            constructPanel(user);
        }
    }

    private void constructPanel(EasyUser user)
    {
        super.setDefaultModel(new CompoundPropertyModel(user));

        addCommonFeedbackPanel();
        
        add(new Label(UserProperties.USER_ID));
        add(new Label(UserProperties.DISPLAYNAME));
        add(new Label(UserProperties.TITLE));
        add(new Label(UserProperties.INITIALS));
        add(new Label(UserProperties.PREFIXES));
        add(new Label(UserProperties.SURNAME));
        add(new Label(UserProperties.ORGANIZATION));
        add(new Label(UserProperties.DEPARTMENT));
        add(new Label(UserProperties.FUNCTION));
        add(new Label(UserProperties.DISCIPLINE1, getDisciplineString(user.getDiscipline1())));
        add(new Label(UserProperties.DISCIPLINE2, getDisciplineString(user.getDiscipline2())));
        add(new Label(UserProperties.DISCIPLINE3, getDisciplineString(user.getDiscipline3())));
        add(new Label(UserProperties.ADDRESS));
        add(new Label(UserProperties.POSTALCODE));
        add(new Label(UserProperties.CITY));
        add(new Label(UserProperties.COUNTRY));
        add(new Label(UserProperties.EMAIL));
        add(new Label(UserProperties.TELEPHONE));
        add(new Label(UserProperties.DAI));
        
		// Have different message depending on boolean; yes or no!
		add(new Label(UserProperties.OPTS_FOR_NEWSLETTER,
		        new StringResourceModel("userinfo.optsForNewsletter.${optsForNewsletter}", this, new Model(user))));
        add(new Label(UserProperties.LOG_MY_ACTIONS,
                new StringResourceModel("userinfo.logMyActions.${logMyActions}", this, new Model(user))));
		
       
        Link modeSwitch = new Link(EDIT_LINK)
        {

            private static final long serialVersionUID = -804946462543838511L;

            @Override
            public void onClick()
            {
                parent.switchMode();
            }

        };
        modeSwitch.setVisible(enableModeSwitch);
        add(modeSwitch);
        
        Link changePasswordLink = new Link(WR_CHANGE_PASSWORD_LINK)
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(ChangePasswordPage.class);
            }          
        };
        add(changePasswordLink);
    }
    
    private String getDisciplineString(String id)
    {
        KeyValuePair result = DisciplineUtils.getDisciplineItemById(id);
        
        return result == null ? "" : result.getValue();
    }

}
