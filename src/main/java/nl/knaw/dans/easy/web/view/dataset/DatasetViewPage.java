package nl.knaw.dans.easy.web.view.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.TemporaryUnAvailableException;
import nl.knaw.dans.common.wicket.components.SimpleTab;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.authn.LoginPage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.permission.DatasetPermissionsTab;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.https.RequireHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class DatasetViewPage extends AbstractEasyNavPage
{
    /**
     * In what way is the {@link DatasetViewPage} used.
     */
    public enum Mode
    {
        /**
         * Used for viewing a dataset.
         */
        VIEW,
        /**
         * Used for selecting a dataset.
         */
        SELECT
    }

    /**
     * Resource id.
     */
    public static final String  RI_TAB_DESCRIPTION     = "tab.description";

    /**
     * Resource id.
     */
    public static final String  RI_TAB_OVERVIEW        = "tab.overview";

    /**
     * Resource id.
     */
    public static final String  RI_TAB_FILEEXPLORER    = "tab.fileexplorer";

    /**
     * Resource id.
     */
    public static final String  RI_TAB_ADMINISTRATION  = "tab.administration";

    /**
     * Resource id.
     */
    public static final String  RI_TAB_PERMISSIONS     = "tab.permissions";
    
    /**
     * Resource id.
     */
    public static final String  RI_TAB_ACTIVITY_LOG     = "tab.activitylog";

    /**
     * Wicket id.
     */
    public static final String  WI_VIEW_TABS           = "tabs";

    public static final String  PM_DATASET_ID			= "id";
    public static final String  PM_VIEW_MODE			= "vm";
    public static final String  PM_TAB_INDEX			= "tab";
    public static final String  PM_REDIRECT_TO_LOGIN	= "rd";

    private static final Logger logger                 = LoggerFactory.getLogger(DatasetViewPage.class);

    private final Mode          mode;

    private boolean             initiated;

    private int                 tabIndex;

    private ContextParameters   contextParameters;

	private DatasetModel	datasetModel;
	
	private boolean mustLogin;

    public static String urlFor(Dataset dataset, int tabIndex, boolean mustLogin, Component component)
    {
        return urlFor(dataset.getStoreId(), tabIndex, mustLogin, component);
    }
    
    public static String urlFor(String datasetId, int tabIndex, boolean mustLogin, Component component)
    {
        PageParameters parameters = urlParametersFor(datasetId, tabIndex, mustLogin);
        String path = (String) component.urlFor(DatasetViewPage.class, parameters);
        String bookmarkableLink = RequestUtils.toAbsolutePath(path);
        logger.debug("Composed bookmarkable link: " + bookmarkableLink);
        return bookmarkableLink;
    }

    static PageParameters urlParametersFor(String datasetId, int tabIndex, boolean mustLogin)
    {
        PageParameters parameters = new PageParameters();
        parameters.add(PM_DATASET_ID, datasetId);
        if (tabIndex > 0)
            parameters.add(PM_TAB_INDEX, Integer.toString(tabIndex));
        if (mustLogin)
            parameters.add(PM_REDIRECT_TO_LOGIN, mustLogin ? "1" : "0");
        return parameters;
    }

    public DatasetViewPage(final PageParameters parameters)
    {
        super(parameters);
        logger.debug("Instantiating DatasetViewPage with PageParameters");
        String datasetId;
        try
        {
            datasetId = parameters.getString(PM_DATASET_ID);
            mode = parameters.getAsEnum(PM_VIEW_MODE, Mode.VIEW);
            tabIndex = parameters.getAsInteger(PM_TAB_INDEX, 0);
            mustLogin = "1".equals(parameters.getString(PM_REDIRECT_TO_LOGIN));
        }
        catch (Exception e)
        {
        	errorMessage(EasyResources.INSUFFICIENT_PARAMETERS);
            logger.error("Unable to read page parameters: ", e);
            throw new InternalWebError();
        }
        
        datasetModel = getDatasetModel(datasetId);

        // maybe link from user that got permission for the dataset or depositor visiting permissions tab
        if (mustLogin && !getEasySession().isAuthenticated())
        {
            redirectToInterceptPage(new LoginPage());
        }
    }

    public DatasetViewPage(String datasetId, Mode mode)
    {
        datasetModel = getDatasetModel(datasetId);
        this.mode = mode == null ? Mode.VIEW : mode;
    }
    
    public DatasetViewPage(Dataset dataset, Mode mode)
    {
        datasetModel = new DatasetModel(dataset);
        this.mode = mode == null ? Mode.VIEW : mode;
    }

    public DatasetViewPage(final DatasetModel datasetModel, Mode mode)
    {
    	this.datasetModel = datasetModel;
        this.mode = mode == null ? Mode.VIEW : mode;
    }
    
    private DatasetModel getDatasetModel(String storeId)
    {
        DatasetModel dm;
        try
        {
            dm = new DatasetModel(storeId);
        }
        catch (ObjectNotAvailableException e)
        {
            errorMessage(EasyResources.NOT_FOUND, storeId);
            logger.error("Object not found: ", e);
            throw new InternalWebError();
        }
        catch (TemporaryUnAvailableException e)
        {
            warningMessage(EasyResources.DATASET_UNDERCONSTRUCTION);
            InfoPage infoPage = new InfoPage(EasyWicketApplication.getProperty(EasyResources.DATASET_UNAVAILABLE));
            infoPage.setCallingClass(this.getClass());
            throw new RestartResponseException(infoPage);
        }
        catch (CommonSecurityException e)
        {
            if (getSessionUser().isAnonymous())
            {
                throw new RestartResponseAtInterceptPageException(LoginPage.class);
            }
            errorMessage(EasyResources.ILLEGAL_ACCESS);
            logger.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.ERROR_LOADING_MODEL_OBJECT, storeId);
            logger.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
        return dm;
    }
    
    protected Dataset getDataset()
    {
        return datasetModel.getObject();
    }

    protected boolean mustLogin()
    {
        return mustLogin;
    }

    @Override
    public ContextParameters getContextParameters()
    {
        if (contextParameters == null)
        {
            contextParameters = new ContextParameters(getSessionUser(), getDataset());
        }
        return contextParameters;
    }

    public Mode getMode()
    {
        return mode;
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
        if (datasetModel.isInvalidated())
        {
        	refresh();
        }
        
        super.onBeforeRender();
    }
    
    public void init()
    {
        addCommonFeedbackPanel();

    	add(new Label("title", getDataset().getPreferredTitle()));

        Link backToListLink = new Link("backToList")
        {
            private static final long serialVersionUID = 2282643032675018321L;

            @Override
            public void onClick()
            {
                Page page = DatasetViewPage.this.getEasySession().getRedirectPage(DatasetViewPage.class);
                if (page != null && page instanceof AbstractEasyPage)
                {
                    ((AbstractEasyPage) page).refresh();
                }
                if (page != null)
                {
                    setResponsePage(page);
                }
            }
            
            @Override
            public boolean isVisible()
            {
            	return DatasetViewPage.this.getEasySession().hasRedirectPage(DatasetViewPage.class);
            }
        };
        add(backToListLink);
        
        Link selectLink = new Link("selectLink")
        {

            private static final long serialVersionUID = 3396158896016830001L;

            @Override
            public void onClick()
            {
                logger.debug("Select link clicked.");
                // TODO what else should happen here? Inform the session? Redirect to ...
            }
 
        };
        selectLink.setVisible(Mode.SELECT.equals(getMode()));
        add(selectLink);

        InfosegmentPanel infosegmentPanel = new InfosegmentPanel("infosegmentPanel", datasetModel, mode);
        infosegmentPanel.setOutputMarkupId(true);
        add(infosegmentPanel);

        // add the tabs 
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(getOverviewTab());
        tabs.add(getDescriptionTab());
        tabs.add(getDataFilesTab());
        tabs.add(getAdministrationTab());
        tabs.add(getPermissionsTab());
        tabs.add(getActivityLogTab());
        TabbedPanel tabbedPanel = new TabbedPanel(WI_VIEW_TABS, tabs)
        {

            private static final long serialVersionUID = 5796914238322673704L;
            
            // this results in urls like /datasets/id/easy-dataset:22/tab/5
            @Override
            protected WebMarkupContainer newLink(String linkId, int index)
            {
                BookmarkablePageLink<String> link = 
                    new BookmarkablePageLink<String>(linkId, DatasetViewPage.class, 
                            DatasetViewPage.urlParametersFor(getDataset().getStoreId(), index, mustLogin()));
                return link;
            }
        };
        add(tabbedPanel);
        
        if (tabIndex >= 0 && tabIndex < tabbedPanel.getTabs().size())
        {
            logger.debug("Setting selected tab. tabIndex=" + tabIndex);
            tabbedPanel.setSelectedTab(tabIndex);
        }
        
        // logging for statistics
        StatisticsLogger.getInstance().logEvent(StatisticsEvent.DATASET_VIEWED, new DatasetStatistics(getDataset()), new DisciplineStatistics(getDataset()));
    }

	private SimpleTab getActivityLogTab()
	{
		return new SimpleTab(new ResourceModel(RI_TAB_ACTIVITY_LOG))
		{
		    private static final long serialVersionUID = 4264378003403493028L;

		    @Override
		    public Panel getPanel(String panelId)
		    {
		        return new ActivityLogPanel(panelId, getDataset());
		    }
		    
		    @Override
		    public boolean isVisible()
		    {
		    	return !getSessionUser().isAnonymous();
		    }
		};
	}

	private SimpleTab getAdministrationTab()
	{
		return new SimpleTab(new ResourceModel(RI_TAB_ADMINISTRATION))
		{
		    private static final long serialVersionUID = 6695260253761809149L;

		    @Override
		    public Panel getPanel(String panelId)
		    {
		        return new AdministrationPanel(panelId, datasetModel);
		    }
		    
		    @Override
		    public boolean isVisible()
		    {
		    	return getSessionUser().hasRole(Role.ARCHIVIST);
		    }

		};
	}

	private SimpleTab getDataFilesTab()
	{
		StringResourceModel titleModel = new StringResourceModel(
				RI_TAB_FILEEXPLORER,
				null,
				new Object[] { new Model<String>()
				{
					private static final long	serialVersionUID	= -7961575318107788527L;

					/**
					 * A model is used, because these values might change
					 * when the dataset changes.
					 */
					@Override
					public String getObject() 
					{
						// NOTE: GK: move this logic to business layer 
						//		 i.e. add a method getVisibleFileCount(EasyUser user)
						EasyUser user = EasySession.getSessionUser();
						int count = 0;
						if(user.isAnonymous()) {
							count = getDataset().getVisibleToFileCount(VisibleTo.ANONYMOUS);
						} else if(user.hasRole(Role.ARCHIVIST) || getDataset().hasDepositor(user)) {
							count = getDataset().getTotalFileCount();
						} else {
							count = getDataset().getVisibleToFileCount(VisibleTo.ANONYMOUS) +
									getDataset().getVisibleToFileCount(VisibleTo.KNOWN);
							
							if(getDataset().hasPermissionRestrictedItems() && getDataset().isPermissionGrantedTo(user))
								count += getDataset().getVisibleToFileCount(VisibleTo.RESTRICTED_REQUEST);
							
							if(getDataset().hasGroupRestrictedItems() && getDataset().isGroupAccessGrantedTo(user))
								count += getDataset().getVisibleToFileCount(VisibleTo.RESTRICTED_GROUP);
						}
						return "" + count;
					}
				} }
			);
		return new SimpleTab(titleModel)
        {
            private static final long serialVersionUID = -1312420240988923158L;

            @Override
            public Panel getPanel(final String panelId)
            {
                return new DataFilesPanel(panelId, datasetModel);
            }

        };
	}

	private SimpleTab getDescriptionTab()
	{
		return new SimpleTab(new ResourceModel(RI_TAB_DESCRIPTION))
        {

            private static final long serialVersionUID = -1312420240988923158L;

            @Override
            public Panel getPanel(final String panelId)
            {
                return new DescriptionPanel(panelId, datasetModel);
            }

        };
	}

	private SimpleTab getOverviewTab()
	{
		return new SimpleTab(new ResourceModel(RI_TAB_OVERVIEW))
        {

            private static final long serialVersionUID = 236015675731297661L;

            @Override
            public Panel getPanel(final String panelId)
            {
                return new OverviewPanel(panelId, getDataset());
            }

        };
	}
	
	@SuppressWarnings("serial")
	private SimpleTab getPermissionsTab()
	{
        final StringResourceModel titleModel = new StringResourceModel(
        		RI_TAB_PERMISSIONS, 
        		null, 
        		new Object[] { 
					/**
					 * Models are used, because these values might change
					 * when the dataset changes.
					 */
        			new Model<String>() {
        				public String getObject() 
        				{
        					return getDataset().getPermissionSequenceList().getPermissionSequences(State.Submitted).size() +"";
        				};
        			},
        			new Model<String>() {
        				public String getObject() 
        				{
        					return getDataset().getPermissionSequenceList().getPermissionSequences().size() +"";
        				};
        			},
        		}
        	);
        final SimpleTab tab = new SimpleTab(titleModel)
        {
            private static final long serialVersionUID = -5421636926598790323L;

            @Override
            public Panel getPanel(String panelId)
            {
                return new DatasetPermissionsTab(panelId, datasetModel, getSessionUser(),
                        (AbstractEasyPage) getPage());
            }
            
            @Override
            public boolean isVisible()
            {
            	return DatasetPermissionsTab.required(getSessionUser(), getDataset());
            }

        };
        return tab;
	}


	@Override
    public String getPageTitlePostfix()
    {
        String datasetTitle = getDataset().getPreferredTitle();
        String pageTitlePostfix = super.getPageTitlePostfix();
        return String.format(pageTitlePostfix, datasetTitle);
    }

}
