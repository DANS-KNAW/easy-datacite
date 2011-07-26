package nl.knaw.dans.common.wicket.components.pagebrowse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.wicket.components.CommonGPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * A panel for browsing through items on pages. A typical {@link PageBrowsePanel} looks like this:
 *
 * <pre>
 *      previous   |   1   |   ...   |   3   |   4   |   5   |   6   |   7   |   ...   |   9   |   next
 * </pre>
 *
 * @author ecco
 */
public class PageBrowsePanel extends CommonGPanel<PageBrowseData>
{
	private static final long serialVersionUID = 4385883898867962693L;

	public static final String	DEFAULT_SEPARATOR	= "|";

    public static final String          RK_PREVIOUS           = "PageBrowsePanel.previous";

    public static final String          RK_NEXT               = "PageBrowsePanel.next";

    private Set<PageBrowseLinkListener> linkListeners      = new HashSet<PageBrowseLinkListener>();


    public PageBrowsePanel(final String wicketId, final IModel<PageBrowseData> model)
    {
    	this(wicketId, model, null);
    }	
    
    public PageBrowsePanel(final String wicketId, final IModel<PageBrowseData> model, PageBrowseLinkListener listener)
    {
        super(wicketId, model);
        
        if (listener != null)
        	linkListeners.add( listener );
        
        ListView<PageBrowseLink> pageBrowse = new ListView<PageBrowseLink>("pages", new AbstractReadOnlyModel<List<PageBrowseLink>>()
			{
				private static final long	serialVersionUID	= -7716849873494368548L;
	
				public List<PageBrowseLink> getObject() 
				{
					return computeLinks();
				}
			}
        )
        {
            private static final long serialVersionUID = 0L;

            @Override
            protected void populateItem(ListItem<PageBrowseLink> item)
            {
                final int page = item.getIndex();
                if(page > 0)
                {
                    item.add(new Label("separator", DEFAULT_SEPARATOR));
                }
                else
                {
                    // no separator
                    item.add(new Label("separator", ""));
                }
                
                final PageBrowseLink plink = item.getModelObject();
                item.add(plink);
            }
        };
        add(pageBrowse);
    }

    public void addPageLinkListener(PageBrowseLinkListener listener)
    {
        linkListeners.add(listener);
    }

    public boolean removePageLinkListener(PageBrowseLinkListener listener)
    {
        return linkListeners.remove(listener);
    }
	
		
    @Override
    public boolean isVisible()
    {
    	return !(getCurrentPage() == 1 && getLastPage() == 1);
    }
        
    public int getCurrentPage()
    {
    	return getModelObject().getCurrentPage();
    }
    
    public int getLastPage()
    {
    	return getModelObject().getLastPage();
    }
    
    public int getWindowSize()
    {
    	return getModelObject().getWindowSize();
    }

    public int getWindowStart()
    {
    	return getModelObject().getWindowStart();
    }

    public int getWindowEnd()
    {
    	return getModelObject().getWindowEnd();
    }

    List<PageBrowseLink> computeLinks()
    {
    	PageBrowseData model = getModelObject();
        List<PageBrowseLink> links = new ArrayList<PageBrowseLink>();
        if (model.hasPrevious())
        {
            links.add(new PageBrowseLink(getCurrentPage() - 1, getPageSize(), new ResourceModel(RK_PREVIOUS, "previous"), true, linkListeners));
        }
        if (getWindowStart() > 1)
        {
            links.add(new PageBrowseLink(1, getPageSize(), new Model<String>("" + 1), true, linkListeners));
        }
        if (getWindowStart() > 1 + 1)
        {
            links.add(new PageBrowseLink(-1, getPageSize(), new Model<String>("..."), false, linkListeners));
        }
        for (int i = getWindowStart(); i <= getWindowEnd(); i++)
        {
            links.add(new PageBrowseLink(i, getPageSize(), new Model<String>("" + i), i != getCurrentPage(), linkListeners));
        }
        if (getWindowEnd() < getLastPage() - 1)
        {
            links.add(new PageBrowseLink(-1, getPageSize(), new Model<String>("..."), false, linkListeners));
        }
        if (getWindowEnd() < getLastPage())
        {
            links.add(new PageBrowseLink(getLastPage(), getPageSize(), new Model<String>("" + getLastPage()), true, linkListeners));
        }
        if (model.hasNext())
        {
            links.add(new PageBrowseLink(getCurrentPage() + 1, getPageSize(), new ResourceModel(RK_NEXT, "next"), true, linkListeners));
        }
        return links;
    }

	private int getPageSize()
	{
		return getModelObject().getPageSize();
	}
    
    // method for testing
    String printLinks()
    {
        StringBuilder sb = new StringBuilder();
        for (PageBrowseLink link : computeLinks())
        {
            sb.append(link.printLink());
            sb.append(DEFAULT_SEPARATOR);
        }
        return sb.toString();
    }

    
    /**
     * A link for browsing through items on pages.
     *
     * @author ecco
     */
    public class PageBrowseLink extends Link<String>
    {

        public static final String WI_PAGELINK = "pageLink";

        public static final String WI_PAGELINKTEXT = "pageLinkText";

        private static final long           serialVersionUID = -5052832307990433719L;
        private final int                   targetPage;
        private final int                   pageSize;
        private final IModel<String>        labelModel;
        private final boolean               enabled;
        private final Set<PageBrowseLinkListener> listeners;

        PageBrowseLink(int targetPage, int pageSize, IModel<String> labelModel, boolean enabled, Set<PageBrowseLinkListener> listeners)
        {
            super(WI_PAGELINK);
            this.pageSize = pageSize;
            this.targetPage = targetPage;
            this.labelModel = labelModel;
            this.enabled = enabled;
            this.listeners = listeners;
            add(new Label(WI_PAGELINKTEXT, labelModel));
        }

        public int getTargetPage()
        {
            return targetPage;
        }

        public int getTargetItemStart()
        {
            return ((targetPage * pageSize) - pageSize) + 1;
        }

        public IModel<String> getLabelModel()
        {
            return labelModel;
        }

        public boolean isEnabled()
        {
            return enabled;
        }
        
        @Override
        protected boolean getStatelessHint()
        {
            return false;
        }

        @Override
        public void onClick()
        {
        	PageBrowseData pbModel = PageBrowsePanel.this.getModelObject();
			pbModel.setCurrentPage(targetPage);
            for (PageBrowseLinkListener listener : listeners)
            {
                listener.onClick(this);
            }
        }
        
        String printLink()
        {
            return targetPage + " " + enabled + " " + labelModel.getObject();
        }

    }

}
