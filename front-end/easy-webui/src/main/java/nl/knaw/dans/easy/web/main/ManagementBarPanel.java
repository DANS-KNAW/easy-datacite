package nl.knaw.dans.easy.web.main;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.web.admin.EditableContentPage;
import nl.knaw.dans.easy.web.admin.UsersOverviewPage;
import nl.knaw.dans.easy.web.search.pages.AllWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.OurWorkSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.TrashCanSearchResultPage;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.SecureEasyPageLink;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
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

        RepeatingView datasetPages = new RepeatingView("datasetPages");
        RepeatingView userPages = new RepeatingView("userPages");
        RepeatingView otherPages = new RepeatingView("otherPages");

        createChild(datasetPages).add(createCountedLink(AllWorkSearchResultPage.class, "page.allwork", createAllWorkModel()));
        createChild(datasetPages).add(createCountedLink(OurWorkSearchResultPage.class, "page.ourwork", createOurWorkModel()));
        createChild(datasetPages).add(createCountedLink(MyWorkSearchResultPage.class, "page.mywork", createMyWorkModel()));
        createChild(datasetPages).add(createCountedLink(TrashCanSearchResultPage.class, "page.trashcan", createTrashCanModel()));
        createChild(otherPages).add(createLink(EditableContentPage.class, "page.editableContent"));

        // role users would repeat all users, other states not wanted by https://drivenbydata.atlassian.net/browse/EASY-853
        createChild(userPages).add(createLink(UsersOverviewPage.class, "page.users.all"));
        createChild(userPages).add(createFilteredLink("state", State.ACTIVE.name()));
        createChild(userPages).add(createFilteredLink("state", State.BLOCKED.name()));
        createChild(userPages).add(createFilteredLink("state", State.REGISTERED.name()));
        createChild(userPages).add(createFilteredLink("role", Role.ARCHIVIST.name()));
        createChild(userPages).add(createFilteredLink("role", Role.ADMIN.name()));

        add(datasetPages);
        add(userPages);
        add(otherPages);
    }

    private MarkupContainer createChild(RepeatingView listItems) {
        MarkupContainer child = new WebMarkupContainer(listItems.newChildId());
        listItems.add(child);
        return child;
    }

    private MarkupContainer createCountedLink(Class<? extends AbstractEasyNavPage> linkItem, String name, LoadableDetachableModel<Integer> numberOf) {
        return createLink(linkItem, name).add(new Label("numberOf", numberOf));
    }

    private MarkupContainer createLink(Class<? extends AbstractEasyNavPage> linkItem, String name) {
        Label label = new Label("text", getString(name));
        return new SecureEasyPageLink("link", linkItem).add(label);
    }

    private MarkupContainer createFilteredLink(String pageParameKey, String pageParamValue) {
        PageParameters pageParams = new PageParameters();
        pageParams.add(pageParameKey, pageParamValue);
        Label label = new Label("text", getString("page.users." + pageParameKey + "." + pageParamValue));
        return new SecureEasyPageLink("link", UsersOverviewPage.class, pageParams).add(label);
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
