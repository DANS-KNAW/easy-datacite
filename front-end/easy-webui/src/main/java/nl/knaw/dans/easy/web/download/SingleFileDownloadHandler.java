/**
 *
 */
package nl.knaw.dans.easy.web.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.DownloadStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileDownloadHandler extends AbstractDownloadHandler
{

    public static final String DOWNLOAD_TYPE_FILE = "file";
    private static final long serialVersionUID = -3895017767546166939L;
    private static final Logger logger = LoggerFactory.getLogger(SingleFileDownloadHandler.class);

    private final FileDownloadResponse fileDownloadResponse;

    private FileContentWrapper fileContentWrapper;

    private InputStream ioStream;
    private boolean hasSecurityException;

    public SingleFileDownloadHandler(FileDownloadResponse fileDownloadResponse)
    {
        this.fileDownloadResponse = fileDownloadResponse;
        getFileContentWrapper();
    }

    public String getContentType()
    {
        return getURLConnection().getContentType();
    }

    public InputStream getInputStream() throws ResourceStreamNotFoundException
    {
        try
        {
            ioStream = getURLConnection().getInputStream();

            // logging for statistics is badly placed code
            DmoStoreId dmoStoreId = new DmoStoreId(fileDownloadResponse.getMandatoryStringParam(FileDownloadResponse.DATASET_ID));
            Dataset dataset = (Dataset) EasySession.get().getDataset(dmoStoreId);
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_FILE_REQUEST, new DatasetStatistics(dataset),
                    new DownloadStatistics(fileContentWrapper), new DisciplineStatistics(dataset));

            return ioStream;
        }
        catch (IOException e)
        {
            logger.error("Unable to get inputstream from URLConnection: ", e);
            throw new ResourceStreamNotFoundException(e);
        }
        catch (DownloadException e)
        {
            logger.error("Unable to get inputstream from URLConnection: ", e);
            throw new ResourceStreamNotFoundException(e);
        }
        catch (ServiceException e)
        {
            logger.error("Unable to get inputstream from URLConnection: ", e);
            throw new ResourceStreamNotFoundException(e);
        }
    }

    public void close() throws IOException
    {
        if (ioStream != null)
        {
            ioStream.close();
        }
    }

    public long length()
    {
        return getURLConnection().getContentLength();
    }

    public Time lastModifiedTime()
    {
        return Time.valueOf(getURLConnection().getLastModified());
    }

    private URLConnection getURLConnection()
    {
        URLConnection connection = null;
        try
        {
            connection = getFileContentWrapper().getURL().openConnection();
        }
        catch (IOException e)
        {
            logger.error("Unable to get a URLConnection: ", e);
            fileDownloadResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return connection;
    }

    public FileContentWrapper getFileContentWrapper()
    {
        if (fileContentWrapper == null)
        {
            EasySession session = (EasySession) Session.get();
            try
            {
                final DmoStoreId datasetStoreId = new DmoStoreId(fileDownloadResponse.getMandatoryStringParam(FileDownloadResponse.DATASET_ID));
                final DmoStoreId fileStoreId = new DmoStoreId(fileDownloadResponse.getMandatoryStringParam(FileDownloadResponse.SELECTED_ITEM));
                final Dataset dataset = (Dataset) session.getDataset(datasetStoreId);
                fileContentWrapper = Services.getItemService().getContent(session.getUser(), dataset, fileStoreId);
            }
            catch (CommonSecurityException e)
            {
                logger.warn("Unable to get a URLConnection: " + e.getMessage());
                hasSecurityException = true;
                fileContentWrapper = new FileContentWrapper(null);
                fileContentWrapper.setFileName("insufficientRights.html");
                fileContentWrapper.setURL(getMockURL());
            }
            catch (ServiceException e)
            {
                logger.error("Unable to get a URLConnection: ", e);
                fileDownloadResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        return fileContentWrapper;
    }

    public boolean hasSecurityException()
    {
        return hasSecurityException;
    }

    @Override
    public void setHeaders(WebResponse response)
    {
        response.setHeader("Content-Disposition", "inline; filename=" + getFileContentWrapper().getFileName());
    }

}
