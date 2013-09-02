package nl.knaw.dans.common.wicket.model;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DMOModel<T extends DataModelObject> implements IModel<T>
{

    private static final long serialVersionUID = -8410446576640508341L;

    private static final Logger logger = LoggerFactory.getLogger(DMOModel.class);

    private T dmo;

    private final String storeId;
    private DmoStoreId dmoStoreId;

    /**
     * Dynamic reloading of a DMO means that when the DMO gets invalidated the DMO held in memory is
     * reloaded.
     */
    private boolean dynamicReload = true;

    public DMOModel(String storeId)
    {
        this.storeId = storeId;
        this.dmoStoreId = storeId == null ? null : new DmoStoreId(storeId);
    }

    public DMOModel(T dmo)
    {
        this.dmo = dmo;
        this.storeId = dmo.getStoreId();
        this.dmoStoreId = storeId == null ? null : new DmoStoreId(storeId);
    }

    public DMOModel(DMOModel<T> model)
    {
        this.storeId = model.getStoreId();
        this.dmoStoreId = storeId == null ? null : new DmoStoreId(storeId);
        this.dmo = model.getCachedObject();
        this.dynamicReload = model.isDynamicReloadEnabled();
    }

    public String getStoreId()
    {
        return storeId;
    }

    public DmoStoreId getDmoStoreId()
    {
        if (dmoStoreId == null && !StringUtils.isBlank(storeId))
        {
            dmoStoreId = new DmoStoreId(storeId);
        }
        return dmoStoreId;
    }

    @Override
    public T getObject() throws ServiceRuntimeException
    {
        if (dmo == null)
        {
            dmo = loadDmo();
        }
        else if (dynamicReload && isInvalidated())
        {
            logger.debug("Reloading " + dmo.toString());
            dmo = loadDmo();
        }

        return dmo;
    }

    public T getCachedObject()
    {
        return dmo;
    }

    @Override
    public void setObject(T object)
    {
        if (!this.storeId.equals(object.getStoreId()))
        {
            throw new IllegalArgumentException("Trying to set a dmo which does not have the same storeId. this.storeId=" + storeId + " other dmo.storeId="
                    + object.getStoreId());
        }
        this.dmo = object;
    }

    @Override
    public void detach()
    {
        // not in use.
    }

    protected abstract T loadDmo();

    public boolean isInvalidated()
    {
        try
        {
            return dmo != null && dynamicReload && dmo.isInvalidated();
        }
        catch (RepositoryException e)
        {
            throw new WicketRuntimeException("Could not determine if the Data Model Object was invalidated. storeId=" + storeId, e);
        }
    }

    /**
     * Enable or disable dynamic reloading. Dynamic reloading of a DMO means that when the DMO gets
     * invalidated the DMO held in memory is reloaded.
     * 
     * @param enable
     */
    public void setDynamicReload(boolean enable)
    {
        this.dynamicReload = enable;
    }

    public boolean isDynamicReloadEnabled()
    {
        return dynamicReload;
    }
}
