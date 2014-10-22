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

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
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

        RepeatingView listItems = new RepeatingView("listItems");
        RepeatingView listItems2 = new RepeatingView("listItems2");

        WebMarkupContainer item1 = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link1 = new SecureEasyPageLink("link", AllWorkSearchResultPage.class);
        link1.add(new Label("text", getString("page.allwork")));
        link1.add(new Label("numberOf", createAllWorkModel()));
        item1.add(link1);
        listItems.add(item1);

        WebMarkupContainer item2 = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link2 = new SecureEasyPageLink("link", OurWorkSearchResultPage.class);
        link2.add(new Label("text", getString("page.ourwork")));
        link2.add(new Label("numberOf", createOurWorkModel()));
        item2.add(link2);
        listItems.add(item2);

        WebMarkupContainer item3 = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link3 = new SecureEasyPageLink("link", MyWorkSearchResultPage.class);
        link3.add(new Label("text", getString("page.mywork")));
        link3.add(new Label("numberOf", createMyWorkModel()));
        item3.add(link3);
        listItems.add(item3);

        WebMarkupContainer item4 = new WebMarkupContainer(listItems.newChildId());
        SecureEasyPageLink link4 = new SecureEasyPageLink("link", TrashCanSearchResultPage.class);
        link4.add(new Label("text", getString("page.trashcan")));
        link4.add(new Label("numberOf", createTrashCanModel()));
        item4.add(link4);
        listItems.add(item4);

        WebMarkupContainer item5 = new WebMarkupContainer(listItems2.newChildId());
        SecureEasyPageLink link5 = new SecureEasyPageLink("link", UsersOverviewPage.class);
        link5.add(new Label("text", getString("page.users")));
        item5.add(link5);
        listItems2.add(item5);

        WebMarkupContainer item6 = new WebMarkupContainer(listItems2.newChildId());
        SecureEasyPageLink link6 = new SecureEasyPageLink("link", EditableContentPage.class);
        link6.add(new Label("text", getString("page.editableContent")));
        item6.add(link6);
        listItems2.add(item6);

        add(listItems);
        add(listItems2);
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
