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
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityLogPanel extends AbstractEasyPanel
{
	private static final Logger	LOGGER = LoggerFactory.getLogger(ActivityLogPanel.class);

	private static final long serialVersionUID = 7576727206422816545L;
    
    private static final List<Integer> MONTHS = Arrays.asList(
            new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
    
    private final Dataset dataset;
    
    private boolean initiated;

    public ActivityLogPanel(String id, Dataset dataset)
    {
        super(id);
        this.dataset = dataset;
    }
    
    public Dataset getDataset()
    {
        return dataset;
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
        ActivityLogForm activityLogForm = new ActivityLogForm("choiceForm");
        add(activityLogForm);
        displayDownloads(activityLogForm.getYear(), activityLogForm.getMonth());
    }
    
    private void displayDownloads(int year, int month)
    {
    	DownloadListPanel dlp = new DownloadListPanel("downloadListPanel", year, month);
    	dlp.setVisible(dlp.isNotEmpty());
        addOrReplace(dlp);
    }
    
    private class DownloadListPanel extends Panel
    {
        
        private static final long serialVersionUID = -7996685875625497073L;
        private boolean notEmpty = false;
        
        public DownloadListPanel(String id, int year, int month)
        {
            super(id);
            
            // show temporary info message
            //infoMessage("tempMessage");
            
            DateTime pDate = new DateTime(year, month, 1, 0, 0, 0, 0);
            DownloadList downloadList;
            
            try
            {
                DownloadHistory dlh = Services.getDatasetService().getDownloadHistoryFor(getSessionUser(), dataset, pDate);
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
                add(new Label("downloadedBytes", Long.toString(downloadList.getTotalBytes())));
                
                final Map<DateTime, List<DownloadRecord>> timeMap  = downloadList.getDownloadsByTime();
                List<DateTime> dates = new ArrayList<DateTime>(timeMap.keySet());
                
                final WebMarkupContainer timeViewContainer = new WebMarkupContainer("timeViewContainer");
                Label detailsHeader = new Label("detailsHeader", "Details");
                detailsHeader.setVisible(getSessionUser().hasRole(Role.ARCHIVIST) || 
						getSessionUser().hasRole(Role.ADMIN) || 
						dataset.hasDepositor(getSessionUser()));
                timeViewContainer.add(detailsHeader);
                add(timeViewContainer);
                ListView<DateTime> timeView = new ListView<DateTime>("timeView", dates)
                {

                    private static final long serialVersionUID = 1835277512292284607L;

                    @Override
                    protected void populateItem(final ListItem<DateTime> item)
                    {
                        final List<DownloadRecord> records = timeMap.get(item.getModelObject());
                        
                        item.add(new DateTimeLabel("downloadTime", getString(EasyResources.DATE_FORMAY_KEY), new Model<DateTime>()
                        {

                            private static final long serialVersionUID = -868918967174524401L;

                            @Override
                            public DateTime getObject()
                            {
                            	return item.getModelObject();
                            }
                        }));
                        
                        String displayName = "";
                        String organization = "";
                        String function = "";
                        try {
                        	if(records!=null && records.get(0)!=null && records.get(0).getDownloaderId()!=null) {
								EasyUser downloader = Services.getUserService().getUserById(getSessionUser(), records.get(0).getDownloaderId());
								if(downloader!=null && ( 
										getSessionUser().hasRole(Role.ARCHIVIST) || 
										(!downloader.isAnonymous() && downloader.isLogMyActions()) ||
										(dataset.hasDepositor(getSessionUser()) && dataset.hasPermissionRestrictedItems() && dataset.isPermissionGrantedTo(downloader)) )) {
									displayName = downloader.getDisplayName();
									organization = downloader.getOrganization();
									function = downloader.getFunction();
								} else {
									displayName = "Anonymous";
								}
                        	}
						} catch (ObjectNotAvailableException e) {
							LOGGER.error("error getting downloader for activity.", e );
						} catch (ServiceException e) {
							LOGGER.error("error getting downloader for activity.", e );
						}
                        
						item.add(new Label("displayName", displayName));
						item.add(new Label("organization", organization));
						item.add(new Label("function", function));
                        item.add(new Label("fileCount", Integer.toString(records.size())));
                        
                        final DetailsViewPanel detailsViewPanel = new DetailsViewPanel("detailsView", records);
                        item.add(detailsViewPanel);
                        
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
                                                
                        final AjaxLink<Void> detailsLink = new AjaxLink<Void>("detailsLink")
                        {
                            private static final long serialVersionUID = 7852411683843630456L;

                            @Override
                            public void onClick(AjaxRequestTarget target)
                            {
                                detailsViewPanel.toggleDisplay();
                                target.addComponent(detailsLabel);
                                target.addComponent(detailsViewPanel);                           
                            }
                        };
                        detailsLink.setVisible(getSessionUser().hasRole(Role.ARCHIVIST) || 
                        						getSessionUser().hasRole(Role.ADMIN) || 
                        						dataset.hasDepositor(getSessionUser()));
                        detailsLink.add(detailsLabel);
                        item.add(detailsLink);
                                                
                    }
                };
                timeViewContainer.setOutputMarkupId(true);
                timeViewContainer.add(timeView);
            }
            catch (ServiceException e)
            {
                final String message = errorMessage(EasyResources.ERROR_RETRIEVING_DOWNLOAD_HISTORY);
                LOGGER.error(message, e);
                throw new InternalWebError();
            }
        }
        
        boolean isNotEmpty() {
        	return notEmpty;
        }

    }
    
    private class DetailsViewPanel extends Panel
    {
        private static final long serialVersionUID = -7736996737054567840L;
        
        private final List<DownloadRecord> records;
        private final List<DownloadRecord> emptyList = Collections.emptyList();
        
        private boolean showing;
        
        public DetailsViewPanel(String wicketId, List<DownloadRecord> records)
        {
            super(wicketId);
            this.records = records;
            setOutputMarkupId(true);
        }
        
        @Override
        protected void onBeforeRender()
        {
            ListView detailsView = new ListView("detailsView", showing ? records : emptyList)
            {
                private static final long serialVersionUID = -6012969128990248434L;

                @Override
                protected void populateItem(ListItem item)
                {
                    DownloadRecord record = (DownloadRecord) item.getDefaultModelObject();
                    item.add(new Label("path", record.getPath()));
                }

            };
            addOrReplace(detailsView);
            super.onBeforeRender();
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
        
        public ActivityLogForm(String wicketId)
        {
            super(wicketId);
            
            DateTime dateTime = new DateTime();
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear();
            final List<Integer> years = new ArrayList<Integer>();
            for (int i = 2006; i <= year; i++)
            {
                years.add(i);
            }
            
            DropDownChoice yearChoice = new DropDownChoice("yearChoice", new PropertyModel(this, "year"), years);
            yearChoice.setRequired(true);
            yearChoice.setNullValid(false);
            add(yearChoice);
            
            DropDownChoice monthChoice = new DropDownChoice("monthChoice", new PropertyModel(this, "month"), MONTHS);
            monthChoice.setRequired(true);
            monthChoice.setNullValid(false);
            add(monthChoice);
            
            SubmitLink submitButton = new SubmitLink("submitLink", new ResourceModel("button.text.submit"));
            add(submitButton);
        }

        public int getYear()
        {
            return year;
        }

        public void setYear(int year)
        {
            this.year = year;
        }

        public int getMonth()
        {
            return month;
        }

        public void setMonth(int month)
        {
            this.month = month;
        }

        @Override
        protected void onSubmit()
        {
            displayDownloads(year, month);
        }
        
    }

}
