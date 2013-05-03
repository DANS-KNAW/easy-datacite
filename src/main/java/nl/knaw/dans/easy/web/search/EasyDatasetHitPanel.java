package nl.knaw.dans.easy.web.search;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SnippetField;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.components.UnescapedLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfo;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.dates.EasyDateLabel;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.easy.web.wicket.DisciplineModel;
import nl.knaw.dans.easy.web.wicket.HighlightedCharSequence;
import nl.knaw.dans.easy.web.wicket.ShortenedCharSequenceModel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

public class EasyDatasetHitPanel extends AbstractEasyPanel
{
    private static final long serialVersionUID = 1765295909790138569L;

    public EasyDatasetHitPanel(String wicketId, IModel<SearchHit<EasyDatasetSB>> model, SearchModel svModel)
    {
        super(wicketId, model);
        add(new DatasetLink("showDataset", model, svModel));
    }

    private class DatasetLink extends AbstractDatasetLink<SearchHit<EasyDatasetSB>>
    {
        private static final long serialVersionUID = -2898309546692290393L;

        DatasetLink(String wicketId, IModel<SearchHit<EasyDatasetSB>> model, SearchModel svModel)
        {
            super(wicketId, model);

            SearchHit<EasyDatasetSB> hit = model.getObject();
            EasyDatasetSB datasetHit = hit.getData();

            // ------ status header

            // this could be done with the help of a SecurityOfficer. The
            // problem is, however, that
            // in this case if the user is a depositor cannot be determined by
            // the Dataset, but only by
            // the dataset search bean. Furthermore, we need to show different
            // kinds of messages based
            // on the role of the user
            String sessionUserId = getSessionUser().isAnonymous() ? null : getSessionUser().getId();
            boolean isDepositor = sessionUserId == null ? false : datasetHit.getDepositorId().equals(sessionUserId);
            boolean isArchivist = getSessionUser() == null ? false : getSessionUser().hasRole(Role.ARCHIVIST);

            EasyDatasetHitStatus status = new EasyDatasetHitStatus();
            status.setState(datasetHit.getState());
            if (isDepositor)
                status.setRole("depositor");
            else if (isArchivist)
                status.setRole("archivist");
            status.setDepositor(userIdToDisplayName(datasetHit.getDepositorId()));
            status.setAssignee(assigneeIdToDisplayName(datasetHit.getAssigneeId()));
            status.setWorkflowProgress(datasetHit.getWorkflowProgress() == null ? 0 : datasetHit.getWorkflowProgress());
            switch (datasetHit.getState())
            {
            case DRAFT:
                status.setDate(datasetHit.getDateDraftSaved());
                break;
            case SUBMITTED:
                status.setDate(datasetHit.getDateSubmitted());
                break;
            case PUBLISHED:
                status.setDate(datasetHit.getDatePublished());
                break;
            case DELETED:
                status.setDate(datasetHit.getDateDeleted());
                break;
            case MAINTENANCE:
                status.setDate(datasetHit.getDateDeleted());
                break;
            }
            if (isDepositor || isArchivist)
                add(new UnescapedLabel("status", new StringResourceModel("status.${state}.${role}", this, new Model<EasyDatasetHitStatus>(status))));
            else
                WicketUtil.hide(this, "status");

            // ------ the number of new requests for this dataset
            int numReq = 0;
            if (datasetHit.getPermissionStatusList() != null)
            {
                // count the number of submitted requests
                for (PermissionRequestSearchInfo info : datasetHit.getPermissionStatusList())
                {
                    if (info.getState().equals(State.Submitted))
                        numReq++;
                }
            }
            add(new Label("newRequests", new Model(numReq)).setVisible(numReq > 0 && (isDepositor || isArchivist)));

            // ------- permission request
            PermissionRequestSearchInfo pmInfo = getPermissionRequestInfo(datasetHit, sessionUserId);
            Label pmStatus = new Label("permissionRequestStatus");
            if (pmInfo != null)
            {
                pmStatus.setDefaultModel(new ResourceModel("fieldvalue." + pmInfo.getState()));
                add(new EasyDateLabel("permissionRequestDate", pmInfo.getStateLastModified()));
            }
            else
            {
                pmStatus.setVisible(false);
                WicketUtil.hide(this, "permissionRequestDate");
            }
            add(pmStatus);

            addLabel(new UnescapedLabel("title", new Model<String>(getSnippetOrValue("dcTitle"))));
            addLabel(new UnescapedLabel("creator", new ShortenedCharSequenceModel(getSnippetOrValue("dcCreator"))));
            addLabel(new Label("dateCreated", datasetHit.getDateCreatedFormatted()));

            // -------- column 3
            final List<String> audienceList = datasetHit.getAudience();
            // requirements say that 'audience' can be plural. mierenneukerij dus.
            String emdAudience = audienceList == null || audienceList.size() <= 1 ? "fieldname.emd_audience" : "fieldname.emd_audiences";
            add(new Label("audienceLabel", new ResourceModel(emdAudience)));

            add(new ListView<String>("disciplines", audienceList)
            {
                private static final long serialVersionUID = 1540669253501482128L;

                @Override
                protected void populateItem(ListItem<String> item)
                {
                    item.add(new Label("disciplineName", new DisciplineModel(item.getModelObject())));
                    Label disciplineSeparator = new Label("disciplineSeparator", ", ");
                    disciplineSeparator.setVisible(item.getIndex() + 1 < audienceList.size());
                    item.add(disciplineSeparator);
                }
            }.setVisible(audienceList != null && audienceList.size() > 0));
            addLabel(new Label("accessrights", new ResourceModel("fieldvalue." + datasetHit.getAccessCategory())), datasetHit.getAccessCategory() != null);
            DateTime dateAvailable = datasetHit.getDateAvailable();
            addLabel(new Label("dateAvailable", datasetHit.getDateAvailableFormatted()), dateAvailable != null && dateAvailable.isAfterNow());
            addLabel(new EasyDateLabel("dateSubmitted", datasetHit.getDateSubmitted()));

            // -------- footer
            addLabel(new Label("relevance", String.format("%.0f", hit.getRelevanceScore() * 100)), !StringUtils.isBlank(svModel.getObject().getRequestBuilder()
                    .getRequest().getQuery().getQueryString()));
            List<SnippetField> remainingSnippets = getRemainingSnippets();
            add(new ListView<SnippetField>("snippets", remainingSnippets)
            {
                private static final long serialVersionUID = 6092057488401837474L;

                @Override
                protected void populateItem(ListItem<SnippetField> item)
                {
                    final SnippetField snippetField = item.getModelObject();
                    String snippet = "";
                    for (String snip : snippetField.getValue())
                        snippet += snip;
                    item.add(new Label("snippetField", new ResourceModel("fieldname." + snippetField.getName())));
                    item.add(new UnescapedLabel("snippet", new ShortenedCharSequenceModel(new HighlightedCharSequence(snippet), 100)));
                }
            }.setVisible(remainingSnippets.size() > 0));
        }

        private PermissionRequestSearchInfo getPermissionRequestInfo(EasyDatasetSB datasetHit, String userId)
        {
            if (userId == null)
                return null;

            List<PermissionRequestSearchInfo> permissionStatusList = datasetHit.getPermissionStatusList();
            if (permissionStatusList == null)
                return null;

            for (PermissionRequestSearchInfo pmInfo : permissionStatusList)
            {
                if (pmInfo.getRequesterId().equals(userId))
                {
                    return pmInfo;
                }
            }

            return null;
        }

        // TODO: implement this function
        private String userIdToDisplayName(String userId)
        {
            return userId;
        }

        private String assigneeIdToDisplayName(String userId)
        {
            if (userId == null || userId.equals(WorkflowData.NOT_ASSIGNED))
                // Tried to retrieve a localized string for a component that has not yet been added to the page.
                // Make sure you are not calling Component#getString() inside your Component's constructor.
                // This method called from the constructor.
                //return getString(AssignToDropChoiceList.NOT_ASSIGNED_RESOURCEKEY);
                return "Not Assigned";
            else
                return userIdToDisplayName(userId);
        }

        @Override
        public void onClick()
        {
            SearchHit<? extends DatasetSB> hit = (SearchHit<? extends DatasetSB>) getModelObject();
            DatasetSB datasetHit = hit.getData();

            // instructions how to get back to this searchView
            ((EasySession) getSession()).setRedirectPage(DatasetViewPage.class, getPage());

            // view the dataset on dataset view page.
            PageParameters params = new PageParameters();
            params.put(DatasetViewPage.PM_DATASET_ID, datasetHit.getStoreId());
            setResponsePage(DatasetViewPage.class, params);
        }
    }

}
