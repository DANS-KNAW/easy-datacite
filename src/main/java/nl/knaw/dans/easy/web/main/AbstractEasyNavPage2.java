package nl.knaw.dans.easy.web.main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.popup.HelpPopup;
import nl.knaw.dans.common.wicket.components.search.SearchBar2;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.authn.LogoffLink;
import nl.knaw.dans.easy.web.authn.RegistrationPage;
import nl.knaw.dans.easy.web.authn.UserInfoPage;
import nl.knaw.dans.easy.web.authn.login.LoginPage;
import nl.knaw.dans.easy.web.common.HelpFileReader;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.search.pages.AdvSearchPage;
import nl.knaw.dans.easy.web.search.pages.BrowsePage;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyRequestsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.PublicSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.SearchAllSearchResultPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.SystemReadonlyLink;
import nl.knaw.dans.easy.web.template.VersionPanel;
import nl.knaw.dans.easy.web.wicket.SecureEasyPageLink;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IPageMap;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract template for every page within the EASY application.
 * 
 * @author lobo
 */
public abstract class AbstractEasyNavPage2 extends AbstractEasyPage
{

    private static final long serialVersionUID = -5373290220504946463L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractEasyNavPage.class);

    private static final String EASY_LOGO_LINK = "easyLogoLink";
    private static final String MANAGEMENT_BAR_PANEL = "managementBarPanel2";
    private static final String EASY_VERSION = "easyVersion";
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String DEPOSIT = "navDeposit";
    private static final String LOGOFF = "logoff";
    private static final String SETTINGS = "myPersonalInfoLink";
    private static final String DISPLAY_NAME = "displayName";
    private static final String MY_DATASETS = "myDatasets";
    private static final String MY_REQUESTS = "myRequests";
    private static final String SEARCH_PANEL = "navSearchPanel";
    private static final String HOME_PAGE = "homePage";
    private static final String BROWSE_PAGE = "browsePage";
    private static final String ADVANCED_SEARCH_PAGE = "advancedSearchPage";
    private static final String DISCLAIMER_URL = "http://www.dans.knaw.nl/sites/default/files/file/archief/DisclaimerEASY.pdf";
    public static final String EDITABLE_ADMIN_BANNER_TEMPLATE = "/pages/AdminBanner.template";

    private static final int MAX_NAME_LENGTH = 30;

    // keep results, otherwise we have a search request for every isVisible call
    private int numDatasets = 0;
    private boolean isNumDatasetsRetrieved = false;
    private int numRequests = 0;
    private boolean isNumRequestsRetrieved = false;

    /**
     * Default constructor.
     */
    public AbstractEasyNavPage2()
    {
        super();
        init();
    }

    /**
     * Constructor adds wicket components.
     * 
     * @param parameters
     *        Parameters for this page.
     * @see org.apache.wicket.markup.html.WebPage#WebPage()
     */
    public AbstractEasyNavPage2(final PageParameters parameters)
    {
        super(parameters);
        init();
    }

    /**
     * Constructor with model.
     * 
     * @param model
     *        Model attached to the page.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(IModel)
     */
    public AbstractEasyNavPage2(final IModel<?> model)
    {
        super(model);
        init();
    }

    /**
     * Constructor with PageMap.
     * 
     * @param map
     *        PageMap attached.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(PageMap)
     */
    public AbstractEasyNavPage2(final IPageMap map)
    {
        super(map);
        init();
    }

    /**
     * Constructor with PageMap and Model.
     * 
     * @param map
     *        PageMap attached.
     * @param model
     *        Model attached.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(PageMap, IModel)
     */
    public AbstractEasyNavPage2(final IPageMap map, final IModel<?> model)
    {
        super(map, model);
        init();
    }

    /**
     * Constructor with PageMap and Parameters.
     * 
     * @param pageMap
     *        IPageMap
     * @param parameters
     *        PageParameters
     */
    public AbstractEasyNavPage2(final IPageMap pageMap, final PageParameters parameters)
    {
        super(pageMap, parameters);
        init();
    }

    /**
     * Initialization for all constructors.
     */
    private void init()
    {
        // logo
        add(new BookmarkablePageLink<HomePage>(EASY_LOGO_LINK, HomePage.class));

        // search bar
        add(new SearchBar2(SEARCH_PANEL, isArchivistOrAdmin() ? SearchAllSearchResultPage.class : PublicSearchResultPage.class));
        add(new HelpPopup("searchHelpPopup", "Search", new HelpFileReader("Search").read()));
        add(new EasyEditablePanel("adminBanner", EDITABLE_ADMIN_BANNER_TEMPLATE));

        // personal bar
        add(new PageLink<LoginPage>(LOGIN, LoginPage.class)
        {
            private static final long serialVersionUID = -2538869070667617524L;

            @Override
            public boolean isVisible()
            {
                return getSessionUser().isAnonymous();
            }

            @Override
            public void onClick()
            {
                // Enable redirection to this page which is viewed before login
                ((EasySession) getSession()).setRedirectPage(LoginPage.class, getPage());
                setResponsePage(LoginPage.class);
            }

        });
        add(new SecureEasyPageLink(MY_DATASETS, MyDatasetsSearchResultPage.class)
        {
            private static final long serialVersionUID = -69304959956597268L;

            @Override
            public boolean isVisible()
            {
                boolean visible = true;

                if (getSessionUser().isAnonymous())
                {
                    visible = false;
                }
                else
                {
                    // hide when the user has no datasets
                    if (!hasDatasets())
                        visible = false;
                }

                return visible;
            }
        });
        add(new SecureEasyPageLink(MY_REQUESTS, MyRequestsSearchResultPage.class)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible()
            {
                boolean visible = true;

                if (getSessionUser().isAnonymous())
                {
                    visible = false;
                }
                else
                {
                    // hide when the user has no requests
                    if (!hasRequests())
                        visible = false;
                }

                return visible;
            }
        });
        add(new LogoffLink(LOGOFF));

        if (!getSessionUser().isAnonymous())
        {
            // user name
            Label displayNameLabel = new Label(DISPLAY_NAME, new PropertyModel(this, "displayNameLabelString"));
            add(displayNameLabel);
            if (isDisplayNameToLong())
            {
                // add 'tooltip'
                displayNameLabel.add(new AttributeModifier("title", true, new PropertyModel(this, "fullDisplayNameLabelString")));
            }

            // settings link
            add(new Link(SETTINGS)
            {
                private static final long serialVersionUID = 927863641840108643L;

                @Override
                public void onClick()
                {
                    setResponsePage(new UserInfoPage(false, true));
                }
            });

            hide(REGISTER);
        }
        else
        {
            hide(DISPLAY_NAME);
            hide(SETTINGS);

            add(new BookmarkablePageLink<RegistrationPage>(REGISTER, RegistrationPage.class));
        }
        add(new BookmarkablePageLink<DepositIntroPage>(DEPOSIT, DepositIntroPage.class));

        // main bar
        add(new BookmarkablePageLink<HomePage>(HOME_PAGE, HomePage.class));
        add(new BookmarkablePageLink<BrowsePage>(BROWSE_PAGE, BrowsePage.class));
        add(new BookmarkablePageLink<AdvSearchPage>(ADVANCED_SEARCH_PAGE, AdvSearchPage.class));

        // management bar
        ManagementBarPanel2 mgmBar = new ManagementBarPanel2(MANAGEMENT_BAR_PANEL);
        add(mgmBar);

        add(new SystemReadonlyLink());

        // footer
        add(createDisclaimerLink());
        add(new VersionPanel(EASY_VERSION));
    }

    @Override
    public void refresh()
    {
        // do my own refreshing?
        logger.debug(">>>> REFRESH called");
    }

    @Override
    public void detachModels()
    {
        super.detachModels();

        // the value will be retrieved again next time it is needed
        isNumDatasetsRetrieved = false;
    }

    /**
     * Determine if the user has requests which need to be handled
     * 
     * @return true if so, false otherwise
     */
    private boolean hasRequests()
    {
        boolean result = true;

        if (!isNumRequestsRetrieved)
        {
            // retrieve value
            try
            {
                numRequests = Services.getSearchService().getNumberOfRequests(getSessionUser());
                if (numRequests == 0)
                    result = false;

                // update
                isNumRequestsRetrieved = true;

                logger.debug("The number of requests: " + numRequests);
            }
            catch (ServiceException e)
            {
                logger.error("Could not retrieve the number of requests.", e);
                throw new InternalWebError();
            }
        }
        else
        {
            // use previously retrieved value

            if (numRequests == 0)
                result = false;

        }
        return result;
    }

    /**
     * Determine if the user has datasets
     * 
     * @return true if so, false otherwise
     */
    private boolean hasDatasets()
    {
        boolean result = true;

        if (!isNumDatasetsRetrieved)
        {
            // retrieve value

            try
            {
                numDatasets = Services.getSearchService().getNumberOfDatasets(getSessionUser());
                if (numDatasets == 0)
                    result = false;

                // update
                isNumDatasetsRetrieved = true;

                logger.debug("The number of datasets: " + numDatasets);
            }
            catch (ServiceException e)
            {
                logger.error("Could not retrieve the number of datasets.", e);
                throw new InternalWebError();
            }
        }
        else
        {
            // use previously retrieved value
            if (numDatasets == 0)
                result = false;
        }

        return result;
    }

    public String getFullDisplayNameLabelString()
    {
        final EasyUser user = getSessionUser();

        // user display name
        String displayName = user.getDisplayName();
        displayName = displayName.trim();

        // Add roles
        displayName += getDisplayRolesLabelString(user);

        return displayName;
    }

    // truncates name part if to long
    public String getDisplayNameLabelString()
    {
        final EasyUser user = getSessionUser();

        // user display name
        String displayName = user.getDisplayName();
        // Truncate user name part to a maximum length
        // and indicate truncation with ellipsis (...)
        String truncationIndicator = "...";
        displayName = displayName.trim();
        if (displayName.length() > MAX_NAME_LENGTH)
        {
            displayName = displayName.substring(0, MAX_NAME_LENGTH - truncationIndicator.length()) + truncationIndicator;
        }

        // Add roles
        displayName += getDisplayRolesLabelString(user);

        return displayName;
    }

    private String getDisplayRolesLabelString(final EasyUser user)
    {
        String displayRoles = "";

        Set<Role> roles = new HashSet<Role>(user.getRoles());
        roles.remove(Role.USER);
        if (roles.size() > 0)
        {
            displayRoles += " (";
            Iterator<Role> roleIt = roles.iterator();
            while (roleIt.hasNext())
            {
                displayRoles += getString("role." + roleIt.next().toString());
                if (roleIt.hasNext())
                    displayRoles += ", ";
            }
            displayRoles += ")";
        }

        return displayRoles;
    }

    private boolean isDisplayNameToLong()
    {
        final EasyUser user = getSessionUser();

        // user display name
        String displayName = user.getDisplayName();
        // Truncate user name part to a maximum length
        // and indicate truncation with ellipsis (...)
        displayName = displayName.trim();
        if (displayName.length() > MAX_NAME_LENGTH)
            return true;
        else
            return false;
    }

    private ExternalLink createDisclaimerLink()
    {
        String disclaimerLinkText = getLocalizer().getString("page.disclaimerLinkText", this);
        ExternalLink disclaimerLink = new ExternalLink("disclaimerLink", DISCLAIMER_URL, disclaimerLinkText);
        return disclaimerLink;

    }
}
