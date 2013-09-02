package nl.knaw.dans.easy.web.fileexplorer;

import java.io.Serializable;
import java.util.ArrayList;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

/**
 * @author Georgi Khomeriki
 */
public class TreeItem implements Serializable, ITreeItem
{
    private static final long serialVersionUID = 1L;

    // TODO: somehow get display values for VisibleTo/AccessibleTo/Creator from properties file
    // (ResourceModel doesn't work from here?)

    private int size;
    private String sizeAsString;
    private String creator;
    private String visibleTo;
    private String accessibleTo;
    private String mimeType;
    private ArrayList<ITreeItem> children = new ArrayList<ITreeItem>(); // directories only
    private ArrayList<ITreeItem> childrenWithFiles = new ArrayList<ITreeItem>(); // files and directories
    private ITreeItem parent;
    private Type type;
    private ItemVO itemVO;
    private boolean loaded;

    public TreeItem(ItemVO itemVO, ITreeItem parent)
    {
        this.itemVO = itemVO;
        this.parent = parent;

        if (itemVO instanceof FolderItemVO)
        {
            // we're dealing with a folder
            FolderItemVO folderItem = (FolderItemVO) itemVO;
            visibleTo = "";
            for (FolderItemVisibleTo fivt : folderItem.getVisibleToList())
            {
                visibleTo += makeValueReadable(fivt.getVisibleTo().toString()) + ", ";
            }
            if (visibleTo.length() > 2)
                visibleTo = visibleTo.substring(0, visibleTo.length() - 2); // remove last comma
            accessibleTo = "";
            for (FolderItemAccessibleTo fiat : folderItem.getAccessibleToList())
            {
                accessibleTo += makeValueReadable(fiat.getAccessibleTo().toString()) + ", ";
            }
            if (accessibleTo.length() > 2)
                accessibleTo = accessibleTo.substring(0, accessibleTo.length() - 2); // remove last comma
            creator = "";
            for (FolderItemCreatorRole role : folderItem.getCreatorRoles())
            {
                creator += makeValueReadable(role.getCreatorRole().toString()) + ", ";
            }
            if (creator.length() > 2)
                creator = creator.substring(0, creator.length() - 2); // remove last comma
            size = 0;
            sizeAsString = "";
            type = Type.FOLDER;
            mimeType = "folder";
        }
        else
        {
            // we're dealing with a file
            FileItemVO fileItem = (FileItemVO) itemVO;
            visibleTo = makeValueReadable(fileItem.getVisibleTo().toString());
            accessibleTo = makeValueReadable(fileItem.getAccessibleTo().toString());
            creator = makeValueReadable(fileItem.getCreatorRole().toString());
            size = fileItem.getSize();
            sizeAsString = "" + fileItem.getSize();
            type = Type.FILE;
            mimeType = fileItem.getMimetype();
        }
    }

    public void addChild(ITreeItem child)
    {
        ITreeItem item = (ITreeItem) child;
        if (item.getType().equals(Type.FOLDER))
        {
            children.add(item);
            childrenWithFiles.add(item);
        }
        else if (item.getType().equals(Type.FILE))
        {
            childrenWithFiles.add(item);
        }
    }

    @Override
    public void removeChild(ITreeItem item)
    {
        if (children.contains(item))
        {
            children.remove(item);
        }
        if (childrenWithFiles.contains(item))
        {
            childrenWithFiles.remove(item);
        }
    }

    // converts values of Creator, VisibleTo and AccessibleTo to readable values
    public static String makeValueReadable(String value)
    {
        String result = "";
        String[] parts = value.split("_");
        for (String part : parts)
        {
            String convertedPart = part.charAt(0) + part.substring(1).toLowerCase();
            result += convertedPart + " ";
        }
        return result.length() > 1 ? result.substring(0, result.length() - 1) : result;
    }

    public String getId()
    {
        return itemVO.getSid();
    }

    public String getName()
    {
        return itemVO.getName();
    }

    public int getSize()
    {
        return size;
    }

    public String getSizeAsString()
    {
        return sizeAsString;
    }

    public String getCreator()
    {
        return creator;
    }

    public String getVisibleTo()
    {
        return visibleTo;
    }

    public String getAccessibleTo()
    {
        return accessibleTo;
    }

    public Type getType()
    {
        return type;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    @Override
    public String toString()
    {
        return itemVO.getName();
    }

    @Override
    public Object clone()
    {
        return new TreeItem(itemVO, parent);
    }

    public boolean hasChildren()
    {
        return children.size() > 0;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public ArrayList<ITreeItem> getChildren()
    {
        return children;
    }

    public ArrayList<ITreeItem> getChildrenWithFiles()
    {
        return childrenWithFiles;
    }

    public ITreeItem getParent()
    {
        return parent;
    }

    public void setParent(ITreeItem parent)
    {
        this.parent = parent;
    }

    public ItemVO getItemVO()
    {
        return itemVO;
    }

    public void setItemVO(ItemVO itemVO)
    {
        this.itemVO = itemVO;
    }

    @Override
    public int compareTo(ITreeItem o)
    {
        return this.getName().compareTo(o.getName());
    }

}
