package nl.knaw.dans.easy.web;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Error page for the Easy application
 * 
 * @author Herman Suijs
 * @author lobo
 */
public class ErrorPage extends AbstractEasyNavPage
{
	public static final String ERROR_PANEL      = "errorPanel";
    
    private ErrorLevelFeedbackMessageFilter errorFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR);

    public ErrorPage()
    {
    	if (getSession().getFeedbackMessages().messages(errorFilter).size() == 0)
    	{
    		errorMessage(EasyResources.DEFAULT_ERROR_MESSAGE);
    	}

    	addCommonFeedbackPanel(errorFilter);
    }
}
