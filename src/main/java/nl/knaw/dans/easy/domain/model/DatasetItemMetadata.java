package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;

public interface DatasetItemMetadata extends MetadataUnitXMLBean
{
    
    void setSid(String storeId);
    
    String getSid();
    
    String getParentSid();
    
    void setParentSid(String parentSid);
    
    String getDatasetId();
    
    void setDatasetId(String datasetId);
    
    String getName();
    
    String getPath();
    
    void setPath(String path);
    
}
