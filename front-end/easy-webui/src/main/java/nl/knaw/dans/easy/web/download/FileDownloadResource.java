package nl.knaw.dans.easy.web.download;

/**
 * A WebResource for download streaming.
 * 
 * @author Eko Indarto
 */

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton WebResource for file download.
 */
public class FileDownloadResource extends WebResource {

    private static final long serialVersionUID = -8238946591987397379L;

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadResource.class);

    @Override
    public IResourceStream getResourceStream() {
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse(getParameters());
        ((EasySession) Session.get()).put(FileDownloadResponse.class.getName(), fileDownloadResponse);
        return fileDownloadResponse.getResourceStream();
    }

    @Override
    protected void setHeaders(WebResponse response) {
        try {
            FileDownloadResponse fileDownloadResponse = (FileDownloadResponse) ((EasySession) Session.get()).get(FileDownloadResponse.class.getName());
            // TODO Known issue: Wicket doesn't like us sending a response,
            // but has no method for sending failure to the client (that I know of).
            if (fileDownloadResponse == null) {
                response.getHttpServletResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } else if (fileDownloadResponse.hasStatusError()) {
                response.getHttpServletResponse().sendError(fileDownloadResponse.getStatusCode());
                return;
            }

            fileDownloadResponse.getResourceStream().setHeaders(response);

        }
        catch (IOException e) {
            logger.error("Unable to send an error response: ", e);
        }
    }

}
