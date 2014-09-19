package nl.knaw.dans.easy.web.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.DownloadZipStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipDownloadHandler extends AbstractDownloadHandler {

    private static final long serialVersionUID = -6033391606229853640L;

    private static final Logger logger = LoggerFactory.getLogger(ZipDownloadHandler.class);

    private final FileDownloadResponse fileDownloadResponse;

    private FileInputStream inStream;
    private ZipFileContentWrapper contentWrapper;

    private boolean hasSecurityException;

    public ZipDownloadHandler(FileDownloadResponse fileDownloadResponse) {
        this.fileDownloadResponse = fileDownloadResponse;
        getContentWrapper();
    }

    @Override
    public void setHeaders(WebResponse response) {
        String fileName = getContentWrapper().getFilename();
        if (hasSecurityException) {
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        }
    }

    public void close() throws IOException {
        if (inStream != null) {
            inStream.close();
        }
        deleteZipFile();
    }

    public String getContentType() {
        if (hasSecurityException) {
            return "text/html";
        } else {
            return "application/zip";
        }
    }

    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        try {
            inStream = new FileInputStream(getZipFile());

            // logging for statistics is badly placed code.
            DmoStoreId datasetId = new DmoStoreId(fileDownloadResponse.getMandatoryStringParam(FileDownloadResponse.DATASET_ID));
            Dataset dataset = (Dataset) EasySession.get().getDataset(datasetId);
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_DATASET_REQUEST, new DatasetStatistics(dataset),
                    new DownloadZipStatistics(contentWrapper), new DisciplineStatistics(dataset));
        }
        catch (FileNotFoundException e) {
            throw new ResourceStreamNotFoundException(e);
        }
        catch (DownloadException e) {
            throw new ResourceStreamNotFoundException(e);
        }
        catch (ServiceException e) {
            throw new ResourceStreamNotFoundException(e);
        }
        return inStream;
    }

    public long length() {
        return getZipFile().length();
    }

    public Time lastModifiedTime() {
        return Time.valueOf(getZipFile().lastModified());
    }

    private ZipFileContentWrapper getContentWrapper() {
        if (contentWrapper == null) {
            List<RequestedItem> requestedItems = fileDownloadResponse.getDownloadRequestItems();
            EasySession session = EasySession.get();
            try {
                DmoStoreId datasetId = new DmoStoreId(fileDownloadResponse.getMandatoryStringParam(FileDownloadResponse.DATASET_ID));
                Dataset dataset = (Dataset) session.getDataset(datasetId);
                contentWrapper = Services.getItemService().getZippedContent(session.getUser(), dataset, requestedItems);
            }
            catch (CommonSecurityException e) {
                logger.warn("Insufficient rights on zip download: ", e);
                hasSecurityException = true;
                contentWrapper = new ZipFileContentWrapper();
                contentWrapper.setFilename("insufficientRights.html");
                contentWrapper.setZipFile(getMockFile());
            }
            catch (ServiceException e) {
                logger.error("Unable to get a zipped file for download: ", e);
                fileDownloadResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        return contentWrapper;
    }

    private File getZipFile() {
        ZipFileContentWrapper contentWrapper = getContentWrapper();
        return contentWrapper.getZipFile();
    }

    private boolean deleteZipFile() throws IOException {
        boolean deleted = false;
        if (contentWrapper != null && !hasSecurityException) {
            deleted = contentWrapper.deleteZipFile();
        }
        return deleted;
    }

}
