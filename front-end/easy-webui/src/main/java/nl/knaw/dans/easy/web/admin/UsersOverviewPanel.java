package nl.knaw.dans.easy.web.admin;

import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class UsersOverviewPanel extends AbstractEasyPanel {
    private static final long serialVersionUID = 3332915988521197605L;

    @SuppressWarnings("unchecked")
    public UsersOverviewPanel(String wicketId, IModel model) {
        super(wicketId, model);
        List<EasyUser> users = (List<EasyUser>) model.getObject();
        add(new ListView("users", users) {

            private static final long serialVersionUID = -6597598635055541684L;

            @Override
            protected void populateItem(ListItem item) {
                final EasyUser user = (EasyUser) item.getDefaultModelObject();

                item.add(new UserPanel("user", new CompoundPropertyModel(user)));
            }
        });
    }

    class UserPanel extends AbstractEasyStatelessPanel {

        private static final long serialVersionUID = 7544583798689556606L;

        public UserPanel(String wicketId, IModel model) {
            super(wicketId, model);
            add(new UserLink("showUser", model));
            add(new Label("displayName"));
            add(new Label("userId"));
            add(new Label("state"));
        }

    }

    static class UserLink extends Link {

        private static final long serialVersionUID = -3139887868300673513L;

        private final String userId;

        public UserLink(String id, IModel model) {
            super(id, model);
            this.userId = ((EasyUser) getModelObject()).getId();
            add(new Label("commonName"));
        }

        @Override
        public void onClick() {
            setResponsePage(new UserDetailsPage(userId, false, true));
        }

    }
}
