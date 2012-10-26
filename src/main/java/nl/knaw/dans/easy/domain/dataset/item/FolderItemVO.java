package nl.knaw.dans.easy.domain.dataset.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class FolderItemVO extends AbstractItemVO implements Cloneable
{
    private static final long serialVersionUID = 5833718449823501446L;

    private int childItemCount;
    private Set<FolderItemCreatorRole> creatorRolesOfChildren = new HashSet<FolderItemCreatorRole>(0);
    private Set<FolderItemVisibleTo> visibleToOfChildren = new HashSet<FolderItemVisibleTo>(0);
    private Set<FolderItemAccessibleTo> accesibleToOfChildren = new HashSet<FolderItemAccessibleTo>(0);

    public FolderItemVO()
    {
    }

    public FolderItemVO(FolderItem folderItem)
    {
        super(folderItem.getDmoStoreId().getStoreId(), folderItem.getDatasetItemMetadata().getParentDmoStoreId().getStoreId(), folderItem.getDatasetId()
                .getStoreId(), folderItem.getLabel());
        setPath(folderItem.getPath());
        setAccessibleToes(folderItem.getDatasetItemContainerMetadata().getAccessibleToList());
        setVisibleToes(folderItem.getDatasetItemContainerMetadata().getVisibleToList());
        setCreators(folderItem.getDatasetItemContainerMetadata().getCreatorRoles());
    }

    public FolderItemVO(String pid, String parentSid, String datasetSid, String name, int childItemCount)
    {
        super(pid, parentSid, datasetSid, name);
        this.setChildItemCount(childItemCount);
    }

    public void setChildItemCount(int childItemCount)
    {
        this.childItemCount = childItemCount;
    }

    public int getChildItemCount()
    {
        return childItemCount;
    }

    public void setCreatorRoles(Set<FolderItemCreatorRole> creatorRoles)
    {
        this.creatorRolesOfChildren = creatorRoles;
    }

    // ... and yet another translation...
    private void setCreators(List<CreatorRole> creatorRoles)
    {
        creatorRolesOfChildren.clear();
        String storeId = getSid();
        for (CreatorRole creatorRole : creatorRoles)
        {
            creatorRolesOfChildren.add(new FolderItemCreatorRole(storeId, creatorRole));
        }
    }

    public Set<FolderItemCreatorRole> getCreatorRoles()
    {
        return creatorRolesOfChildren;
    }

    public void setVisibleToList(Set<FolderItemVisibleTo> visibleToList)
    {
        this.visibleToOfChildren = visibleToList;
    }

    // ... and strange enumerations lead to strange names...
    private void setVisibleToes(List<VisibleTo> visibleToes)
    {
        visibleToOfChildren.clear();
        String storeId = getSid();
        for (VisibleTo visibleToe : visibleToes)
        {
            visibleToOfChildren.add(new FolderItemVisibleTo(storeId, visibleToe));
        }
    }

    public Set<FolderItemVisibleTo> getVisibleToList()
    {
        return visibleToOfChildren;
    }

    public void setAccessibleToList(Set<FolderItemAccessibleTo> accesibleToList)
    {
        this.accesibleToOfChildren = accesibleToList;
    }

    // The accessCategory has too many manifestations!
    protected void setAccessibleToes(List<AccessibleTo> accesibleToes)
    {
        accesibleToOfChildren.clear();
        String storeId = getSid();
        for (AccessibleTo accessibleToe : accesibleToes)
        {
            accesibleToOfChildren.add(new FolderItemAccessibleTo(storeId, accessibleToe));
        }
    }

    public Set<FolderItemAccessibleTo> getAccessibleToList()
    {
        return accesibleToOfChildren;
    }

    public boolean isAccessibleFor(int profile)
    {
        int mask = AccessCategory.UTIL.getBitMask(getChildAccessibility());
        return ((mask & profile) > 0);
    }

    public List<AccessCategory> getChildVisibility()
    {
        List<AccessCategory> categories = new ArrayList<AccessCategory>();
        for (FolderItemVisibleTo fiat : getVisibleToList())
        {
            categories.add(VisibleTo.translate(fiat.getVisibleTo()));
        }
        return categories;
    }

    public List<AccessCategory> getChildAccessibility()
    {
        List<AccessCategory> categories = new ArrayList<AccessCategory>();
        for (FolderItemAccessibleTo fiat : getAccessibleToList())
        {
            categories.add(AccessibleTo.translate(fiat.getAccessibleTo()));
        }
        return categories;
    }

    @Override
    public String getPath()
    {
        String path = super.getPath();
        if (path != null && !path.endsWith("/"))
        {
            path += "/";
        }
        return path;
    }

    @Override
    public String getAutzStrategyName()
    {
        return "nl.knaw.dans.easy.security.authz.EasyItemContainerVOAuthzStrategy";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + childItemCount;
        result = prime * result + ((creatorRolesOfChildren == null) ? 0 : creatorRolesOfChildren.hashCode());
        result = prime * result + ((visibleToOfChildren == null) ? 0 : visibleToOfChildren.hashCode());
        result = prime * result + ((accesibleToOfChildren == null) ? 0 : accesibleToOfChildren.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderItemVO other = (FolderItemVO) obj;
        if (childItemCount != other.childItemCount)
            return false;
        if (creatorRolesOfChildren == null)
        {
            if (other.creatorRolesOfChildren != null)
                return false;
        }
        else if (!creatorRolesOfChildren.equals(other.creatorRolesOfChildren))
            return false;
        if (visibleToOfChildren == null)
        {
            if (other.visibleToOfChildren != null)
                return false;
        }
        else if (!visibleToOfChildren.equals(other.visibleToOfChildren))
            return false;
        if (accesibleToOfChildren == null)
        {
            if (other.accesibleToOfChildren != null)
                return false;
        }
        else if (!accesibleToOfChildren.equals(other.accesibleToOfChildren))
        {
            return false;
        }
        return true;
    }

    /**
     * Creates a deep copy of this object
     */
    public Object clone() throws CloneNotSupportedException
    {
        FolderItemVO c = (FolderItemVO) super.clone();
        c.setCreatorRoles(deepCopyCreatorRoles());
        c.setVisibleToList(deepCopyVisibleToList());
        return c;
    }

    private Set<FolderItemCreatorRole> deepCopyCreatorRoles() throws CloneNotSupportedException
    {
        Set<FolderItemCreatorRole> clone = new HashSet<FolderItemCreatorRole>();
        Iterator<FolderItemCreatorRole> i = creatorRolesOfChildren.iterator();
        while (i.hasNext())
        {
            FolderItemCreatorRole obj = i.next();
            FolderItemCreatorRole cloneobj = (FolderItemCreatorRole) obj.clone();
            clone.add(cloneobj);
        }
        return clone;
    }

    private Set<FolderItemVisibleTo> deepCopyVisibleToList() throws CloneNotSupportedException
    {
        Set<FolderItemVisibleTo> clone = new HashSet<FolderItemVisibleTo>();
        Iterator<FolderItemVisibleTo> i = visibleToOfChildren.iterator();
        while (i.hasNext())
        {
            FolderItemVisibleTo obj = i.next();
            FolderItemVisibleTo cloneobj = (FolderItemVisibleTo) obj.clone();
            clone.add(cloneobj);
        }
        return clone;
    }

    public void updateTo(FolderItem folderItem)
    {
        if (!getSid().equals(folderItem.getStoreId()))
        {
            throw new IllegalArgumentException("Cannot update FolderItemVO " + getSid() + " to " + folderItem);
        }
        setParentSid(folderItem.getDatasetItemContainerMetadata().getParentDmoStoreId().getStoreId());
        setDatasetSid(folderItem.getDatasetId().getStoreId());
        setName(folderItem.getLabel());
        setPath(folderItem.getPath());
        setAccessibleToes(folderItem.getDatasetItemContainerMetadata().getAccessibleToList());
        setVisibleToes(folderItem.getDatasetItemContainerMetadata().getVisibleToList());
        setCreators(folderItem.getDatasetItemContainerMetadata().getCreatorRoles());

    }

}
