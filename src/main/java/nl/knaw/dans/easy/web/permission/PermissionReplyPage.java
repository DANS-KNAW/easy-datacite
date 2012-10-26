package nl.knaw.dans.easy.web.permission;

import java.text.MessageFormat;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetUrlComposerImpl;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page with a form allowing a depositor of a data set to (re)view or reply to a permission request.
 *
 */
public class PermissionReplyPage extends AbstractEasyNavPage
{
    public static final String PM_DATASET_ID = "dsid";
    public static final String PM_REQUESTER_ID = "rqid";

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionReplyPage.class);
    private static final String TITLE_KEY = "InitialPermissionReplyTitle";
    private final EasyUser user;
    private boolean initiated = false;
    private final AbstractEasyPage fromPage;
    private final PermissionSequence sequence;

    public static String urlFor(Dataset dataset, EasyUser requester, Component component)
    {
        return urlFor(dataset.getStoreId(), requester.getId(), component);
    }

    public static String urlFor(String datasetId, String requesterId, Component component)
    {
        PageParameters parameters = new PageParameters();
        parameters.add(PM_DATASET_ID, datasetId);
        parameters.add(PM_REQUESTER_ID, requesterId);
        String path = (String) component.urlFor(PermissionReplyPrePage.class, parameters);
        String bookmarkableLink = RequestUtils.toAbsolutePath(path);
        LOGGER.debug("Composed bookmarkable link: " + bookmarkableLink);
        return bookmarkableLink;
    }

    public PermissionReplyPage(final DatasetModel datasetModel, final AbstractEasyPage fromPage, final PermissionSequence sequence)
    {
        super(new DatasetModel(datasetModel));
        this.fromPage = fromPage;
        this.sequence = sequence;
        user = ((EasySession) getSession()).getUser();
        if (user.isAnonymous() || datasetModel == null)
        {
            LOGGER.error("No user or no dataset for permission reply.");
            pageBack();

        }
        // hack ! security
        if (!datasetModel.getObject().hasDepositor(user))
        {
            pageBack();
        }

        // Disable dynamic reload. We don't want the dataset reloading automatically
        // just before saving. We want to save it in exactly the way it was presented.
        getDatasetModel().setDynamicReload(false);
    }

    protected DatasetModel getDatasetModel()
    {
        return (DatasetModel) getDefaultModel();
    }

    public PermissionReplyPage(PageParameters parameters)
    {
        super(parameters);
        LOGGER.debug("Instantiating PermissionReplyPage with PageParameters");
        DatasetModel datasetModel;
        String datasetId = parameters.getString(PM_DATASET_ID);
        try
        {
            datasetModel = new DatasetModel(datasetId);
            setDefaultModelObject(datasetModel);
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.DATASET_LOAD, datasetId);
            LOGGER.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
        fromPage = null;
        String requesterId = parameters.getString(PM_REQUESTER_ID);
        sequence = getDataset().getPermissionSequenceList().getSequenceFor(requesterId);
        user = getSessionUser();
        // hack ! security
        if (!getDataset().hasDepositor(user))
        {
            pageBack();
        }
    }

    protected Dataset getDataset()
    {
        return (Dataset) getDefaultModelObject();
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            setOutputMarkupId(true);
            addComponents();
            DatasetNotification.setDatasetUrlComposer(DatasetUrlComposerImpl.getInstance(getPageMap()));
            initiated = true;
        }
        super.onBeforeRender();
    }

    @Override
    public String getPageTitlePostfix()
    {
        // TODO Get from resource, use Wicket StringResourceModel
        String format = getString(TITLE_KEY);
        if (sequence.getState().equals(PermissionSequence.State.Submitted))
            format = getString(TITLE_KEY + "." + sequence.getState());
        String requesterName = sequence.getRequester().getDisplayName();
        String preferredTitle = getDataset().getPreferredTitle();
        final String[] strings = new String[] {requesterName, preferredTitle};
        return new MessageFormat(format).format(strings);
    }

    private void addComponents()
    {
        add(new Label("title", new Model<String>(getPageTitlePostfix())));

        if (sequence.getState().equals(PermissionSequence.State.Submitted))
        {
            LOGGER.debug("Edit panel");
            add(new PermissionReplyEditPanel("replyPanel", fromPage, new DatasetModel((Dataset) getDefaultModelObject()), sequence));
        }
        else
        {
            LOGGER.debug("View panel");
            add(new PermissionReplyViewPanel("replyPanel", fromPage, new DatasetModel((Dataset) getDefaultModelObject()), sequence));
        }

    }

    protected void pageBack() throws RestartResponseException
    {
        if (fromPage == null)
        {
            setResponsePage(HomePage.class);
        }
        else
        {
            fromPage.refresh();
            setResponsePage(fromPage);
        }
    }
}
