package nl.knaw.dans.easy.web.permission;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
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

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Tab for a depositor with an overview of permission requests on his/her data set.
 */
public class DatasetPermissionsTab extends AbstractDatasetModelPanel
{
    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    private static final long serialVersionUID = 1L;
    public static int TAB_INDEX = 3;

    // GK: note: couldn't find a function to disable paging therefor the Integer.MAX_VALUE as a
    // workaround
    // PBoon: use 50 for now
    public static int MAX_REQUESTS_IN_LIST = 50;

    public static boolean required(final EasyUser user, final Dataset dataset)
    {
        if (dataset == null || user == null || !user.isActive() || user.isAnonymous())
            return false;
        return (dataset.hasDepositor(user) || user.hasRole(Role.ARCHIVIST)) && dataset.hasPermissionRestrictedItems();
    }

    public DatasetPermissionsTab(final String panelId, final DatasetModel datasetModel, final EasyUser user, final AbstractEasyPage currentPage)
    {
        super(panelId, datasetModel);

        add(CSSPackageResource.getHeaderContribution(ExplorerTheme.class, "style.css"));

        final List<PermissionSequence> requests = datasetModel.getObject().getPermissionSequenceList().getPermissionSequences();

        // Use DataTable instead of ListView to support sorting and paging
        ISortableDataProvider<PermissionSequence> dataProvider = new RequestDataTableProvider(requests);

        List<IColumn<PermissionSequence>> columns = new ArrayList<IColumn<PermissionSequence>>();
        columns.add(new ClickablePropertyColumn<PermissionSequence>(new StringResourceModel("requester.displayName", this, null), "requester.displayName",
                "requester.displayName")
        {
            @Override
            protected void onClick(IModel<PermissionSequence> clicked)
            {
                // The dataset is not in de contextparameters, add it because the authorization needs it.
                getEasySession().setContextParameters(new ContextParameters(EasySession.getSessionUser(), datasetModel.getObject()));
                PermissionReplyPage permissionReplyPage = new PermissionReplyPage(datasetModel, currentPage, clicked.getObject());
                setResponsePage(permissionReplyPage);
            }
        });
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("requester.organization", this, null), "requester.organization",
                "requester.organization"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("requester.department", this, null), "requester.department",
                "requester.department"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("state", this, null), "state", "state"));
        columns.add(new PropertyColumn<PermissionSequence>(new StringResourceModel("lastStateChange", this, null), "lastStateChange", "lastStateChange")
        {
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
}
