package nl.knaw.dans.easy.web.view.dataset;

import java.util.List;
import java.util.UUID;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.SecuredStreamingService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class VideoPanel extends AbstractDatasetModelPanel implements IHeaderContributor
{
    private static final Logger logger = LoggerFactory.getLogger(VideoPanel.class);

    private String streamingUrl = "";
    private String ticketValue = "";

    @SpringBean(name = "securedStreamingService")
    private SecuredStreamingService securedStreamingService;

    @SpringBean(name = "itemService")
    private ItemService itemService;

    public VideoPanel(String id, DatasetModel model, final List<FileItemVO> videoFiles, PageParameters pageParameters)
    {
        super(id, model);
        add(new Label("fileName", ""));
        for (FileItemVO videoFile : videoFiles)
        {
            addVideoFile(videoFile);
            // now only one video is show; break statement after the first video
            break;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.renderString("<script> var presentation = \"" + streamingUrl + "\"; </script><script> var ticket_value = \"" + ticketValue + "\"; </script>");
    }

    private void addVideoFile(final FileItemVO videoFile)
    {
        try
        {
            FileItemDescription description = itemService
                    .getFileItemDescription(EasySession.getSessionUser(), getDataset(), new DmoStoreId(videoFile.getSid()));
            List<KeyValuePair> metadata = description.getMetadataForAnonKnown();
            for (KeyValuePair kvp : metadata)
            {
                if (kvp.getKey().toLowerCase().equals("streaming url"))
                {
                    ticketValue = UUID.randomUUID().toString();
                    streamingUrl = kvp.getValue();
                    securedStreamingService.addSecurityTicketToResource(ticketValue, streamingUrl);
                }
            }
        }
        catch (ObjectNotAvailableException e)
        {
            errorMessage(EasyResources.NOT_FOUND);
            logger.error("Object not found: ", e);
            throw new InternalWebError();
        }
        catch (CommonSecurityException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
    }
}
