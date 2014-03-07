package nl.knaw.dans.common.lang.repo.collections;

import java.util.Set;

import nl.knaw.dans.common.lang.repo.DataModelObject;

/**
 * An object that is member of a collection. This object stores it's relations through the relations
 * object of the DataModelObject
 * 
 * @author lobo
 */
public interface DmoCollectionMember extends DataModelObject
{
    /**
     * @return a list of the collections that this object is part of
     */
    public Set<DmoCollection> getCollections(); // wrong method name

    /**
     * A convenience method that may be called to check whether this object is part of a collection
     * 
     * @param collection
     *        the collection to check for
     */
    public boolean isPartOfCollection(DmoCollection collection);
}
