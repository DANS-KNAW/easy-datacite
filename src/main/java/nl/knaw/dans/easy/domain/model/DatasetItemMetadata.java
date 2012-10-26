package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;

public interface DatasetItemMetadata extends MetadataUnitXMLBean
{

    void setDmoStoreId(DmoStoreId storeId);

    DmoStoreId getDmoStoreId();

    DmoStoreId getParentDmoStoreId();

    void setParentDmoStoreId(DmoStoreId parentSid);

    DmoStoreId getDatasetDmoStoreId();

    void setDatasetDmoStoreId(DmoStoreId datasetId);

    String getName();

    String getPath();

    void setPath(String path);

}
