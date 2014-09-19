package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;

import org.dom4j.Element;

public interface DmoFactory<T extends DataModelObject> {

    /**
     * Get the namespace of the object this DmoFactory is instantiating.
     * 
     * @return namespace
     */
    DmoNamespace getNamespace();

    /**
     * Create an new instance of T with a new storeId. The storeId may be <code>null</code> if no SidDispenser is associated with this DmoFactory.
     * 
     * @return new instance of T with a new storeId
     * @throws RepositoryException
     *         for exceptions while obtaining a new storeId
     */
    T newDmo() throws RepositoryException;

    /**
     * Create a new instance of T with the given storeId.
     * 
     * @param storeId
     *        storeId for returned T
     * @return new instance of T with the given storeId
     */
    T createDmo(String storeId);

    /**
     * Set the metadata unit represented by <code>element</code> on the given <code>dmo</code>. The metadata unit is identified with the given
     * <code>unitId</code>.
     * 
     * @param dmo
     *        DataModelObject to handle
     * @param unitId
     *        unitId of the metadata unit represented by <code>element</code>.
     * @param element
     *        represents the metadata unit
     * @throws ObjectDeserializationException
     *         if the element cannot be deserialized.
     */
    void setMetadataUnit(DataModelObject dmo, String unitId, Element element) throws ObjectDeserializationException;
}
