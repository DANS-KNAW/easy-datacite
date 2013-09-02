package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.easy.domain.model.AccessibleTo;

public class FolderItemAccessibleTo implements java.io.Serializable, Cloneable
{
    private static final long serialVersionUID = -2021285396582701318L;
    private long id = -1;
    private AccessibleTo accessibleTo;
    private String folderSid;

    public FolderItemAccessibleTo()
    {
    }

    public FolderItemAccessibleTo(String folderSid, AccessibleTo accessibleTo)
    {
        this.folderSid = folderSid;
        this.accessibleTo = accessibleTo;
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public AccessibleTo getAccessibleTo()
    {
        return this.accessibleTo;
    }

    public void setAccessibleTo(AccessibleTo accessibleTo)
    {
        this.accessibleTo = accessibleTo;
    }

    public void setFolderSid(String folderSid)
    {
        this.folderSid = folderSid;
    }

    public String getFolderSid()
    {
        return folderSid;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((folderSid == null) ? 0 : folderSid.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((accessibleTo == null) ? 0 : accessibleTo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderItemAccessibleTo other = (FolderItemAccessibleTo) obj;
        if (folderSid == null)
        {
            if (other.folderSid != null)
                return false;
        }
        else if (!folderSid.equals(other.folderSid))
            return false;
        if (id != other.id)
            return false;
        if (accessibleTo == null)
        {
            if (other.accessibleTo != null)
                return false;
        }
        else if (!accessibleTo.equals(other.accessibleTo))
            return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public String toString()
    {
        // if (accessibleTo== null) return "null";
        // return accessibleTo.toString();

        return "folderSid=" + folderSid + " accessibleTo=" + accessibleTo;
    }

}
