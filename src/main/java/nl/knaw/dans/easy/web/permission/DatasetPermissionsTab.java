package nl.knaw.dans.easy.web.permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.common.wicket.components.GPanel;
import nl.knaw.dans.common.wicket.components.explorer.style.ExplorerTheme;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;


/**
 * Tab for a depositor with an overview of permission requests on his/her data set.
 *
 */
public class DatasetPermissionsTab extends AbstractDatasetModelPanel
{
    private static final String 	PERMISSION_DATASET	 = "permission.dataset";
    private static final String 	NR_REQUESTS			 = "permission.nr_requests";
    private static final String 	DATE_TIME_FORMAT	 = "DateAndTimeFormat";

    private static final String LINK_WID = "link";
    private static final String SUMMARY_WID = "summary";
    private static final long serialVersionUID = 1L;
    public static int TAB_INDEX = 3;

    // GK: note: couldn't find a function to disable paging therefor the Integer.MAX_VALUE as a workaround
    // PBoon: use 50 for now
    public static int MAX_REQUESTS_IN_LIST = 50;
    
    public static boolean required(final EasyUser user, final Dataset dataset)
    {
        if (dataset == null || user == null || !user.isActive() || user.isAnonymous())
            return false;
        return (dataset.hasDepositor(user) || user.hasRole(Role.ARCHIVIST)) && dataset.hasPermissionRestrictedItems();
    }

    public DatasetPermissionsTab(final String panelId, final DatasetModel datasetModel, final EasyUser user,
            final AbstractEasyPage currentPage)
    {
        super(panelId, datasetModel);
        
        add(CSSPackageResource.getHeaderContribution(ExplorerTheme.class, "style.css"));
        
        final List<PermissionSequence> requests =
                datasetModel.getObject().getPermissionSequenceList().getPermissionSequences();

        //add(new Label(SUMMARY_WID, getSummary(requests)));
        
        /* TODO remove ListView and related code
        add(new HeaderPanel("header", requests.size()>0));
        add(new ListView("requests", requests)
        {
            private static final long serialVersionUID = -6597598635055541684L;

            @Override
            protected void populateItem(final ListItem item)
            {
                final PermissionSequence request = (PermissionSequence) item.getDefaultModelObject();
                final CompoundPropertyModel model = new CompoundPropertyModel(request);
                item.add(new RequestPanel("request", model, datasetModel, currentPage));
            }
        });
        */
        
        // Use DataTable instead of ListView to support sorting and paging
        ISortableDataProvider<PermissionSequence> dataProvider = new RequestDataTableProvider(requests);
        
        List<IColumn<PermissionSequence>> columns = new ArrayList<IColumn<PermissionSequence>>();
        columns.add(new ClickablePropertyColumn<PermissionSequence>(new StringResourceModel("requester.displayName", this, null), "requester.displayName", "requester.displayName") {
                    @Override
                    protected void onClick(IModel<PermissionSequence> clicked) {
                       //info("You clicked: " + clicked.getObject().getRequester().getDisplayName());
                        // The dataset is not in de contextparameters, add it because the authorization needs it.
                        getEasySession().setContextParameters(new ContextParameters(EasySession.getSessionUser(), datasetModel.getObject()));
                        PermissionReplyPage permissionReplyPage = new PermissionReplyPage(datasetModel, currentPage, clicked.getObject());
                        setResponsePage(permissionReplyPage);
                    }
            });
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("requester.organization", this, null),"requester.organization", "requester.organization"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("requester.department", this, null),"requester.department", "requester.department"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("state", this, null),"state", "state"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("lastStateChange", this, null),"lastStateChange", "lastStateChange") {
            private static final long serialVersionUID = 1L;
            @Override
            public void populateItem(Item<ICellPopulator<PermissionSequence>> item, String componentId, IModel<PermissionSequence> rowModel)
            {
                PermissionSequence sequence = rowModel.getObject();
                item.add(new DateTimeLabel(componentId, getString(DATE_TIME_FORMAT), new Model(sequence.getLastStateChange())));
            }
        });
        
        DefaultDataTable table = new DefaultDataTable("requestTable", columns, dataProvider, MAX_REQUESTS_IN_LIST);
        add(table);
    }

    private String getSummary(final List<PermissionSequence> requests)
    {
        if (requests == null || requests.size() == 0)
        {
            return getString(PERMISSION_DATASET);
        }
        else
        {
            return getString(NR_REQUESTS).replace("$1", String.valueOf(requests.size()));
        }
    }

    class HeaderPanel extends AbstractEasyStatelessPanel
    {
        private static final long serialVersionUID = 7544583798689556606L;

        public HeaderPanel(String wicketId, boolean visible)
        {
            super(wicketId);
            setVisible(visible);
        }
    }

    class RequestPanel extends AbstractEasyStatelessPanel
    {
        private static final long serialVersionUID = 7544583798689556606L;

        public RequestPanel(final String wicketId, final IModel model, final DatasetModel datasetModel,
                final AbstractEasyPage currentPage)
        {
            super(wicketId, model);

            final PermissionSequence request = ((PermissionSequence) getDefaultModelObject());
            final DateTime date = request.getLastRequestDate();
            final MarkupContainer pageLink = new PageLink(LINK_WID, new IPageLink()
            {
                private static final long serialVersionUID = 1L;

                public Page getPage()
                {
                	// The dataset is not in de contextparameters, add it because the authorization needs it.
                	getEasySession().setContextParameters(new ContextParameters(EasySession.getSessionUser(), datasetModel.getObject()));
                    return new PermissionReplyPage(datasetModel, currentPage, request);
                }

                @SuppressWarnings("unchecked")
                public Class<PermissionReplyPage> getPageIdentity()
                {
                    return PermissionReplyPage.class;
                }
            });
            add(pageLink);
            pageLink.add(new Label("requester.displayName"));
            add(new Label("requester.organization"));
            add(new Label("requester.department"));
            add(new Label("state"));
            add(new DateTimeLabel("lastStateChange", getString(DATE_TIME_FORMAT), new Model(date)));
        }
    }
}
