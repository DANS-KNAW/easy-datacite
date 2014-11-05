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
        return getParentSid().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileItemVO && super.equals(o)) {
            FileItemVO f = (FileItemVO) o;
            //@formatter:off
            return accessibleTo.equals(f.accessibleTo) 
                && creatorRole.equals(f.creatorRole) 
                && mimetype.equals(f.mimetype) 
                && size == f.size
                && visibleTo.equals(f.visibleTo);
            //@formatter:on

        }
        return false;
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
