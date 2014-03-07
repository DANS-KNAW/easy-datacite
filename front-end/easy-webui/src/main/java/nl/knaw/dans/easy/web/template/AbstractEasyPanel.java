/**
 *
 */
package nl.knaw.dans.easy.web.template;

import nl.knaw.dans.common.wicket.components.CommonPanel;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.wicket.AjaxEventListener;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Generic panel for Easy.
 * 
 * @author Herman Suijs
 */
public abstract class AbstractEasyPanel<T> extends CommonPanel<T>
{
    private static final long serialVersionUID = 5529101351554863036L;

    /**
     * Default constructor.
     * 
     * @param wicketId
     *        wicket id
     */
    public AbstractEasyPanel(final String wicketId)
    {
        super(wicketId);
        initAbstractEasyPanel();
    }

    /**
     * Constructor with model.
     * 
     * @param wicketId
     *        wicket id
     * @param model
     *        model
     */
    public AbstractEasyPanel(final String wicketId, final IModel<T> model)
    {
        super(wicketId, model);
        initAbstractEasyPanel();
    }

    protected void initAbstractEasyPanel()
    {
    }

    /**
     * Check if authenticated.
     * 
     * @return true if authenticated.
     */
    public final boolean isAuthenticated()
    {
        return !getSessionUser().isAnonymous();
    }

    public EasySession getEasySession()
    {
        return (EasySession) getSession();
    }

    public EasyUser getSessionUser()
    {
        return getEasySession().getUser();
    }

    public boolean registerAjaxEventListener(String event, AjaxEventListener listener)
    {
        boolean success = false;
        Page page = getPage();
        if (page != null && page instanceof AbstractEasyPage)
        {
            ((AbstractEasyPage) page).registerAjaxEventListener(event, listener);
            success = true;
        }
        return success;
    }

    public void handleAjaxEvent(String event, AjaxRequestTarget target)
    {
        Page page = getPage();
        if (page != null && page instanceof AbstractEasyPage)
        {
            ((AbstractEasyPage) page).handleAjaxEvent(event, target);
        }
    }
}
