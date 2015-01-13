package nl.knaw.dans.easy.web.main;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.web.admin.EditableContentPage;
import nl.knaw.dans.easy.web.admin.UsersOverviewPage;
import nl.knaw.dans.easy.web.search.pages.AllWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.OurWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.TrashCanSearchResultPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.SecureEasyPageLink;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Panel
 * 
 * @author lobo
 */
public class ManagementBarPanel extends AbstractEasyStatelessPanel {
    private static final long serialVersionUID = -4344141494726647837L;
    private static final Logger logger = LoggerFactory.getLogger(ManagementBarPanel.class);

    @SpringBean(name = "searchService")
    private SearchService searchService;

    public ManagementBarPanel(final String wicketId) {
        super(wicketId);

        RepeatingView listItemsWithCount = new RepeatingView("listItemsWithCount");
        RepeatingView listItemsWithoutCount = new RepeatingView("listItemsWithoutCount");

        listItemsWithCount.add(createListLinkWithCount(listItemsWithCount, AllWorkSearchResultPage.class, "page.allwork", createAllWorkModel()));
        listItemsWithCount.add(createListLinkWithCount(listItemsWithCount, OurWorkSearchResultPage.class, "page.ourwork", createOurWorkModel()));
        listItemsWithCount.add(createListLinkWithCount(listItemsWithCount, MyWorkSearchResultPage.class, "page.mywork", createMyWorkModel()));
        listItemsWithCount.add(createListLinkWithCount(listItemsWithCount, TrashCanSearchResultPage.class, "page.trashcan", createTrashCanModel()));

        listItemsWithoutCount.add(createListLinkWithoutCount(listItemsWithoutCount, UsersOverviewPage.class, "page.users"));
        listItemsWithoutCount.add(createListLinkWithoutCount(listItemsWithoutCount, EditableContentPage.class, "page.editableContent"));

        add(listItemsWithCount);
        add(listItemsWithoutCount);
    }

    private WebMarkupContainer createListLinkWithCount(RepeatingView listItems, Class linkItem, String name, LoadableDetachableModel<Integer> numberOf) {
        WebMarkupContainer item = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link = new SecureEasyPageLink("link", linkItem);
        link.add(new Label("text", getString(name)));
        link.add(new Label("numberOf", numberOf));
        item.add(link);
        return item;
    }

    private WebMarkupContainer createListLinkWithoutCount(RepeatingView listItems, Class linkItem, String name) {
        WebMarkupContainer item = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link = new SecureEasyPageLink("link", linkItem);
        link.add(new Label("text", getString(name)));
        item.add(link);
        return item;
    }

    // Note: the following members are much alike, maybe we can refactor this

    private LoadableDetachableModel<Integer> createAllWorkModel() {
        return new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                try {
                    int numberOfItems = searchService.getNumberOfItemsInAllWork(getSessionUser());
                    logger.debug("The number of items in 'all work': " + numberOfItems);
                    return numberOfItems;
                }
                catch (ServiceException e) {
                    logger.error("Could not retrieve the number of items in 'all work'.", e);
                    throw new InternalWebError();
                }
            }
        };
    }

    private LoadableDetachableModel<Integer> createOurWorkModel() {
        return new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                try {
                    int numberOfItems = searchService.getNumberOfItemsInOurWork(getSessionUser());
                    logger.debug("The number of items in 'our work': " + numberOfItems);

                    return numberOfItems;
                }
                catch (ServiceException e) {
                    logger.error("Could not retrieve the number of items in 'our work'.", e);
                    throw new InternalWebError();
                }
            }
        };
    }

    private LoadableDetachableModel<Integer> createMyWorkModel() {
        return new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                try {
                    int numberOfItems = searchService.getNumberOfItemsInMyWork(getSessionUser());
                    logger.debug("The number of items in 'my work': " + numberOfItems);

                    return numberOfItems;
                }
                catch (ServiceException e) {
                    logger.error("Could not retrieve the number of items in 'my work'.", e);
                    throw new InternalWebError();
                }
            }
        };
    }

    private LoadableDetachableModel<Integer> createTrashCanModel() {
        return new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                try {
                    int numberOfItems = searchService.getNumberOfItemsInTrashcan(getSessionUser());
                    logger.debug("The number of items in 'trashcan': " + numberOfItems);

                    return numberOfItems;
                }
                catch (ServiceException e) {
                    logger.error("Could not retrieve the number of items in 'trashcan'.", e);
                    throw new InternalWebError();
                }
            }
        };
    }

}
