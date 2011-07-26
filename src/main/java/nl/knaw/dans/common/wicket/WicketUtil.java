package nl.knaw.dans.common.wicket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.knaw.dans.common.wicket.components.CommonSession;
import nl.knaw.dans.common.wicket.components.HiddenComponent;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

public class WicketUtil
{
	/**
	 * Hides an element with a certain wicketId in a certain parent component.
	 * @param parentComponent the parent component, may be a page, panel, etc.
	 * @param wicketId the id of the wicket element that needs to be hidden
	 */
	public static void hide(MarkupContainer parentComponent, String wicketId)
	{
		HiddenComponent hiddenComponent = new HiddenComponent(wicketId);
		parentComponent.addOrReplace(hiddenComponent);
	}

    public static final String  COMMON_FEEDBACK_PANEL    = "commonFeedbackPanel";

    public static FeedbackPanel addCommonFeedbackPanel(MarkupContainer comp)
	{
		return addCommonFeedbackPanel(comp, (IFeedbackMessageFilter) null);
	}

    public static FeedbackPanel addCommonFeedbackPanel(MarkupContainer comp, IFeedbackMessageFilter filter)
    {
        FeedbackPanel commonFeedBackPanel = new FeedbackPanel(COMMON_FEEDBACK_PANEL, filter)
        {
			private static final long serialVersionUID = -8064284418808980432L;

			@Override
            public boolean isVisible()
            {
                return this.anyMessage();
            }
        };

        comp.add(commonFeedBackPanel).setOutputMarkupId(true);
        return commonFeedBackPanel;
    }    

    public static String commonMessage(Component reporter, final String messageKey, final int type, final String... param)
    {
        String propertyMessage;
        if (reporter != null) 
        	propertyMessage = reporter.getString(messageKey);
        else
        	propertyMessage = (String) new ResourceModel(messageKey).getObject();

        if (param != null && param.length > 0)
        {
        	for (int i = 0; i < param.length; i++)
        	{
        		if (param[i] != null)
        			propertyMessage = propertyMessage.replace("$"+ (i+1), param[i]);
        	}
        }
        
        final String message = getDisplayedMessage(propertyMessage, type);
        
       	Session.get().getFeedbackMessages().add(reporter, message, type);
       	Session.get().dirty();
        
    	return propertyMessage;
    }
    
    public static void clearMessages() {
    	Session.get().cleanupFeedbackMessages();
    }
    
    private static String getDisplayedMessage(final String message, final int type)
    {
    	String displayedMessage = "";
    	
        switch (type)
        {
        	case FeedbackMessage.INFO: 		
        	case FeedbackMessage.WARNING:	displayedMessage = message; break;
        	case FeedbackMessage.ERROR:
        	case FeedbackMessage.FATAL: 	displayedMessage = getMessageTime() + message; break;
        }
        return displayedMessage;
    }

    private static String getMessageTime()
    {
    	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date();
        return "[" + dateFormat.format(date) + "]   ";
    }
    
    public static boolean redirectToLastVisitedPage()
    {
    	String lastVisitedPageURL = CommonSession.get().getLastVisitedPageURL();
    	if (lastVisitedPageURL != null)
    	{
    		RequestCycle.get().setRequestTarget(new RedirectRequestTarget(lastVisitedPageURL));
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
}
