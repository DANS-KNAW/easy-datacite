package nl.knaw.dans.easy.web.view.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadRecord;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.Style;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityLogPanel extends AbstractEasyPanel
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogPanel.class);

    private static final long serialVersionUID = 7576727206422816545L;

    private static final List<Integer> MONTHS = Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});

    private final Dataset dataset;

    private DownloadActivityLogPanel downloadActivityLogPanel;

    @SpringBean(name = "datasetService")
    private DatasetService datasetService;

    @SpringBean(name = "userService")
    private UserService userService;

    public ActivityLogPanel(final String id, final Dataset dataset)
    {
        super(id);
        this.dataset = dataset;
        final ActivityLogForm activityLogForm = new ActivityLogForm("choiceForm");
        add(Style.ACTIVITY_LOG_PANEL_CONTRIBUTION);
        add(activityLogForm);
        displayDownloads(activityLogForm.getYear(), activityLogForm.getMonth());
        downloadActivityLogPanel = new DownloadActivityLogPanel("downloadActivityLogPanel", dataset, getSessionUser());
        addOrReplace(downloadActivityLogPanel);
    }

    private void displayDownloads(final int year, final int month)
    {
        final DownloadListPanel dlp = new DownloadListPanel("downloadListPanel", year, month);
        dlp.setVisible(dlp.isNotEmpty());
        addOrReplace(dlp);
    }

    private class DownloadListPanel extends Panel
    {

        private static final long serialVersionUID = -7996685875625497073L;
        private boolean notEmpty = false;

        public DownloadListPanel(final String id, final int year, final int month)
        {
            super(id);

            // show temporary info message
            // infoMessage("tempMessage");

            final DateTime pDate = new DateTime(year, month, 1, 0, 0, 0, 0);
            DownloadList downloadList;

            try
            {
                final DownloadHistory dlh = datasetService.getDownloadHistoryFor(getSessionUser(), dataset, pDate);
                if (dlh == null)
                {
                    downloadList = new DownloadList(DownloadHistory.LIST_TYPE_DATASET);
                }
                else
                {
                    downloadList = dlh.getDownloadList();
                    notEmpty = true;
                }
                add(new Label("downloadCount", Integer.toString(downloadList.getDownloadCount())));
                add(new Label("downloadedBytes", Long.toString(downloadList.getTotalBytes())).setVisible(downloadList.getTotalBytes() != 0));

                final Map<DateTime, List<DownloadRecord>> timeMap = downloadList.getDownloadsByTime();
                final List<DateTime> dates = new ArrayList<DateTime>(timeMap.keySet());

                final WebMarkupContainer timeViewContainer = new WebMarkupContainer("timeViewContainer");
                final Label detailsHeader = new Label("detailsHeader", "Details");
                detailsHeader.setVisible(hasPermissionForDetails());
                timeViewContainer.add(detailsHeader);
                add(timeViewContainer);
                final ListView<DateTime> timeView = createTimeView(timeMap, dates);
                timeViewContainer.setOutputMarkupId(true);
                timeViewContainer.add(timeView);
            }
            catch (final ServiceException e)
            {
                final String message = errorMessage(EasyResources.ERROR_RETRIEVING_DOWNLOAD_HISTORY);
                LOGGER.error(message, e);
                throw new InternalWebError();
            }
        }

        boolean isNotEmpty()
        {
            return notEmpty;
        }

    }

    private ListView<DateTime> createTimeView(final Map<DateTime, List<DownloadRecord>> timeMap, final List<DateTime> dates)
    {
        return new ListView<DateTime>("timeView", dates)
        {

            private static final long serialVersionUID = 1835277512292284607L;

            @Override
            protected void populateItem(final ListItem<DateTime> item)
            {
                final List<DownloadRecord> records = timeMap.get(item.getModelObject());

                item.add(new DateTimeLabel("downloadTime", getString(EasyResources.DATE_FORMAY_KEY), createDateTimeModel(item)));

                String displayName = "";
                String organization = "";
                String function = "";
                boolean detailsAvailable = false;
                try
                {
                    if (records != null)
                    {
                        if (records.get(0) != null)
                        {
                            detailsAvailable = records.get(0).getFileItemId() != null;
                            final EasyUser downloader = getDownloader(records.get(0).getDownloaderId());
                            if (!downloader.isAnonymous() && hasPerrmissionToViewDownloader(downloader))
                            {
                                displayName = downloader.getDisplayName();
                                organization = downloader.getOrganization();
                                function = downloader.getFunction();
                            }
                            else
                            {
                                displayName = "Anonymous";
                            }
                        }
                    }
                }
                catch (final ObjectNotAvailableException e)
                {
                    LOGGER.error("error getting downloader for activity.", e);
                }
                catch (final ServiceException e)
                {
                    LOGGER.error("error getting downloader for activity.", e);
                }

                item.add(new Label("displayName", displayName));
                item.add(new Label("organization", organization));
                item.add(new Label("function", function));
                item.add(new Label("fileCount", Integer.toString(records.size())).setVisible(detailsAvailable));

                if (detailsAvailable)
                {
                    final DetailsViewPanel detailsViewPanel = new DetailsViewPanel("detailsView", records);
                    item.add(createDetailsLink(detailsViewPanel));
                    item.add(detailsViewPanel);
                }
                else
                {
                    item.add(createDummyLink());
                    item.add(new Label("detailsView", getString("label.details.available")));
                }
            }
        };
    }

    private EasyUser getDownloader(final String downloaderId) throws ObjectNotAvailableException, ServiceException
    {
        if (downloaderId == null)
            return EasyUserAnonymous.getInstance();
        else
            return userService.getUserById(getSessionUser(), downloaderId);
    }

    private Model<DateTime> createDateTimeModel(final ListItem<DateTime> item)
    {
        return new Model<DateTime>()
        {

            private static final long serialVersionUID = -868918967174524401L;

            @Override
            public DateTime getObject()
            {
                return item.getModelObject();
            }
        };
    }

    private Label createDetailsLabel(final DetailsViewPanel detailsViewPanel)
    {
        final Label detailsLabel = new Label("detailsLabel", new Model<String>()
        {
            private static final long serialVersionUID = -3463837402258647445L;

            @Override
            public String getObject()
            {
                return detailsViewPanel.isShowing() ? getString("label.details.hide") : getString("label.details.show");
            }

        });
        detailsLabel.setOutputMarkupId(true);
        return detailsLabel;
    }

    private AjaxLink<Void> createDetailsLink(final DetailsViewPanel detailsViewPanel)
    {
        final Component detailsLabel = createDetailsLabel(detailsViewPanel);
        final AjaxLink<Void> detailsLink = new AjaxLink<Void>("detailsLink")
        {
            private static final long serialVersionUID = 7852411683843630456L;

            @Override
            public void onClick(final AjaxRequestTarget target)
            {
                detailsViewPanel.toggleDisplay();
                target.addComponent(detailsLabel);
                target.addComponent(detailsViewPanel);
            }
        };
        detailsLink.setVisible(hasPermissionForDetails());
        detailsLink.add(detailsLabel);
        return detailsLink;
    }

    private Component createDummyLink()
    {
        final ExternalLink detailsLink = new ExternalLink("detailsLink", "#");
        detailsLink.add(new Label("detailsLabel", new Model<String>("")));
        detailsLink.setVisible(false);
        return detailsLink;
    }

    private boolean hasPerrmissionToViewDownloader(final EasyUser downloader)
    {
        return isSessionUserArchivist() || isLogMyActionsOnFor(downloader) || isDepositorViewingGrantedRestrictedDownloadBy(downloader);
    }

    private boolean isSessionUserArchivist()
    {
        return getSessionUser().hasRole(Role.ARCHIVIST);
    }

    private boolean isLogMyActionsOnFor(final EasyUser downloader)
    {
        return !downloader.isAnonymous() && downloader.isLogMyActions();
    }

    private boolean hasPermissionForDetails()
    {
        return getSessionUser().hasRole(Role.ARCHIVIST) || getSessionUser().hasRole(Role.ADMIN) || dataset.hasDepositor(getSessionUser());
    }

    private boolean isDepositorViewingGrantedRestrictedDownloadBy(final EasyUser downloader)
    {
        return dataset.hasDepositor(getSessionUser()) && dataset.hasPermissionRestrictedItems() && dataset.isPermissionGrantedTo(downloader);
    }

    private class DetailsViewPanel extends Panel
    {
        private static final long serialVersionUID = -7736996737054567840L;

        private final List<DownloadRecord> records;
        private final List<DownloadRecord> emptyList = Collections.emptyList();

        private boolean showing;

        public DetailsViewPanel(final String wicketId, final List<DownloadRecord> records)
        {
            super(wicketId);
            this.records = records;
            setOutputMarkupId(true);
        }

        @Override
        protected void onBeforeRender()
        {
            addOrReplace(creteDetailsView());
            super.onBeforeRender();
        }

        private ListView creteDetailsView()
        {
            @SuppressWarnings({"rawtypes", "unchecked"})
            final ListView detailsView = new ListView("detailsView", showing ? records : emptyList)
            {
                private static final long serialVersionUID = -6012969128990248434L;

                @Override
                protected void populateItem(final ListItem item)
                {
                    final DownloadRecord record = (DownloadRecord) item.getDefaultModelObject();
                    item.add(new Label("path", record.getPath()));
                }

            };
            return detailsView;
        }

        public void toggleDisplay()
        {
            showing = !showing;
        }

        public boolean isShowing()
        {
            return showing;
        }
    }

    private class ActivityLogForm extends Form
    {
        private static final long serialVersionUID = -2310851219839528715L;
        private int year;
        private int month;

        public ActivityLogForm(final String wicketId)
        {
            super(wicketId);

            final DateTime dateTime = new DateTime();
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear();
            final List<Integer> years = new ArrayList<Integer>();
            for (int i = 2006; i <= year; i++)
            {
                years.add(i);
            }

            final DropDownChoice yearChoice = new DropDownChoice("yearChoice", new PropertyModel(this, "year"), years);
            yearChoice.setRequired(true);
            yearChoice.setNullValid(false);
            add(yearChoice);

            final DropDownChoice monthChoice = new DropDownChoice("monthChoice", new PropertyModel(this, "month"), MONTHS);
            monthChoice.setRequired(true);
            monthChoice.setNullValid(false);
            add(monthChoice);

            final SubmitLink submitButton = new SubmitLink("submitLink", new ResourceModel("button.text.submit"));
            add(submitButton);
        }

        public int getYear()
        {
            return year;
        }

        public void setYear(final int year)
        {
            this.year = year;
        }

        public int getMonth()
        {
            return month;
        }

        public void setMonth(final int month)
        {
            this.month = month;
        }

        @Override
        protected void onSubmit()
        {
            displayDownloads(year, month);
            downloadActivityLogPanel.makeDownloadList(year, month);
            downloadActivityLogPanel.setVisibility();
        }
    }
}
