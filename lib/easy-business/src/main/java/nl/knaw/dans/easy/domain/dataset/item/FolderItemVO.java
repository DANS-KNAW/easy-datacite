package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.easy.domain.model.FolderItem;

public class FolderItemVO extends AbstractItemVO implements Cloneable {
    private static final long serialVersionUID = 5833718449823501446L;

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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    /**
     * Creates a deep copy of this object
     */
    public Object clone() throws CloneNotSupportedException {
        FolderItemVO c = (FolderItemVO) super.clone();
        return c;
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
