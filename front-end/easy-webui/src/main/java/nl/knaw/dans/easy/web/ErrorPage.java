package nl.knaw.dans.easy.web;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error page for the Easy application
 * 
 * @author Herman Suijs
 * @author lobo
 */
public class ErrorPage extends AbstractEasyNavPage {
    public static final String ERROR_PANEL = "errorPanel";
    private static final Logger log = LoggerFactory.getLogger(ErrorPage.class);

    private ErrorLevelFeedbackMessageFilter errorFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR);

    public ErrorPage() {
        if (getSession().getFeedbackMessages().messages(errorFilter).size() == 0) {
            log.error(DEFAULT_ERROR_MESSAGE, new Throwable().fillInStackTrace());
            errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
        }

        addCommonFeedbackPanel(errorFilter);
    }

    public ErrorPage(final String message, final String... params) {
        if (getSession().getFeedbackMessages().messages(errorFilter).size() == 0) {
            errorMessage(message, params);
        }

        addCommonFeedbackPanel(errorFilter);
    }
}
