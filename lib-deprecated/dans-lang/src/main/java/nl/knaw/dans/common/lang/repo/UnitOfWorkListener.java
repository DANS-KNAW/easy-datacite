package nl.knaw.dans.common.lang.repo;

/**
 * 
 *
 */
public interface UnitOfWorkListener {

    boolean onIngest(DataModelObject dmo);

    void afterIngest(DataModelObject dmo);

    boolean onUpdate(DataModelObject dmo);

    void afterUpdate(DataModelObject dmo);

    boolean onUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit);

    void afterUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit);

    boolean onPurge(DataModelObject dmo);

    void afterPurge(DataModelObject dmo);

    void afterRetrieveObject(DataModelObject dmo);

    void onException(Throwable t);

}
