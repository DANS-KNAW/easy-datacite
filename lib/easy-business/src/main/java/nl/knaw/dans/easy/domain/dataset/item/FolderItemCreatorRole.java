package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class FolderItemCreatorRole implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = -1024185521659638423L;
    private String id;
    private String folderSid;
    private CreatorRole creatorRole;

    public FolderItemCreatorRole() {}

    public FolderItemCreatorRole(String folderSid, CreatorRole creator) {
        this.folderSid = folderSid;
        this.creatorRole = creator;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CreatorRole getCreatorRole() {
        return this.creatorRole;
    }

    public void setCreatorRole(CreatorRole creator) {
        this.creatorRole = creator;
    }

    public void setFolderSid(String folderSid) {
        this.folderSid = folderSid;
    }

    public String getFolderSid() {
        return folderSid;
    }

    @Override
    public int hashCode() {
        return folderSid.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FolderItemCreatorRole) {
            FolderItemCreatorRole f = (FolderItemCreatorRole) o;
            //@formatter:off
            return id.equals(f.id) 
                && folderSid.equals(f.folderSid) 
                && creatorRole.equals(f.creatorRole);
            //@formatter:on
        }
        return false;
    }

    @Override
    public String toString() {
        return creatorRole.toString();
    }
}
