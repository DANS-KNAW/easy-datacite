package nl.knaw.dans.easy.web.view.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.StringHeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoPanel extends AbstractDatasetModelPanel implements IHeaderContributor
{

    private static final long serialVersionUID = -5695419613165470561L;
    // CHANGE THIS!!!
    private static final String VIDEO_EXTENSION = ".xml";
    private static final Logger logger = LoggerFactory.getLogger(VideoPanel.class);

    private List<String> streamingUrls = new ArrayList<String>();

    private boolean initiated;

    public VideoPanel(String id, DatasetModel model, PageParameters pageParameters)
    {
        super(id, model);

        final EasyUser user = getEasySession().getUser();
        final Dataset dataset = getDataset();
        final boolean seesAll = seesAll(dataset, user);

        try
        {
            add(new Label("fileName", ""));
            add(new Label("streamingUrl", ""));
            List<FileItemVO> videoFiles = getVideoFiles(Data.getFileStoreAccess().getDatasetFiles(model.getDmoStoreId()));
            if (videoFiles.size() > 0)
            {
                replace(new Label("fileName", videoFiles.get(0).getName()));
                FileItemDescription description = Services.getItemService().getFileItemDescription(EasySession.getSessionUser(), dataset,
                        new DmoStoreId(videoFiles.get(0).getSid()));
                List<KeyValuePair> metadata = description.getMetadataForAnonKnown();
                for (KeyValuePair kvp : metadata)
                {
                    if (kvp.getKey().toLowerCase().equals("streamingurl"))
                    {
                        replace(new Label("streamingUrl", kvp.getValue()));
                        streamingUrls.add(kvp.getValue());
                    }
                }
            }
        }
        catch (StoreAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ObjectNotAvailableException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (CommonSecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ServiceException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isInitiated()
    {
        return initiated;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
    }

    private boolean seesAll(final Dataset dataset, final EasyUser user)
    {
        if (user == null || user.isAnonymous())
            return false;
        if (user.hasRole(Role.ARCHIVIST))
            return true;
        if (!dataset.hasDepositor(user))
            return false;
        return dataset.getAdministrativeState().equals(DatasetState.DRAFT) || dataset.getState().equals("Active");
    }

    private List<FileItemVO> getVideoFiles(final List<FileItemVO> datasetFiles)
    {

        List<FileItemVO> videoFiles = new ArrayList<FileItemVO>();
        for (FileItemVO fileitem : datasetFiles)
        {
            if (fileitem.getName().endsWith(VIDEO_EXTENSION))
            {
                videoFiles.add(fileitem);
            }
        }
        return videoFiles;
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        // response.renderString("<script> var presentation = \"/domain/dans/_urn:nbn:nl:ui:13-f2o-u22\"; </script>");
        response.renderString("<script> var presentation = \"" + streamingUrls.get(0) + "\"; </script>");
    }
}
