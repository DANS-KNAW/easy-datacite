package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public interface DatasetItemContainer extends DataModelObject
{
    DatasetItemContainerMetadata getDatasetItemContainerMetadata();

    int getTotalFileCount();

    int getChildFileCount();

    int getTotalFolderCount();

    int getChildFolderCount();

    int getCreatorRoleFileCount(CreatorRole creatorRole);

    int getVisibleToFileCount(VisibleTo visibleTo);

    int getAccessibleToFileCount(AccessibleTo accessibleTo);

    void addFileOrFolder(DatasetItem item) throws DomainException;

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onChildRemoved(DatasetItem item);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onChildAdded(DatasetItem item);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantAdded(DatasetItem item);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantRemoved(DatasetItem item);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantStateChange(CreatorRole oldCreatorRole, CreatorRole newCreatorRole);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantStateChange(String oldStreamingPath, String newStreamingPath);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantStateChange(VisibleTo oldVisibleTo, VisibleTo newVisibleTo);

    /**
     * NOT PART OF THE PUBLIC API. CALLED INTERNALLY.
     */
    void onDescendantStateChange(AccessibleTo oldAccessibleTo, AccessibleTo newAccessibleTo);
}
