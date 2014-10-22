package nl.knaw.dans.easy.web;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

/**
 * Easy web application home page.
 */
public class HomePage extends AbstractEasyNavPage {
    public static final String EDITABLE_HOMEPAGE_TEMPLATE = "/pages/HomePage.template";
    private static final String DEPOSIT = "navDeposit";

    public HomePage() {
        addCommonFeedbackPanel();
        add(new EasyEditablePanel("editablePanel", EDITABLE_HOMEPAGE_TEMPLATE));
        add(new BookmarkablePageLink<DepositIntroPage>(DEPOSIT, DepositIntroPage.class));
        StatisticsLogger.getInstance().logEvent(StatisticsEvent.START_PAGE_VISIT);
    }

}
