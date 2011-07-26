package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public interface DatasetItem extends DataModelObject
{  
    String getDatasetId();
    
    void setDatasetId(String datasetId);
    
    DatasetItemMetadata getDatasetItemMetadata();

    void setParent(DatasetItemContainer parent) throws DomainException;
    
    boolean isDescendantOf(String storeId);
    
    boolean isDescendantOf(DataModelObject dmo);
    
    String getPath();
}
