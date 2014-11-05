package nl.knaw.dans.easy.web.fileexplorer;

import java.io.Serializable;
import java.util.ArrayList;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.util.StringUtil;

/**
 * @author Georgi Khomeriki
 */
public class TreeItem implements Serializable, ITreeItem {

    private static final long serialVersionUID = 1L;

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

    public TreeItem(ItemVO itemVO, ITreeItem parent) {
        this.itemVO = itemVO;
        this.parent = parent;

        if (itemVO instanceof FolderItemVO) {
            FolderItemVO folderItemVO = (FolderItemVO) itemVO;
            visibleTo = makeValuesReadable(folderItemVO.getVisibilities().toArray());
            accessibleTo = makeValuesReadable(folderItemVO.getAccessibilities().toArray());
            creator = makeValuesReadable(folderItemVO.getCreatorRoles().toArray());
            size = 0;
            sizeAsString = "";
            type = Type.FOLDER;
            mimeType = "folder";
        } else {
            FileItemVO fileItem = (FileItemVO) itemVO;
            visibleTo = makeValueReadable(fileItem.getVisibleTo());
            accessibleTo = makeValueReadable(fileItem.getAccessibleTo());
            creator = makeValueReadable(fileItem.getCreatorRole());
            size = fileItem.getSize();
            sizeAsString = "" + fileItem.getSize();
            type = Type.FILE;
            mimeType = fileItem.getMimetype();
        }
    }

    public void addChild(ITreeItem child) {
        ITreeItem item = (ITreeItem) child;
        if (item.getType().equals(Type.FOLDER)) {
            children.add(item);
            childrenWithFiles.add(item);
        } else if (item.getType().equals(Type.FILE)) {
            childrenWithFiles.add(item);
        }
    }

    @Override
    public void removeChild(ITreeItem item) {
        if (children.contains(item)) {
            children.remove(item);
        }
        if (childrenWithFiles.contains(item)) {
            childrenWithFiles.remove(item);
        }
    }

    private static String makeValueReadable(FileItemVOAttribute value) {
        return StringUtil.firstCharToUpper(value.toString().replaceAll("_", " ").toLowerCase());
    }

    private static String makeValuesReadable(Object... values) {
        StringBuffer result = new StringBuffer();
        for (Object value : values)
            result.append(StringUtil.firstCharToUpper(value.toString().replaceAll("_", " ").toLowerCase()));
        return result.toString();
    }

    public String getId() {
        return itemVO.getSid();
    }

    public String getName() {
        return itemVO.getName();
    }

    public int getSize() {
        return size;
    }

    public String getSizeAsString() {
        return sizeAsString;
    }

    public String getCreator() {
        return creator;
    }

    public String getVisibleTo() {
        return visibleTo;
    }

    public String getAccessibleTo() {
        return accessibleTo;
    }

    public Type getType() {
        return type;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return itemVO.getName();
    }

    @Override
    public Object clone() {
        return new TreeItem(itemVO, parent);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public ArrayList<ITreeItem> getChildren() {
        return children;
    }

    public ArrayList<ITreeItem> getChildrenWithFiles() {
        return childrenWithFiles;
    }

    public ITreeItem getParent() {
        return parent;
    }

    public void setParent(ITreeItem parent) {
        this.parent = parent;
    }

    public ItemVO getItemVO() {
        return itemVO;
    }

    public void setItemVO(ItemVO itemVO) {
        this.itemVO = itemVO;
    }

    @Override
    public int compareTo(ITreeItem o) {
        return this.getName().compareTo(o.getName());
    }
}
