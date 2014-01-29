package nl.knaw.dans.easy.web.view.dataset;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoPanel extends AbstractDatasetModelPanel implements IHeaderContributor
{

    private static final long serialVersionUID = -5695419613165470561L;
    private static final Logger logger = LoggerFactory.getLogger(VideoPanel.class);
    private static final String TICKET_SERVER_ADDRESS = "http://tstreaming11.dans.knaw.nl/acl/ticket";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String ROLE = "EasyUser";
    private static final String FAR_FAR_AWAY = "3000000000";
    
    private String streamingUrl = "";
    private String ticketValue = "";

    private boolean initiated;

    public VideoPanel(String id, DatasetModel model, final List<ItemVO> videoFiles, PageParameters pageParameters)
    {
        super(id, model);

        final Dataset dataset = getDataset();

        add(new Label("fileName", ""));
        add(new Label("streamingUrl", ""));
        for (ItemVO videoFile : videoFiles)
        {
        	addVideoFile(videoFile, dataset);
        	// now only one video is show; break statement after the first video
            break;
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

    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.renderString("<script> var presentation = \"" + streamingUrl + "\"; </script><script> var ticket_value = \"" + ticketValue + "\"; </script>");
    }

    private void init()
    {
    }

    private void addVideoFile(final ItemVO videoFile, final Dataset dataset)
    {
        try
        {
            replace(new Label("fileName", videoFile.getName()));
            FileItemDescription description = Services.getItemService().getFileItemDescription(EasySession.getSessionUser(), dataset,
                    new DmoStoreId(videoFile.getSid()));
            List<KeyValuePair> metadata = description.getMetadataForAnonKnown();
            for (KeyValuePair kvp : metadata)
            {
                if (kvp.getKey().toLowerCase().equals("streamingurl"))
                {
                	ticketValue = UUID.randomUUID().toString();
                    final String ticketResponse = sendTicketRequest(kvp.getValue(), ticketValue);
                    if (ticketResponse != null)
                    {
                        streamingUrl = kvp.getValue();
                    	replace(new Label("streamingUrl", kvp.getValue()));
                    }
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

    public String sendTicketRequest(final String resourceId, final String ticket)
    {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(TICKET_SERVER_ADDRESS);

		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) {
				try {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				} catch (ClientProtocolException e) {
					errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
					logger.error("Video file ticket http-response failed: ", e);
					throw new InternalWebError();
				} catch (ParseException e) {
					errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
					logger.error("Video file ticket http-response failed: ", e);
					throw new InternalWebError();
				} catch (IOException e) {
					errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
					logger.error("Video file ticket http-response failed: ", e);
					throw new InternalWebError();
				}
			}
		};

		StringEntity entity = postContent(resourceId, ticket);
		entity.setContentType(new BasicHeader("Content-Type", "text/xml"));
		httppost.setEntity(entity);
		String responseBody;
		try {
			responseBody = httpclient.execute(httppost, responseHandler);
		} catch (ClientProtocolException e) {
			errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
			logger.error("Video file ticket http-post failed: ", e);
			throw new InternalWebError();
		} catch (IOException e) {
			errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
			logger.error("Video file ticket http-post failed: ", e);
			throw new InternalWebError();
		}

		try {
			httpclient.close();
		} catch (IOException e) {
			errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
			logger.error("Video file ticket; closing httpclient failed: ", e);
			throw new InternalWebError();
		}
		return responseBody;
    }

    private StringEntity postContent(final String resourceId, final String ticket)
    {

        String content = "<fsxml><properties><ticket>" + ticket + "</ticket><uri>" + resourceId + "</uri><role>" + ROLE + "</role>" + 
        		"<ip>" + IP_ADDRESS + "</ip><expiry>" + FAR_FAR_AWAY + "</expiry></properties></fsxml>";
        try
        {
            return new StringEntity(content);
        }
        catch (UnsupportedEncodingException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
    }

//    private String getActiveTickets()
//    {
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            HttpGet httpget = new HttpGet(TICKET_SERVER_ADDRESS);
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>()
//            {
//
//                public String handleResponse(final HttpResponse response)
//                {
//                    try
//                    {
//                        int status = response.getStatusLine().getStatusCode();
//                        if (status >= 200 && status < 300)
//                        {
//                            HttpEntity entity = response.getEntity();
//                            return entity != null ? EntityUtils.toString(entity) : null;
//                        }
//                        else
//                        {
//                            throw new ClientProtocolException("Unexpected response status: " + status);
//                        }
//                    }
//                    catch (ClientProtocolException e)
//                    {
//                        errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                        logger.error("Video file ticket http-response failed: ", e);
//                        throw new InternalWebError();
//                    }
//                    catch (ParseException e)
//                    {
//                        errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                        logger.error("Video file ticket http-response failed: ", e);
//                        throw new InternalWebError();
//                    }
//                    catch (IOException e)
//                    {
//                        errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                        logger.error("Video file ticket http-response failed: ", e);
//                        throw new InternalWebError();
//                    }
//                }
//            };
//            
//            String responseBody;
//			try {
//				responseBody = httpclient.execute(httpget, responseHandler);
//			} catch (ClientProtocolException e) {
//                errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                logger.error("Video file ticket http-post failed: ", e);
//                throw new InternalWebError();
//			} catch (IOException e) {
//                errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                logger.error("Video file ticket http-post failed: ", e);
//                throw new InternalWebError();
//			}
//            // System.out.println(responseBody);
//            try {
//				httpclient.close();
//			} catch (IOException e) {
//                errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
//                logger.error("Video file ticket; closing httpclient failed: ", e);
//                throw new InternalWebError();
//			}
//            return responseBody;
//    }
    
//  private String getIpAddress()
//  {
//     	InetAddress IP;
//  		try {
//  			return InetAddress.getLocalHost().getHostAddress();
//  		} catch (UnknownHostException e) {
//              final String message = errorMessage(EasyResources.INTERNAL_ERROR);
//              logger.error(message, e);
//              throw new InternalWebError();
//  		}
//  }
//  
  	

}
