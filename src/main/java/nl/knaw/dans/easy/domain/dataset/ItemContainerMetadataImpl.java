package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class ItemContainerMetadataImpl extends AbstractItemMetadataImpl<DatasetItemContainerMetadata> implements DatasetItemContainerMetadata
{

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an
     * xml-stream might be obtained by {@link #getVersion()}.
     */
    public static final String VERSION = "0.1";

    private static final long serialVersionUID = -396869998619454854L;

    private int totalFileCount;
    private int childFileCount;

    private int totalFolderCount;
    private int childFolderCount;

    private int[] creatorRoleArray = new int[CreatorRole.values().length];

    private int[] visibleToArray = new int[VisibleTo.values().length];

    private int[] accessibleToArray = new int[AccessibleTo.values().length];

    private String version;

    protected ItemContainerMetadataImpl()
    {
        super();
    }

    public ItemContainerMetadataImpl(DmoStoreId sid)
    {
        super(sid);
    }

    public String getVersion()
    {
        if (version == null)
        {
            version = VERSION;
        }
        return version;
    }

    // ///////////////////////////////////////////
    protected void setNop(List<?> list)
    {
        // needed for jiBx deserialization
    }

    public List<CreatorRole> getCreatorRoles()
    {
        List<CreatorRole> creatorRoles = new ArrayList<CreatorRole>();
        for (int i = 0; i < creatorRoleArray.length; i++)
        {
            if (creatorRoleArray[i] > 0)
            {
                creatorRoles.add(CreatorRole.values()[i]);
            }
        }
        return creatorRoles;
    }

    public List<VisibleTo> getVisibleToList()
    {
        List<VisibleTo> visibleToList = new ArrayList<VisibleTo>();
        for (int i = 0; i < visibleToArray.length; i++)
        {
            if (visibleToArray[i] > 0)
            {
                visibleToList.add(VisibleTo.values()[i]);
            }
        }
        return visibleToList;
    }

    public List<AccessibleTo> getAccessibleToList()
    {
        List<AccessibleTo> accessibleToList = new ArrayList<AccessibleTo>();
        for (int i = 0; i < accessibleToArray.length; i++)
        {
            if (accessibleToArray[i] > 0)
            {
                accessibleToList.add(AccessibleTo.values()[i]);
            }
        }
        return accessibleToList;
    }

    @Override
    public List<AccessCategory> getChildVisibility()
    {
        List<AccessCategory> visibilityCategories = new ArrayList<AccessCategory>();
        for (VisibleTo vt : getVisibleToList())
        {
            visibilityCategories.add(VisibleTo.translate(vt));
        }
        return visibilityCategories;
    }

    @Override
    public List<AccessCategory> getChildAccessibility()
    {
        List<AccessCategory> accessibilityCategories = new ArrayList<AccessCategory>();
        for (AccessibleTo at : getAccessibleToList())
        {
            accessibilityCategories.add(AccessibleTo.translate(at));
        }
        return accessibilityCategories;
    }

    // ////////////////////////////////////////////

    public int getTotalFileCount()
    {
        return totalFileCount;
    }

    public int getChildFileCount()
    {
        return childFileCount;
    }

    public int getTotalFolderCount()
    {
        return totalFolderCount;
    }

    public int getChildFolderCount()
    {
        return childFolderCount;
    }

    public int getCreatorRoleFileCount(CreatorRole creatorRole)
    {
        return creatorRoleArray[creatorRole.ordinal()];
    }

    public int getVissibleToFileCount(VisibleTo visibleTo)
    {
        return visibleToArray[visibleTo.ordinal()];
    }

    public int getAccessibleToFileCount(AccessibleTo accessibleTo)
    {
        return accessibleToArray[accessibleTo.ordinal()];
    }

    public void addDescendant(DatasetItem item)
    {
        addRemoveItem(item, true, true);
    }

    public void onChildAdded(DatasetItem item)
    {
        addRemoveItem(item, false, true);
    }

    public void onChildRemoved(DatasetItem item)
    {
        addRemoveItem(item, false, false);
    }

    public void onDescendantRemoved(DatasetItem item)
    {
        addRemoveItem(item, true, false);
    }

    protected void addRemoveItem(DatasetItem item, boolean descendant, boolean add)
    {
        int count = add ? 1 : -1;

        try
        {
            if (item instanceof FileItem)
            {
                FileItem fileItem = (FileItem) item;

                if (!descendant) // direct child
                {
                    childFileCount += count;
                }
                totalFileCount += count;

                if (fileItem.getCreatorRole() != null)
                    creatorRoleArray[fileItem.getCreatorRole().ordinal()] = Math.max(0, creatorRoleArray[fileItem.getCreatorRole().ordinal()] + count);
                if (fileItem.getVisibleTo() != null)
                    visibleToArray[fileItem.getVisibleTo().ordinal()] = Math.max(0, visibleToArray[fileItem.getVisibleTo().ordinal()] + count);
                if (fileItem.getAccessibleTo() != null)
                    accessibleToArray[fileItem.getAccessibleTo().ordinal()] = Math.max(0, accessibleToArray[fileItem.getAccessibleTo().ordinal()] + count);
            }
            else if (item instanceof FolderItem)
            {
                FolderItem folderItem = (FolderItem) item;

                if (!descendant) // direct child
                {
                    childFolderCount += count;
                }
                totalFolderCount += count;

                totalFileCount += (folderItem.getTotalFileCount() * count);
                totalFolderCount += (folderItem.getTotalFolderCount() * count);
                for (int i = 0; i < creatorRoleArray.length; i++)
                {
                    creatorRoleArray[i] = Math.max(0, creatorRoleArray[i] + (folderItem.getCreatorRoleFileCount(CreatorRole.values()[i]) * count));
                }

                for (int i = 0; i < visibleToArray.length; i++)
                {
                    visibleToArray[i] = Math.max(0, visibleToArray[i] + (folderItem.getVisibleToFileCount(VisibleTo.values()[i]) * count));
                }

                for (int i = 0; i < accessibleToArray.length; i++)
                {
                    accessibleToArray[i] = Math.max(0, accessibleToArray[i] + (folderItem.getAccessibleToFileCount(AccessibleTo.values()[i]) * count));
                }
            }
        }
        finally
        {
            if (childFileCount < 0)
                childFileCount = 0;
            if (totalFileCount < 0)
                totalFileCount = 0;
            if (childFolderCount < 0)
                childFolderCount = 0;
            if (totalFolderCount < 0)
                totalFolderCount = 0;

            setDirty(true);
        }
    }

    protected void onChildStateChange(CreatorRole oldCreatorRole, CreatorRole newCreatorRole)
    {
        if (oldCreatorRole != null)
        {
            creatorRoleArray[oldCreatorRole.ordinal()]--;
        }
        if (newCreatorRole != null)
        {
            creatorRoleArray[newCreatorRole.ordinal()]++;
        }
        setDirty(true);
    }

    /*
     * COMMENTED OUT FOR RELEASE 2.8 public void onChildStateChange(String oldStreamingUrl, String
     * newStreamingUrl) { setDirty(true); }
     */

    protected void onChildStateChange(VisibleTo oldVisibleTo, VisibleTo newVisibleTo)
    {
        if (oldVisibleTo != null)
        {
            visibleToArray[oldVisibleTo.ordinal()]--;
        }
        if (newVisibleTo != null)
        {
            visibleToArray[newVisibleTo.ordinal()]++;
        }
        setDirty(true);
    }

    protected void onChildStateChange(AccessibleTo oldAcessibleTo, AccessibleTo newAccessibleTo)
    {
        if (oldAcessibleTo != null)
        {
            accessibleToArray[oldAcessibleTo.ordinal()]--;
        }
        if (newAccessibleTo != null)
        {
            accessibleToArray[newAccessibleTo.ordinal()]++;
        }
        setDirty(true);
    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

}
