package nl.knaw.dans.easy.domain.dataset.item;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.FolderItem;

public class FolderItemVO extends AbstractItemVO {
    private static final long serialVersionUID = 5833718449823501446L;
    private Set<FolderItemCreatorRole> creatorRoles = new HashSet<FolderItemCreatorRole>(0);
    private Set<FolderItemVisibleTo> visibilities = new HashSet<FolderItemVisibleTo>(0);
    private Set<FolderItemAccessibleTo> accessibilities = new HashSet<FolderItemAccessibleTo>(0);

    public Set<FolderItemCreatorRole> getCreatorRoles() {
        return creatorRoles;
    }

    public void setCreatorRoles(Set<FolderItemCreatorRole> creatorRoles) {
        this.creatorRoles = creatorRoles;
    }

    public Set<FolderItemVisibleTo> getVisibilities() {
        return visibilities;
    }

    public void setVisibilities(Set<FolderItemVisibleTo> visibilities) {
        this.visibilities = visibilities;
    }

    public Set<FolderItemAccessibleTo> getAccessibilities() {
        return accessibilities;
    }

    public void setAccessibilities(Set<FolderItemAccessibleTo> accessibilities) {
        this.accessibilities = accessibilities;
    }

    public FolderItemVO() {}

    public FolderItemVO(FolderItem folderItem) {
        super(folderItem.getDmoStoreId().getStoreId(), folderItem.getDatasetItemMetadata().getParentDmoStoreId().getStoreId(), folderItem.getDatasetId()
                .getStoreId(), folderItem.getLabel());
        setPath(folderItem.getPath());
    }

    public FolderItemVO(String pid, String parentSid, String datasetSid, String name, int childItemCount) {
        super(pid, parentSid, datasetSid, name);
    }

    @Override
    public String getPath() {
        String path = super.getPath();
        if (path != null && !path.endsWith("/")) {
            path += "/";
        }
        return path;
    }

    @Override
    public String getAutzStrategyName() {
        return "nl.knaw.dans.easy.security.authz.EasyItemContainerVOAuthzStrategy";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FolderItemVO && super.equals(o)) {
            FolderItemVO f = (FolderItemVO) o;
            //@formatter:off
            return f.accessibilities.equals(f.accessibilities) 
                && f.creatorRoles.equals(f.creatorRoles)
                && f.visibilities.equals(f.visibilities);
            //@formatter:on
        }
        return false;
    }

    public void updateTo(FolderItem folderItem) {
        if (!getSid().equals(folderItem.getStoreId())) {
            throw new IllegalArgumentException("Cannot update FolderItemVO " + getSid() + " to " + folderItem);
        }
        setParentSid(folderItem.getDatasetItemContainerMetadata().getParentDmoStoreId().getStoreId());
        setDatasetSid(folderItem.getDatasetId().getStoreId());
        setName(folderItem.getLabel());
        setPath(folderItem.getPath());
    }
}
