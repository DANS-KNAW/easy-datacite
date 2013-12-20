package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadRecord;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadActivityLogPanel extends Panel
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadActivityLogPanel.class);

    public static final String DOWNLOAD_CSV = "download_csv";

    private static final long serialVersionUID = 9110250938647271835L;

    @SpringBean(name = "datasetService")
    private DatasetService datasetService;

    @SpringBean(name = "userService")
    private UserService userService;

    private final Dataset dataset;
    private final EasyUser easyUser;

    private boolean downloadHistoryAvailable;

    private DownloadList downloadList = null;

    public DownloadActivityLogPanel(final String id, final Dataset dataset, final EasyUser easyUser)
    {
        super(id);
        this.dataset = dataset;
        this.easyUser = easyUser;
        add(new ResourceLink<WebResource>(DOWNLOAD_CSV, createCSVWebResource(dataset)));
        DateTime now = DateTime.now();
        makeDownloadList(now.getYear(), now.getMonthOfYear());
        setVisibility();
    }

    void makeDownloadList(int year, int month)
    {
        DateTime pDate = new DateTime(year, month, 1, 0, 0, 0, 0);
        try
        {
            DownloadHistory downloadHistory = datasetService.getDownloadHistoryFor(easyUser, dataset, pDate);
            if (downloadHistory != null)
            {
                downloadList = downloadHistory.getDownloadList();
                downloadHistoryAvailable = downloadList.getDownloadCount() != 0;
            }
            else
            {
                downloadHistoryAvailable = false;
                downloadList = null;
            }
        }
        catch (ServiceException e)
        {
            LOGGER.error("error getting downloader for activity log.", e);
        }
    }

    void setVisibility()
    {
        this.setVisible(isDownloadHistoryAvailableAvailable() && easyUser.hasRole(Role.ARCHIVIST));
    }

    private WebResource createCSVWebResource(final Dataset dataset)
    {
        WebResource export = new WebResource()
        {

            private static final long serialVersionUID = 2534427934241209655L;

            @Override
            public IResourceStream getResourceStream()
            {
                StringBuffer sb = new StringBuffer();
                for (DownloadRecord dr : downloadList.getRecords())
                {
                    EasyUser downloader = fetchDownloader(dr);
                    sb.append(dr.getDownloadTime());
                    sb.append(";");
                    sb.append(downloader == null ? "anonymous" : downloader.getId());
                    sb.append(";");
                    sb.append(downloader == null ? " " : downloader.getEmail());
                    sb.append(";");
                    sb.append(downloader == null ? " " : downloader.getOrganization());
                    sb.append(";");
                    sb.append(downloader == null ? " " : downloader.getFunction());
                    sb.append(";");
                    sb.append(dr.getPath());
                    sb.append(";\n");
                }
                return new StringResourceStream(sb.toString(), "text/csv");
            }

            private EasyUser fetchDownloader(DownloadRecord dr)
            {
                if (dr.getDownloaderId() != null && dr.getDownloaderId().trim().length() != 0)
                {
                    try
                    {
                        return userService.getUserById(easyUser, dr.getDownloaderId());
                    }
                    catch (ObjectNotAvailableException e)
                    {
                        LOGGER.error("error getting downloader object for activity log.", e);
                    }
                    catch (ServiceException e)
                    {
                        LOGGER.error("error getting user service for activity log.", e);
                    }
                }
                return null;
            }

            @Override
            protected void setHeaders(WebResponse response)
            {
                super.setHeaders(response);
                response.setAttachmentHeader(dataset.getDmoStoreId() + ".csv");
            }
        };
        export.setCacheable(false);
        return export;
    }

    public boolean isDownloadHistoryAvailableAvailable()
    {
        return downloadHistoryAvailable;
    }

}
