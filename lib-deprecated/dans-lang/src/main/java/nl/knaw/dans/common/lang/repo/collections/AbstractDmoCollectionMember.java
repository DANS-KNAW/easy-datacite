package nl.knaw.dans.common.lang.repo.collections;

import java.util.Set;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.exception.ObjectIsNotPartOfCollection;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;

public abstract class AbstractDmoCollectionMember extends AbstractDataModelObject implements DmoCollectionMember
{
    private static final long serialVersionUID = 6988726555791925738L;

    public AbstractDmoCollectionMember(String storeId)
    {
        super(storeId);
    }

    public boolean isPartOfCollection(DmoCollection collection)
    {
        for (DmoCollection col : getCollections())
        {
            if (col.getStoreId().equals(collection.getStoreId()))
                return true;
        }
        return false;
    }

    /**
     * Checks if the relation between this object and the member object is possible. It throws an
     * exception is not.
     */
    @SuppressWarnings({"rawtypes"})
    protected void checkDmoClassCompatible(Class memberClass) throws ObjectIsNotPartOfCollection
    {
        Set<DmoCollection> collections = getCollections();
        if (collections == null)
            return;

        for (DmoCollection col : collections)
        {
            for (Class clazz : col.getMemberClasses())
            {
                if (ClassUtil.instanceOf(memberClass, clazz))
                    return;
            }
        }

        String colStr = "";
        for (DmoCollection col : collections)
        {
            colStr += col.toString();
        }

        throw new ObjectIsNotPartOfCollection("Object " + memberClass.toString() + " is not part of [" + colStr + "]");
    }

    protected void checkDmoCompatible(DataModelObject dmo) throws ObjectIsNotPartOfCollection
    {
        checkDmoClassCompatible(dmo.getClass());
    }

    protected void checkSidCompatible(DmoStoreId dmoStoreId) throws RepositoryException
    {
        UnitOfWork uow = getUnitOfWork();
        if (uow == null)
            throw new NoUnitOfWorkAttachedException();

        Class<? extends DataModelObject> dmoClass = AbstractDmoFactory.dmoInstance(dmoStoreId.getId()).getClass();
        checkDmoClassCompatible(dmoClass);
    }

    protected void tryAttachToUnitOfWork(DataModelObject item)
    {
        try
        {
            getUnitOfWork().attach(item);
        }
        catch (NoUnitOfWorkAttachedException e)
        {
        }
        catch (RepositoryException e)
        {
        }
    }

    protected DataModelObject tryGetObjectFromUnitOfWork(DmoStoreId dmoStoreId)
    {
        try
        {
            return (DmoContainerItem) getUnitOfWork().getObject(dmoStoreId);
        }
        catch (NoUnitOfWorkAttachedException e)
        {
        }
        catch (ObjectNotInStoreException e)
        {
        }
        catch (RepositoryException e)
        {
        }
        return null;
    }

}
