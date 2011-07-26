package nl.knaw.dans.easy.domain.model;

import java.net.URI;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public interface DatasetItemContainerMetadata extends DatasetItemMetadata
{
    
    String UNIT_ID = "EASY_ITEM_CONTAINER_MD";
    
    String UNIT_LABEL = "Metadata for this item container";
    
    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/item-container-md/";
    
    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);
    
    List<CreatorRole> getCreatorRoles();
    
    List<VisibleTo> getVisibleToList();
    
    List<AccessibleTo> getAccessibleToList();
    
    List<AccessCategory> getChildVisibility();

    List<AccessCategory> getChildAccessibility();
    
    int getTotalFileCount();
    
    int getChildFileCount();   
    
    int getTotalFolderCount();
    
    int getChildFolderCount();
    
    int getCreatorRoleFileCount(CreatorRole creatorRole);
    
    int getVissibleToFileCount(VisibleTo visibleTo);
    
    int getAccessibleToFileCount(AccessibleTo accessibleTo);

}
