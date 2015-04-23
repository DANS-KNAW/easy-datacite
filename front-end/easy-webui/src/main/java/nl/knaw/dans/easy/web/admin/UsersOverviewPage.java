package nl.knaw.dans.easy.web.admin;

import java.util.List;

import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class UsersOverviewPage extends AbstractEasyNavPage implements EasyResources {
    public static final String STATE_KEY = "state";
    public static final String ROLE_KEY = "role";

    private static final String WI_USER_OVERVIEW_PANEL = "userOverviewPanel";

    private static final Logger logger = LoggerFactory.getLogger(UsersOverviewPage.class);

    @SpringBean(name = "userService")
    private UserService userService;

    public UsersOverviewPage() {
        super();
        setDefaultModel(createModel());
        add(createPanel());
    }

    public UsersOverviewPage(PageParameters pageParameters) {
        super();
        if (pageParameters.containsKey(STATE_KEY))
            setDefaultModel(createModel(pageParameters.getAsEnum(STATE_KEY, State.class)));
        else if (pageParameters.containsKey(ROLE_KEY))
            setDefaultModel(createModel(pageParameters.getAsEnum(ROLE_KEY, Role.class)));
        else
            setDefaultModel(createModel());
        add(createPanel());
    }

    private LoadableDetachableModel<List<EasyUser>> createModel(final State state) {
        return new LoadableDetachableModel<List<EasyUser>>() {

            private static final long serialVersionUID = -2140139404191270967L;

            @Override
            protected List<EasyUser> load() {
                try {
                    return userService.getUsersByState(state);
                }
                catch (Exception e) {
                    logger.error(fatalMessage(EasyResources.COULD_NOT_RETRIEVE_USERS), e);
                    throw new InternalWebError();
                }
            }

        };
    }

    private LoadableDetachableModel<List<EasyUser>> createModel(final Role role) {
        return new LoadableDetachableModel<List<EasyUser>>() {

            private static final long serialVersionUID = -2140139404191270967L;

            @Override
            protected List<EasyUser> load() {
                try {
                    return userService.getUsersByRole(role);
                }
                catch (Exception e) {
                    logger.error(fatalMessage(EasyResources.COULD_NOT_RETRIEVE_USERS), e);
                    throw new InternalWebError();
                }
            }

        };
    }

    private UsersOverviewPanel createPanel() {
        return new UsersOverviewPanel(WI_USER_OVERVIEW_PANEL, getDefaultModel());
    }

    private LoadableDetachableModel<List<EasyUser>> createModel() {
        return new LoadableDetachableModel<List<EasyUser>>() {

            private static final long serialVersionUID = -2140139404191270967L;

            @Override
            protected List<EasyUser> load() {
                try {
                    return userService.getAllUsers();
                }
                catch (Exception e) {
                    logger.error(fatalMessage(EasyResources.COULD_NOT_RETRIEVE_USERS), e);
                    throw new InternalWebError();
                }
            }

        };
    }
}
