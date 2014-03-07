package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.WeakObjectRefPool;

/**
 * A pool of weak-references DataModelObjects.
 * 
 * @author lobo
 * @param <T>
 *        an info object to keep with the dmos
 */
public class DmoPool<T> extends WeakObjectRefPool<DmoStoreId, DataModelObject, T>
{
    public DmoPool(int initCapacity)
    {
        super(initCapacity);
    }

    public void add(DataModelObject dmo, T dmoInfo)
    {
        add(dmo.getDmoStoreId(), dmo, dmoInfo);
    }
}
