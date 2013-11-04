package nl.knaw.dans.easy.web.main;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.business.bean.SystemStatus;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.admin.EditableContentPage;
import nl.knaw.dans.easy.web.admin.UsersOverviewPage2;
import nl.knaw.dans.easy.web.search.pages.AllWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.OurWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.TrashCanSearchResultPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.SecureEasyPageLink;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Panel
 * 
 * @author lobo
 */
public class ManagementBarPanel extends AbstractEasyStatelessPanel
{
    private static final long serialVersionUID = -4344141494726647837L;
    private static final Logger logger = LoggerFactory.getLogger(ManagementBarPanel.class);

    public static final String MY_WORK = "myWork";
    public static final String ALL_WORK = "allWork";
    public static final String OUR_WORK = "ourWork";
    public static final String TRASH_CAN = "trashCan";
    public static final String USER_INFO = "userInfo";
    public static final String EDITABLE_CONTENT = "editableContent";
    private static final String READ_ONLY = "systemIsReadOnly";

    public ManagementBarPanel(final String wicketId)
    {
        super(wicketId);

        SecureEasyPageLink allWorkLink = new SecureEasyPageLink(ALL_WORK, AllWorkSearchResultPage.class);
        allWorkLink.add(new Label("numberOfItemsInAllWork", new PropertyModel<Integer>(this, "numberOfItemsInAllWork")));
        add(allWorkLink);
        SecureEasyPageLink ourWorkLink = new SecureEasyPageLink(OUR_WORK, OurWorkSearchResultPage.class);
        ourWorkLink.add(new Label("numberOfItemsInOurWork", new PropertyModel<Integer>(this, "numberOfItemsInOurWork")));
        add(ourWorkLink);
        SecureEasyPageLink myWorkLink = new SecureEasyPageLink(MY_WORK, MyWorkSearchResultPage.class);
        myWorkLink.add(new Label("numberOfItemsInMyWork", new PropertyModel<Integer>(this, "numberOfItemsInMyWork")));
        add(myWorkLink);
        SecureEasyPageLink trashCanLink = new SecureEasyPageLink(TRASH_CAN, TrashCanSearchResultPage.class);
        trashCanLink.add(new Label("numberOfItemsInTrashcan", new PropertyModel<Integer>(this, "numberOfItemsInTrashcan")));
        add(trashCanLink);

        Link<Page> readOnlyLink = new Link<Page>(READ_ONLY)
        {
            private static final long serialVersionUID = 1L;

            public void onClick()
            {
                SystemStatus systemStatus = SystemStatus.instance();
                systemStatus.setReadOnly(!systemStatus.getReadOnly());
                setResponsePage(this.getPage());
            }
        };
        readOnlyLink.add(new Label("readOnly", new PropertyModel<SystemStatus>(SystemStatus.instance(), "readOnly")));
        add(readOnlyLink);

        add(new SecureEasyPageLink(USER_INFO, UsersOverviewPage2.class));
        add(new SecureEasyPageLink(EDITABLE_CONTENT, EditableContentPage.class));
    }

    // Note: the following members are much alike, maybe we can refactor this

    public int getNumberOfItemsInAllWork()
    {
        try
        {
            int numberOfItems = Services.getSearchService().getNumberOfItemsInAllWork(getSessionUser());
            logger.debug("The number of items in 'all work': " + numberOfItems);

            return numberOfItems;
        }
        catch (ServiceException e)
        {
            logger.error("Could not retrieve the number of items in 'all work'.", e);
            throw new InternalWebError();
        }
    }

    public int getNumberOfItemsInOurWork()
    {
        try
        {
            int numberOfItems = Services.getSearchService().getNumberOfItemsInOurWork(getSessionUser());
            logger.debug("The number of items in 'our work': " + numberOfItems);

            return numberOfItems;
        }
        catch (ServiceException e)
        {
            logger.error("Could not retrieve the number of items in 'our work'.", e);
            throw new InternalWebError();
        }
    }

    public int getNumberOfItemsInMyWork()
    {
        try
        {
            int numberOfItems = Services.getSearchService().getNumberOfItemsInMyWork(getSessionUser());
            logger.debug("The number of items in 'my work': " + numberOfItems);

            return numberOfItems;
        }
        catch (ServiceException e)
        {
            logger.error("Could not retrieve the number of items in 'my work'.", e);
            throw new InternalWebError();
        }
    }

    public int getNumberOfItemsInTrashcan()
    {
        try
        {
            int numberOfItems = Services.getSearchService().getNumberOfItemsInTrashcan(getSessionUser());
            logger.debug("The number of items in 'trashcan': " + numberOfItems);

            return numberOfItems;
        }
        catch (ServiceException e)
        {
            logger.error("Could not retrieve the number of items in 'trashcan'.", e);
            throw new InternalWebError();
        }
    }

}
