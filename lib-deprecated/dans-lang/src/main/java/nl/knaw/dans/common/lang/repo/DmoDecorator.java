package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.relations.Relation;

/**
 * A {@linkplain DataModelObject} can delegate some of it's operations to a DmoDecorator.
 */
public interface DmoDecorator extends Serializable {

    /**
     * The object namespace is prefixed before the storeId of the data model object.
     * 
     * @return object namespace
     */
    DmoNamespace getObjectNamespace();

    /**
     * Get additional content models. Each data model object can have several content models, which identify the kind of operations the object can do. This
     * method should be implemented by each abstract or non-abstract class and add one ore more content models to the content models from the super class. The
     * content model is a repository level class descriptor for the data model object.
     * 
     * @return additional content models
     */
    Set<String> getAdditionalContentModels();

    /**
     * @return a list of MetadataUnit objects
     */
    List<MetadataUnit> getAdditionalMetadataUnits();

    /**
     * @return a list of BinaryUnit objects.
     */
    List<BinaryUnit> getAdditionalBinaryUnits();

    Set<Relation> getAdditionalRelations();
}
