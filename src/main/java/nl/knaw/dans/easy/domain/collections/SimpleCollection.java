package nl.knaw.dans.easy.domain.collections;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.model.Constants;

public interface SimpleCollection extends DataModelObject
{
    
    String NAMESPACE = "easy-collection";
    
    String CONTENT_MODEL = Constants.CM_SIMPLE_COLLECTION_1;
    
    String ROOT_ID = NAMESPACE + ":" + "ec";
    
    SimpleCollection getParent();
    
    List<SimpleCollection> getChildren();
    
    boolean hasParent();
    
    String getParentId();
    
    boolean hasChildren();
    
    List<String> getChildIds();

    boolean addChild(SimpleCollection child);
    
    boolean isOAISet();
    
    void setOAISet(boolean isOAISet);

}
