package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class FolderItemCreatorRole implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = -1024185521659638423L;
    private long id = -1;
    private String folderSid;
    private CreatorRole creator;

    public FolderItemCreatorRole() {}

    public FolderItemCreatorRole(String folderSid, CreatorRole creator) {
        this.folderSid = folderSid;
        this.creator = creator;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CreatorRole getCreatorRole() {
        return this.creator;
    }

    public void setCreatorRole(CreatorRole creator) {
        this.creator = creator;
    }

    public void setFolderSid(String folderSid) {
        this.folderSid = folderSid;
    }

    public String getFolderSid() {
        return folderSid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((folderSid == null) ? 0 : folderSid.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderItemCreatorRole other = (FolderItemCreatorRole) obj;
        if (creator == null) {
            if (other.creator != null)
                return false;
        } else if (!creator.equals(other.creator))
            return false;
        if (folderSid == null) {
            if (other.folderSid != null)
                return false;
        } else if (!folderSid.equals(other.folderSid))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
