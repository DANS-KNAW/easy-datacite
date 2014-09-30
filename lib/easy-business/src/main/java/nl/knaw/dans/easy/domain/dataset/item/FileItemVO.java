package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class FileItemVO extends AbstractItemVO implements java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 6104956832284968887L;
    private int size;
    private String mimetype;
    private CreatorRole creatorRole;
    private VisibleTo visibleTo;
    private AccessibleTo accessibleTo;

    public FileItemVO() {}

    public FileItemVO(FileItem fileItem) {
        super(fileItem.getDmoStoreId().getStoreId(), fileItem.getFileItemMetadata().getParentDmoStoreId().getStoreId(), fileItem.getDatasetId().getStoreId(),
                fileItem.getLabel());
        size = (int) fileItem.getSize();
        mimetype = fileItem.getFileItemMetadata().getMimeType();
        creatorRole = fileItem.getCreatorRole();
        visibleTo = fileItem.getVisibleTo();
        accessibleTo = fileItem.getAccessibleTo();
        setPath(fileItem.getPath());
    }

    public FileItemVO(String sid, String parentSid, String datasetSid, String name, int size, String mimetype, CreatorRole creatorRole, String streamingPath,
            VisibleTo visibleTo, AccessibleTo accessibleTo)
    {
        super(sid, parentSid, datasetSid, name);
        this.size = size;
        this.mimetype = mimetype;
        this.creatorRole = creatorRole;
        this.visibleTo = visibleTo;
        this.setAccessibleTo(accessibleTo);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMimetype() {
        return this.mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public CreatorRole getCreatorRole() {
        return this.creatorRole;
    }

    public void setCreatorRole(CreatorRole creatorRole) {
        this.creatorRole = creatorRole;
    }

    public VisibleTo getVisibleTo() {
        return this.visibleTo;
    }

    public void setVisibleTo(VisibleTo visibleTo) {
        this.visibleTo = visibleTo;
    }

    public void setAccessibleTo(AccessibleTo accessibleTo) {
        this.accessibleTo = accessibleTo;
    }

    public AccessibleTo getAccessibleTo() {
        return accessibleTo;
    }

    /**
     * Hack needed because there is no unification of key abstractions in the DANS software development process.
     * 
     * @return the AccessCategory of the file item in respect to visibility
     */
    public AccessCategory getViewAccessCategory() {
        return VisibleTo.translate(visibleTo);
    }

    /**
     * Hack needed because there is no unification of key abstractions in the DANS software development process.
     * 
     * @return the AccessCategory of the file item in respect to accessibility
     */
    public AccessCategory getReadAccessCategory() {
        return AccessibleTo.translate(accessibleTo);
    }

    @Override
    public String getAutzStrategyName() {
        return "nl.knaw.dans.easy.security.authz.EasyFileItemVOAuthzStrategy";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((accessibleTo == null) ? 0 : accessibleTo.hashCode());
        result = prime * result + ((creatorRole == null) ? 0 : creatorRole.hashCode());
        result = prime * result + ((mimetype == null) ? 0 : mimetype.hashCode());
        result = prime * result + size;
        result = prime * result + ((visibleTo == null) ? 0 : visibleTo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileItemVO other = (FileItemVO) obj;
        if (accessibleTo == null) {
            if (other.accessibleTo != null)
                return false;
        } else if (!accessibleTo.equals(other.accessibleTo))
            return false;
        if (creatorRole == null) {
            if (other.creatorRole != null)
                return false;
        } else if (!creatorRole.equals(other.creatorRole))
            return false;
        if (mimetype == null) {
            if (other.mimetype != null)
                return false;
        } else if (!mimetype.equals(other.mimetype))
            return false;
        if (size != other.size)
            return false;
        if (visibleTo == null) {
            if (other.visibleTo != null)
                return false;
        } else if (!visibleTo.equals(other.visibleTo))
            return false;
        return true;
    }

    public void updateTo(FileItem fileItem) {
        if (!getSid().equals(fileItem.getStoreId())) {
            throw new IllegalArgumentException("Cannot update FileItemVO " + getSid() + " to " + fileItem);
        }
        setParentSid(fileItem.getDatasetItemMetadata().getParentDmoStoreId().getStoreId());
        setDatasetSid(fileItem.getDatasetId().getStoreId());
        setName(fileItem.getLabel());
        size = (int) fileItem.getSize();
        mimetype = fileItem.getFileItemMetadata().getMimeType();
        creatorRole = fileItem.getCreatorRole();
        visibleTo = fileItem.getVisibleTo();
        accessibleTo = fileItem.getAccessibleTo();
        setPath(fileItem.getPath());
    }

}
