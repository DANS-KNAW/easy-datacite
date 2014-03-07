package nl.knaw.dans.easy.web.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.wicket.components.CommonPage;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.statistics.HttpRequestStatistics;
import nl.knaw.dans.easy.web.statistics.PageClassStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.wicket.AjaxEventListener;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IPageMap;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract template for every page within the EASY application.
 * 
 * @author lobo
 */
public abstract class AbstractEasyPage extends CommonPage implements EasyResources
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5373290220504946433L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractEasyPage.class);

    private Map<String, List<AjaxEventListener>> ajaxEventListenersMap;

    private ContextParameters contextParameters;

    public static final String PAGE_TITLE = "pageTitle";

    /**
     * Default constructor.
     */
    public AbstractEasyPage()
    {
        super();
        init();
    }

    /**
     * Constructor adds wicket components.
     * 
     * @param parameters
     *        Parameters for this page.
     * @see org.apache.wicket.markup.html.WebPage#WebPage()
     */
    public AbstractEasyPage(final PageParameters parameters)
    {
        super(parameters);
        init();
    }

    /**
     * Constructor with model.
     * 
     * @param model
     *        Model attached to the page.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(IModel)
     */
    public AbstractEasyPage(final IModel<?> model)
    {
        super(model);
        init();
    }

    /**
     * Constructor with PageMap.
     * 
     * @param map
     *        PageMap attached.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(PageMap)
     */
    public AbstractEasyPage(final IPageMap map)
    {
        super(map);
        init();
    }

    /**
     * Constructor with PageMap and Model.
     * 
     * @param map
     *        PageMap attached.
     * @param model
     *        Model attached.
     * @see org.apache.wicket.markup.html.WebPage#WebPage(PageMap, IModel)
     */
    public AbstractEasyPage(final IPageMap map, final IModel model)
    {
        super(map, model);
        init();
    }

    /**
     * Constructor with PageMap and Parameters.
     * 
     * @param pageMap
     *        IPageMap
     * @param parameters
     *        PageParameters
     */
    public AbstractEasyPage(final IPageMap pageMap, final PageParameters parameters)
    {
        super(pageMap, parameters);
        init();
    }

    /**
     * Initialization for all constructors.
     */
    private void init()
    {
        logger.info("Init. page=" + this.getClass().getName() + " user=" + getSessionUser());
        add(new Label(PAGE_TITLE, getPageTitleModel()));

        // logging for statistics (only log if there is an external referer-url)
        HttpServletRequest hsr = ((WebRequest) getRequest()).getHttpServletRequest();
        String ref_url = hsr.getHeader("Referer");
        String hostname = hsr.getHeader("Host"); // niet mooi, maar hoe anders?
        if (ref_url != null && !ref_url.contains(hostname))
        {
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_URL_REQUEST, new HttpRequestStatistics(hsr), new PageClassStatistics(this));
        }

    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();
    }

    public boolean isAuthenticated()
    {
        return !getSessionUser().isAnonymous();
    }

    public static EasySession getEasySession()
    {
        return EasySession.get();
    }

    public static EasyUser getSessionUser()
    {
        return getEasySession().getUser();
    }

    /**
     * Gets the context parameters. Subclasses that use AuthorizationStrategy with more context than the
     * sessionUser should override.
     * 
     * @return the context parameters
     */
    public ContextParameters getContextParameters()
    {
        return new ContextParameters(getSessionUser());
    }

    public void registerAjaxEventListener(String event, AjaxEventListener listener)
    {
        List<AjaxEventListener> listeners = getAjaxEventListenersMap().get(event);
        if (listeners == null)
        {
            listeners = new ArrayList<AjaxEventListener>();
            getAjaxEventListenersMap().put(event, listeners);
        }
        listeners.add(listener);
        if (logger.isDebugEnabled())
        {
            logger.debug("Registered AjaxEventListener. event= " + event + " listener=" + listener);
        }
    }

    public List<AjaxEventListener> getAjaxEventListeners(String event)
    {
        List<AjaxEventListener> listeners = getAjaxEventListenersMap().get(event);
        if (listeners == null)
        {
            listeners = Collections.emptyList();
        }
        return listeners;
    }

    public void handleAjaxEvent(String event, AjaxRequestTarget target)
    {
        for (AjaxEventListener listener : getAjaxEventListeners(event))
        {
            listener.handleAjaxEvent(target);
        }
    }

    private Map<String, List<AjaxEventListener>> getAjaxEventListenersMap()
    {
        if (ajaxEventListenersMap == null)
        {
            ajaxEventListenersMap = new HashMap<String, List<AjaxEventListener>>();
        }
        return ajaxEventListenersMap;
    }

    /**
     * Returns the prefix of the page's title. This is used in the <title> tag.
     * 
     * @return the page title prefix
     */
    public String getPageTitlePrefix()
    {
        return getLocalizer().getString("page.title.prefix", this);
    }

    /**
     * Returns the postfix of the page's title. The postfix is automatically appended to the prefix
     * separating the two with a dash. This is used in the <title> tag.
     * 
     * @return the page title prefix
     */
    public String getPageTitlePostfix()
    {
        return getLocalizer().getString("page.title.postfix", this);
    }

    /**
     * Refresh the contents of the page. Subclasses may override.
     */
    public void refresh()
    {
        logger.warn("Refresh called on " + this.getClass().getName() + " while it is not implementing refresh!" + printStackTrace());
    }

    /**
     * Returns the page title model. This is used for reading the content of the <title> tag.
     * 
     * @return
     */
    @SuppressWarnings("serial")
    public IModel getPageTitleModel()
    {
        return new AbstractReadOnlyModel()
        {

            public Serializable getObject()
            {
                String prefix = getPageTitlePrefix();
                String postfix = getPageTitlePostfix();
                // insert dash between prefix and postfix
                // TODO: call postfix prefix and vice versa!
                if (!StringUtils.isBlank(postfix))
                    postfix += " - ";
                return postfix + prefix;
            }
        };
    }

    private String printStackTrace()
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            if (ste.getClassName().startsWith("nl.knaw"))
            {
                sb.append("\n\t").append("at ").append(ste.getClassName()).append(" (").append(ste.getFileName()).append(":").append(ste.getLineNumber())
                        .append(")");
            }
        }
        return sb.toString();
    }

    public static boolean isArchivistOrAdmin()
    {
        return getSessionUser().hasRole(Role.ARCHIVIST, Role.ADMIN);
    }

}
