package nl.knaw.dans.easy.web.admin;

import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDetailsDisplayPanel extends AbstractEasyPanel implements EasyResources
{
    private static Logger       logger                  = LoggerFactory.getLogger(UserDetailsDisplayPanel.class);

    private static final long serialVersionUID = 227752819073521342L;
        
    private final SwitchPanel parent;
    private final boolean       enableModeSwitch;
    
    public UserDetailsDisplayPanel(final SwitchPanel parent, final IModel model, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI, model);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        constructPanel();
    }
    
    private void constructPanel()
    {
        EasyUser user = (EasyUser) getDefaultModel().getObject();
        
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
        add(new Label(UserProperties.DISPLAYGROUPS));
        add(new Label(UserProperties.DISPLAYROLES));
        add(new Label(UserProperties.STATE));
        
        // Have different message depending on boolean; yes or no!
        add(new Label(UserProperties.OPTS_FOR_NEWSLETTER,
                new StringResourceModel("userinfo.optsForNewsletter.${optsForNewsletter}", this, new Model(user))));
        add(new Label(UserProperties.LOG_MY_ACTIONS,
                new StringResourceModel("userinfo.logMyActions.${logMyActions}", this, new Model(user))));

        DateTime lastLogin = user.getLastLoginDate();
        
        OperationalAttributes opa;
		try
		{
			opa = Services.getUserService().getOperationalAttributes(user);
		}
		catch (ServiceException e)
		{
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
	        throw new InternalWebError();
	    }
        DateTime cDate = opa.getCreateTimestamp();
        
        addCommonFeedbackPanel();
        add(DateLabel.forDatePattern("createTime", new Model(cDate == null ? null : cDate.toDate()), "yyyy-MM-dd HH:mm"));
        add(DateLabel.forDatePattern("lastLogin", new Model(lastLogin == null ? null : lastLogin.toDate()), "yyyy-MM-dd HH:mm"));
        
        Link modeSwitch = new Link(EDIT_LINK)
        {

            private static final long serialVersionUID = -6746681373982634187L;

            @Override
            public void onClick()
            {
                parent.switchMode();
            }           
        };
        modeSwitch.setVisible(enableModeSwitch);
        add(modeSwitch);
        
        Link doneLink = new Link(DONE_LINK)
        {

            private static final long serialVersionUID = -9021249896478173151L;

            @Override
            public void onClick()
            {
                setResponsePage(UsersOverviewPage.class);
            }           
        };
        add(doneLink);
    }
    
    private String getDisciplineString(String id)
    {
        KeyValuePair result = DisciplineUtils.getDisciplineItemById(id);
        
        return result == null ? "" : result.getValue();
    }
    

}
