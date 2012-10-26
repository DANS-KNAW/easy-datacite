package nl.knaw.dans.easy.web.view.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadRecord;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
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

    private final Dataset dataset;

    private final EasyUser easyUser;

    private boolean downloadHistoryAvailable;

    private boolean initiated;
    private DownloadList downloadList = null;

    public DownloadActivityLogPanel(final String id, final Dataset dataset, final EasyUser easyUser)
    {
        super(id);
        this.dataset = dataset;
        this.easyUser = easyUser;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        this.setVisible(isDownloadHistoryAvailableAvailable() && easyUser.hasRole(Role.ARCHIVIST));
        super.onBeforeRender();
    }

    private void init()
    {
        add(new ResourceLink(DOWNLOAD_CSV, getCSVWebResource(dataset)));
        try
        {
            DownloadHistory downloadHistory = Services.getDatasetService().getDownloadHistoryFor(easyUser, dataset, new DateTime());
            if (downloadHistory != null)
            {
                downloadHistoryAvailable = true;
                downloadList = downloadHistory.getDownloadList();
            }
        }
        catch (ServiceException e)
        {
            LOGGER.error("error getting downloader for activity log.", e);
        }
    }

    private WebResource getCSVWebResource(final Dataset dataset)
    {
        WebResource export = new WebResource()
        {

            private static final long serialVersionUID = 2534427934241209655L;

            @Override
            public IResourceStream getResourceStream()
            {
                StringBuffer sb = new StringBuffer();

                final List<DownloadRecord> downloadRecord = downloadList.getRecords();

                if (downloadRecord != null)
                {
                    for (DownloadRecord dr : downloadRecord)
                    {
                        EasyUser downloader;
                        try
                        {
                            downloader = Services.getUserService().getUserById(easyUser, dr.getDownloaderId());
                            if (downloader != null && downloader.isLogMyActions())
                            {
                                sb.append(dr.getDownloadTime());
                                sb.append(";");
                                sb.append(downloader.getId());
                                sb.append(";");
                                sb.append(downloader.getEmail());
                                sb.append(";");
                                if (downloader.getOrganization() != null)
                                {
                                    sb.append(downloader.getOrganization());
                                }
                                else
                                {
                                    sb.append(" ");
                                }
                                sb.append(";");
                                if (downloader.getFunction() != null)
                                {
                                    sb.append(downloader.getFunction());
                                }
                                else
                                {
                                    sb.append(" ");
                                }
                                sb.append(";\n");
                            }
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
                }
                return new StringResourceStream(sb.toString(), "text/csv");
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
