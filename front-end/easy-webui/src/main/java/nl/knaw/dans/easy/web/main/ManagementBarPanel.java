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

import org.apache.wicket.markup.html.basic.Label;
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

    public static final String MY_WORK = "myWork";
    public static final String ALL_WORK = "allWork";
    public static final String OUR_WORK = "ourWork";
    public static final String TRASH_CAN = "trashCan";
    public static final String USER_INFO = "userInfo";
    public static final String EDITABLE_CONTENT = "editableContent";

    @SpringBean(name = "searchService")
    private SearchService searchService;

    public ManagementBarPanel(final String wicketId) {
        super(wicketId);

        SecureEasyPageLink allWorkLink = new SecureEasyPageLink(ALL_WORK, AllWorkSearchResultPage.class);
        allWorkLink.add(new Label("numberOfItemsInAllWork", createAllWorkModel()));
        add(allWorkLink);
        SecureEasyPageLink ourWorkLink = new SecureEasyPageLink(OUR_WORK, OurWorkSearchResultPage.class);
        ourWorkLink.add(new Label("numberOfItemsInOurWork", createOurWorkModel()));
        add(ourWorkLink);
        SecureEasyPageLink myWorkLink = new SecureEasyPageLink(MY_WORK, MyWorkSearchResultPage.class);
        myWorkLink.add(new Label("numberOfItemsInMyWork", createMyWorkModel()));
        add(myWorkLink);
        SecureEasyPageLink trashCanLink = new SecureEasyPageLink(TRASH_CAN, TrashCanSearchResultPage.class);
        trashCanLink.add(new Label("numberOfItemsInTrashcan", createTrashCanModel()));
        add(trashCanLink);

        add(new SecureEasyPageLink(USER_INFO, UsersOverviewPage.class));
        add(new SecureEasyPageLink(EDITABLE_CONTENT, EditableContentPage.class));
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
