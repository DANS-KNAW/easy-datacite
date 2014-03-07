package nl.knaw.dans.common.lang.repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.CouldNotGetStoreException;
import nl.knaw.dans.common.lang.repo.exception.NoStoreAttachedException;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;

/**
 * Abstract Data Model Object is the base class for all data model objects.
 * 
 * @author lobo
 */
public abstract class AbstractDataModelObject extends AbstractStorableObject implements DataModelObject
{
    private static final long serialVersionUID = 8229662231033470296L;

    private boolean registerDeleted;

    private Relations relations;

    private transient UnitOfWork uow;

    private String storeName;

    private DmoStoreId dmoStoreId;

    private AuthzStrategy authzStrategy;

    public AbstractDataModelObject()
    {

    }

    public AbstractDataModelObject(String storeId)
    {
        super(storeId);
        dmoStoreId = DmoStoreId.newDmoStoreId(storeId);
    }

    public AbstractDataModelObject(DmoStoreId dmoStoreId)
    {
        this.dmoStoreId = dmoStoreId;
        setStoreId(dmoStoreId.getStoreId());
    }

    @Override
    public DmoStoreId getDmoStoreId()
    {
        if (dmoStoreId == null)
        {
            dmoStoreId = DmoStoreId.newDmoStoreId(getStoreId());
        }
        return dmoStoreId;
    }

    @Override
    public DmoNamespace getDmoNamespace()
    {
        return DmoStoreId.getDmoNamespace(getStoreId());
    }

    /**
     * Returns an empty list. Subclasses may override.
     */
    public List<MetadataUnit> getMetadataUnits()
    {
        return new ArrayList<MetadataUnit>();
    }

    /**
     * Returns an empty list. Subclasses may override.
     */
    public List<BinaryUnit> getBinaryUnits()
    {
        return new ArrayList<BinaryUnit>();
    }

    public void setUnitOfWork(UnitOfWork uow)
    {
        this.uow = uow;
    }

    public UnitOfWork getUnitOfWork() throws NoUnitOfWorkAttachedException
    {
        if (uow == null)
            throw new NoUnitOfWorkAttachedException("No unit of work attached to " + this.toString());
        return uow;
    }

    public void registerDeleted()
    {
        registerDeleted = true;
    }

    public boolean isRegisteredDeleted()
    {
        return registerDeleted;
    }

    /**
     * Creates a new relations object. Override this method if you want to be in complete control of the
     * relations.
     * 
     * @return a new relations object or null if no relations are specified
     */
    protected Relations newRelationsObject()
    {
        return null;
    }

    public Relations getRelations()
    {
        if (relations == null)
            relations = this.newRelationsObject();
        return relations;
    }

    public Set<String> getContentModels()
    {
        return new HashSet<String>();
    }

    @Override
    public String toString()
    {
        String params = "sid='" + getStoreId() + "' ";
        params += "label='" + getLabel() + "' ";
        params += "loaded='" + isLoaded() + "' ";
        params += "registerDeleted='" + isRegisteredDeleted() + "' ";
        params += "isDeletable='" + isDeletable() + "' ";

        return super.toString() + " [ " + params + "]";
    }

    public boolean isInvalidated() throws RepositoryException
    {
        if (isLoaded())
        {
            // It is assumed that all DMO's go through a single store object
            // which thus knows the state of all objects and can determine
            // even without querying its backend (Fedora) if the object is
            // invalidated or not.
            return getStore().isInvalidated(this);
        }
        else
        {
            return false;
        }
    }

    public DmoStore getStore() throws NoStoreAttachedException, CouldNotGetStoreException
    {
        return DmoStores.get().getStoreByName(getStoreName());
    }

    protected void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    public String getStoreName() throws NoStoreAttachedException
    {
        if (storeName == null)
            throw new NoStoreAttachedException();
        return storeName;
    }

    /**
     * Always throws a UnsupportedOperationException, subclasses should override.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public String getAutzStrategyName()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * @throws IllegalStateException if the authzStrategy was not set.
     */
    @Override
    public AuthzStrategy getAuthzStrategy()
    {
        if (authzStrategy == null)
        {
            throw new IllegalStateException("AuthzStrategy not set on " + this);
        }
        return authzStrategy;
    }

    @Override
    public void setAuthzStrategy(AuthzStrategy authzStrategy)
    {
        this.authzStrategy = authzStrategy;
    }
}
