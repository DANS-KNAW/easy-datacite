package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.easy.domain.model.AccessibleTo;

public class FolderItemAccessibleTo implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = -2021285396582701318L;
    private String id;
    private AccessibleTo accessibleTo;
    private String folderSid;

    public FolderItemAccessibleTo() {}

    public FolderItemAccessibleTo(String folderSid, AccessibleTo accessibleTo) {
        this.folderSid = folderSid;
        this.accessibleTo = accessibleTo;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccessibleTo getAccessibleTo() {
        return this.accessibleTo;
    }

    public void setAccessibleTo(AccessibleTo accessibleTo) {
        this.accessibleTo = accessibleTo;
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
        if (o instanceof FolderItemAccessibleTo) {
            FolderItemAccessibleTo f = (FolderItemAccessibleTo) o;
            //@formatter:off
            return id.equals(f.id) 
                && folderSid.equals(f.folderSid) 
                && accessibleTo.equals(f.accessibleTo);
            //@formatter:on
        }
        return false;
    }

    @Override
    public String toString() {
        return accessibleTo.toString();
    }
}
