package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;

public interface DatasetItemMetadata extends MetadataUnitXMLBean {
    String getName();

    String getPath();

    void setPath(String path);
}
