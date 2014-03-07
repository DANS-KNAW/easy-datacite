package nl.knaw.dans.easy.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.CommonSession;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.commons.collections.map.LRUMap;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EasySession extends CommonSession
{

    public static final int MAX_CACHED_OBJECT_CAPACITY = 10;
    private static final long serialVersionUID = 3650502450193859555L;
    private static final Logger logger = LoggerFactory.getLogger(EasySession.class);

    private EasyUser user = EasyUserAnonymous.getInstance();
    private Map<Class<? extends Page>, RedirectData> redirectMap = new HashMap<Class<? extends Page>, RedirectData>();
    private Map<Class<? extends Page>, Page> redirectPageMap = new HashMap<Class<? extends Page>, Page>();
    private ContextParameters contextParameters;
    private Map dmoObjectMap = Collections.synchronizedMap(new LRUMap(MAX_CACHED_OBJECT_CAPACITY));
    private Map<String, Object> sessionMap = Collections.synchronizedMap(new HashMap<String, Object>());

    public EasySession(Request request)
    {
        super(request);
    }

    /**
     * Get the user of this session, or <code>null</code> if no user is logged in.
     * 
     * @return the user of this session
     */
    public EasyUser getUser()
    {
        return user;
    }

    /**
     * Set the user of this session.
     * 
     * @param authentication
     *        user of this session
     */
    public void setLoggedIn(Authentication authentication)
    {
        this.user = authentication.getUser();

        // No reset, otherwise we can't redirect to previous page when logging in
    }

    public void setLoggedOff()
    {
        user = EasyUserAnonymous.getInstance();
        cleanupFeedbackMessages();
        reset();
        super.clear();
    }

    public boolean isAuthenticated()
    {
        return user.isActive() && !user.isAnonymous();
    }

    // Reset this EasySession to a state that is appropriate for the new situation after
    // setLoggedOff/setLoggedIn.
    private void reset()
    {
        contextParameters = null;
        redirectMap.clear();
        redirectPageMap.clear();
        dmoObjectMap.clear();

        clear(); // No reset when logging in, so we can clear the session!
    }

    /**
     * Set redirect data. This method called by page A that links to page B, so that page B knows which
     * page to return to.
     * 
     * <pre>
     *             [pageA] setRedirectData(PageB.class, new RedirectData(PageA.class, parameters))
     *              |   &#8593;
     *              |   |
     *              &#8595;   |
     *             [pageB] getRedirectData(this.getClass())
     * </pre>
     * 
     * @see #getRedirectData(Class)
     * @see #hasRedirectData(Class)
     * @param toPage
     *        the class of the page that is linked to
     * @param redirectData
     *        RedirectData containing calling page class and any parameters for reconstruction
     */
    public void setRedirectData(Class<? extends Page> toPage, RedirectData redirectData)
    {
        redirectMap.put(toPage, redirectData);
        if (logger.isDebugEnabled())
        {
            logger.debug("Added redirect for " + toPage + ". size of redirectMap=" + redirectMap.size());
        }
    }

    /**
     * Get <u>and remove</u> redirect data. This method called by page B that wants to return to the page
     * that set the redirect data.
     * 
     * @see #setRedirectData(Class, RedirectData)
     * @see #hasRedirectData(Class)
     * @param fromPage
     *        the class of the page that was linked to, i.e. the page that is calling this method
     * @return
     */
    public RedirectData getRedirectData(Class<? extends Page> fromPage)
    {
        RedirectData redirectData = redirectMap.remove(fromPage);
        if (logger.isDebugEnabled())
        {
            logger.debug("Removed redirect for " + fromPage + ". size of redirectMap=" + redirectMap.size());
        }
        return redirectData;
    }

    /**
     * Does the session have any redirect data for the page of the given class.
     * 
     * @param fromPage
     *        the class of the page that was linked to, i.e. the page that is calling this method
     * @return <code>true</code> if there are redirect data, <code>false</code> otherwise
     */
    public boolean hasRedirectData(Class<? extends Page> fromPage)
    {
        return redirectMap.containsKey(fromPage);
    }

    public void setRedirectPage(Class<? extends Page> toPage, Page fromPage)
    {
        redirectPageMap.put(toPage, fromPage);
        if (logger.isDebugEnabled())
        {
            logger.debug("Added redirect page for " + toPage + ". size of redirectPageMap=" + redirectPageMap.size());
        }
    }

    public Page getRedirectPage(Class<? extends Page> fromPage)
    {
        Page page = redirectPageMap.remove(fromPage);
        if (logger.isDebugEnabled())
        {
            logger.debug("Removed redirect page for " + fromPage + ". size of redirectPageMap=" + redirectPageMap.size());
        }
        return page;
    }

    public boolean hasRedirectPage(Class<? extends Page> fromPage)
    {
        return redirectPageMap.containsKey(fromPage);
    }

    /**
     * Simple method to get some context. The only context that will be available is the sessionUser and
     * even that may be <code>null</code>. Unless <code>setContextParameters()</code> is called. Note:
     * After the contextParameters are returned the parameters are removed from the EasySession.
     * 
     * @see ContextParameters
     * @return ContextParameters with a sessionUser equal to the user of this session (might be
     *         <code>null</code>)
     */
    public ContextParameters getContextParameters()
    {
        if (contextParameters == null)
        {
            contextParameters = new ContextParameters(getUser());
        }
        ContextParameters ctxParameters = contextParameters;
        contextParameters = null;
        return ctxParameters;
    }

    /**
     * Set contextParameters in the current EasySession. Note: when the
     * <code>getContextParameters()</code> is called the contextParameters in the EasySession are
     * removed.
     * 
     * @param ctxParameters
     *        the parameters to add to the EasySession
     */
    public void setContextParameters(ContextParameters ctxParameters)
    {
        contextParameters = ctxParameters;
    }

    /**
     * Put a DataModelObject for temporary storage in the internal objectMap. The Least Recently Used
     * (LRU) object will be removed if adding the DataModelObject surpasses the maximum capacity for
     * cached objects.
     * 
     * @see #MAX_CACHED_OBJECT_CAPACITY
     * @see <a
     *      href="http://commons.apache.org/collections/api/org/apache/commons/collections/LRUMap.html">LRUMap</a>
     * @param dmo
     *        the DataModelObject to cache
     */
    public void putDataModelObject(DatasetModel dmoModel)
    {
        synchronized (dmoObjectMap)
        {
            logger.debug("Adding " + dmoModel.getStoreId() + " to the session object map");
            dmoObjectMap.put(dmoModel.getDmoStoreId(), dmoModel);
        }
    }

    /**
     * Get the DataModelObject with the given storeId from cache. If the requested object is not in cache
     * (anymore), an attempt will be made to get it from the service-layer.
     * 
     * @param dmoStoreId
     *        the storeId of the requested object
     * @return the requested object
     * @throws ServiceException
     * @throws ServiceException
     *         as a wrapper for exceptions
     */
    public DataModelObject getDataset(DmoStoreId dmoStoreId) throws ServiceException
    {
        DataModelObject dmo = null;
        synchronized (dmoObjectMap)
        {
            DatasetModel dmoModel = (DatasetModel) dmoObjectMap.get(dmoStoreId);
            if (dmoModel != null)
                dmo = dmoModel.getObject();
        }
        try
        {
            if (dmo == null || dmo.isInvalidated())
            {
                if (dmo == null)
                    logger.debug("Object not in objectMap, getting it from service: " + dmoStoreId);
                else
                    logger.debug("Object " + dmoStoreId + " was invalidated, retreiving a new one from service.");
                dmo = Services.getDatasetService().getDataset(getUser(), dmoStoreId);
            }
        }
        catch (RepositoryException e)
        {
            // wrapper for dmo isInvalidated
            throw new ServiceException(e);
        }
        if (!dmo.isLoaded())
        {
            throw new WicketRuntimeException("A dmo is retrieved from session and it's loaded-flag is not set.");
        }
        return dmo;
    }

    /**
     * A way for stateless areas in the web tier to access some state. Keep it lean and clean: the wicket
     * framework is statefull.
     * 
     * @param key
     *        association key
     * @param value
     *        associated value
     */
    public void put(String key, Object value)
    {
        synchronized (sessionMap)
        {
            sessionMap.put(key, value);
        }
    }

    public Object get(String key)
    {
        return sessionMap.get(key);
    }

    public Object remove(String key)
    {
        synchronized (sessionMap)
        {
            return sessionMap.remove(key);
        }
    }

    public static EasySession get()
    {
        return (EasySession) Session.get();
    }

    public static EasyUser getSessionUser()
    {
        return get().getUser();
    }
}
