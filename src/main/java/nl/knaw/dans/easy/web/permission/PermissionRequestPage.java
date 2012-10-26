package nl.knaw.dans.easy.web.permission;

import java.text.MessageFormat;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.ErrorPage;
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
 * Page with a form allowing a user of a data set to (re)view or submit a permission request.
 */
public class PermissionRequestPage extends AbstractEasyNavPage
{
    public static final String PM_DATASET_ID = "dsid";
    public static final String PM_REQUESTER_ID = "rqid";

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionRequestPage.class);
    private static final String TITLE_KEY = "InitialPermissionRequestTitle";
    private static final String INTRO_KEY = "InitialPermissionRequestIntro";
    private boolean initiated = false;
    private final AbstractEasyPage fromPage;

    public static String urlFor(String datasetId, String requesterId, Component component)
    {
        PageParameters parameters = new PageParameters();
        parameters.add(PM_DATASET_ID, datasetId);
        parameters.add(PM_REQUESTER_ID, requesterId);
        String path = (String) component.urlFor(PermissionRequestPage.class, parameters);
        String bookmarkableLink = RequestUtils.toAbsolutePath(path);
        LOGGER.debug("Composed bookmarkable link: " + bookmarkableLink);
        return bookmarkableLink;
    }

    public PermissionRequestPage(final DatasetModel datasetModel)
    {
        super(new DatasetModel(datasetModel));
        this.fromPage = null;
    }

    public PermissionRequestPage(PageParameters parameters)
    {
        super(parameters);

        String datasetId;
        String requesterId;
        try
        {
            datasetId = parameters.getString(PM_DATASET_ID);
            requesterId = parameters.getString(PM_REQUESTER_ID);
        }
        catch (Exception e)
        {
            errorMessage(EasyResources.INSUFFICIENT_PARAMETERS);
            LOGGER.error("Unable to read page parameters: ", e);
            throw new RestartResponseException(new ErrorPage());
        }
        EasyUser user = getSessionUser();
        if (user == null || user.isAnonymous() || !user.getId().equals(requesterId))
        {
            errorMessage(EasyResources.ILLEGAL_ACCESS);
            LOGGER.error("Identity of user unknown or illegal visit.");
            throw new RestartResponseException(new ErrorPage());
        }
        Dataset dataset;
        DatasetModel datasetModel;
        try
        {
            dataset = Services.getDatasetService().getDataset(user, new DmoStoreId(datasetId));
            datasetModel = new DatasetModel(dataset);
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.DATASET_LOAD, datasetId);
            LOGGER.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
        //        if (!getDataset().hasPermissionRestrictedItems())
        //        {
        //            errorMessage(EasyResources.ILLEGAL_PAGE_PARAMETERS);
        //            LOGGER.error("No restricted items in dataset " + getDataset().getStoreId());
        //            throw new RestartResponseException(new ErrorPage());
        //        }
        fromPage = null;
        setResponsePage(new PermissionRequestPage(datasetModel));
    }

    protected Dataset getDataset()
    {
        return (Dataset) getDefaultModelObject();
    }

    public PermissionRequestPage(final DatasetModel datasetModel, final AbstractEasyPage fromPage)
    {
        super(datasetModel);
        this.fromPage = fromPage;
        EasyUser user = ((EasySession) getSession()).getUser();
        if (user.isAnonymous() || datasetModel == null)
        {
            LOGGER.error("No user or no dataset for permission request.");
            pageBack();

        }
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
        final String[] strings = new String[] {getDataset().getPreferredTitle()};
        final State state = getRequestState();
        final String key = TITLE_KEY + (state == null ? "" : "." + state);
        return new MessageFormat(getString(key)).format(strings);
    }

    private void addComponents()
    {
        add(new Label("title", new Model<String>(getPageTitlePostfix())));

        addIfInitialRequest(new Label("intro", new Model<String>(getString(INTRO_KEY))));

        // if initial (needs to be created or it is returned, we need to edit
        // the others; granted or denied (or submitted) are view only
        final EasyUser sessionUser = getSessionUser();
        final boolean initialRequest = !getDataset().getPermissionSequenceList().hasSequenceFor(sessionUser);
        final PermissionSequence userSequence = getDataset().getPermissionSequenceList().getSequenceFor(sessionUser);
        final PermissionSequence.State status = initialRequest ? null : userSequence.getState();
        final boolean editMode = initialRequest || (State.Returned.equals(status));
        if (editMode)
        {
            LOGGER.debug("Edit panel");
            add(new PermissionRequestEditPanel("requestPanel", fromPage, new DatasetModel((Dataset) getDefaultModelObject())));
        }
        else
        {
            LOGGER.debug("View panel");
            add(new PermissionRequestViewPanel("requestPanel", fromPage, new DatasetModel((Dataset) getDefaultModelObject())));
        }

    }

    private void addIfInitialRequest(final Label label)
    {
        label.setVisible(!getDataset().getPermissionSequenceList().hasSequenceFor(getSessionUser()));
        add(label);
    }

    private State getRequestState()
    {
        PermissionSequenceList list = getDataset().getPermissionSequenceList();
        if (!list.hasSequenceFor(getSessionUser()))
            return null;
        return list.getSequenceFor(getSessionUser()).getState();
    }

    protected void pageBack() throws RestartResponseException
    {
        if (fromPage == null)
            throw new RestartResponseException(HomePage.class);
        fromPage.refresh();
        setResponsePage(fromPage);
    }
}
