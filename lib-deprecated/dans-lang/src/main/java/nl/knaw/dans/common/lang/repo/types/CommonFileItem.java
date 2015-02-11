package nl.knaw.dans.common.lang.repo.types;

import nl.knaw.dans.common.lang.repo.DataModelObject;

public interface CommonFileItem extends DataModelObject {

    String getMimeType();

    void setMimeType(String mimeType);
    
    void setSize(long size); 

    long getSize();

}
