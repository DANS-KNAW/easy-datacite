package nl.knaw.dans.easy.web.admin;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage2;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class UsersOverviewPage extends AbstractEasyNavPage2 implements EasyResources
{
    private static final String WI_USER_OVERVIEW_PANEL = "userOverviewPanel";

    private static final Logger logger = LoggerFactory.getLogger(UsersOverviewPage.class);

    @SpringBean(name = "userService")
    private UserService userService;

    public UsersOverviewPage()
    {
        super();
        setDefaultModel(new LoadableDetachableModel()
        {

            private static final long serialVersionUID = -2140139404191270967L;

            @Override
            protected Object load()
            {
                List<EasyUser> users = null;
                try
                {
                    users = userService.getAllUsers();
                }
                catch (ServiceException e)
                {
                    final String message = fatalMessage(EasyResources.COULD_NOT_RETRIEVE_USERS);
                    logger.error(message, e);
                    throw new InternalWebError();
                }
                return users;
            }

        });

        add(new UsersOverviewPanel(WI_USER_OVERVIEW_PANEL, getDefaultModel()));

        Link addLink = new Link(ADD_LINK)
        {
            private static final long serialVersionUID = -4963887902220218903L;

            @Override
            public void onClick()
            {
                setResponsePage(UserDetailsPage.class);
            }
        };
        addLink.add(new Label(ADD_LINK, new ResourceModel(ADD_LINK)));
        addLink.setVisible(false);
        add(addLink);
    }

}
