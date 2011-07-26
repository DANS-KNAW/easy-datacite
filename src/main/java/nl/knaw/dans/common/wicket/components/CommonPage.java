package nl.knaw.dans.common.wicket.components;

import nl.knaw.dans.common.wicket.WicketUtil;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WicketURLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extending this class gives the extender access to common Wicket
 * methods like the WicketUtil.redirectToLastVisitedPage()
 *  
 * @author lobo
 */
public class CommonPage extends WebPage
{
	private static final Logger LOGGER                 = LoggerFactory.getLogger(CommonPage.class);

	public CommonPage()
	{
		super();
		init();
	}

	public CommonPage(PageParameters parameters)
	{
		super(parameters);
		init();
	}

	public CommonPage(IModel<?> model)
	{
		super(model);
		init();
	}

	public CommonPage(IPageMap map)
	{
		super(map);
		init();
	}

	public CommonPage(IPageMap map, IModel<?> model)
	{
		super(map, model);
		init();
	}

	public CommonPage(IPageMap pageMap, PageParameters parameters)
	{
		super(pageMap, parameters);
		init();
	}

	private void init()
	{
		RequestCycle cycle = RequestCycle.get();
		String URL;
		if (cycle.getRequest() instanceof WebRequest && ((WebRequest)cycle.getRequest()).isAjax())
		{
			URL = cycle.urlFor(cycle.getRequest().getPage()).toString();
		}
		else
		{
			URL = "/" + cycle.getRequest().getURL();
			URL = WicketURLEncoder.FULL_PATH_INSTANCE.encode(URL);
		}
		
		CommonSession.get().setCurrentPage(URL, this.getClass());
	}
	
	protected FeedbackPanel addCommonFeedbackPanel()
    {
    	return WicketUtil.addCommonFeedbackPanel(this);
    }
	
	protected FeedbackPanel addCommonFeedbackPanel(IFeedbackMessageFilter filter)
    {
    	return WicketUtil.addCommonFeedbackPanel(this, filter);
    }	

	public String infoMessage(final String messageKey)
    {
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO);
    }
    
    public String infoMessage(final String messageKey, final String... param)
    {
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO, param);
    }
    
    public String warningMessage(final String messageKey)
    {        
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING);
    }
    
    public String warningMessage(final String messageKey, final String param)
    {
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING, param);
    }
    
    public String errorMessage(final String messageKey)
    {        
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR);
    }
    
    public String errorMessage(final String messageKey, final String... param)
    {
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }
    
    public String fatalMessage(final String messageKey)
    {        
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL);
    }
    
    public String fatalMessage(final String messageKey, final String... param)
    {
    	return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL, param);
    }
	
    public void hide(String wicketId)
    {
    	WicketUtil.hide(this, wicketId);
    }


}
