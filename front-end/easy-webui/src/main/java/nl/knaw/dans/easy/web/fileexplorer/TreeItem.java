package nl.knaw.dans.easy.web.fileexplorer;

import java.io.Serializable;
import java.util.ArrayList;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Georgi Khomeriki
 */
public class TreeItem implements Serializable, ITreeItem {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TreeItem.class);

    // (ResourceModel for VisibleTo/AccessibleTo/Creator doesn't work from here?)

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
            FolderItemVO folderItem = (FolderItemVO) itemVO;
            DmoStoreId folderStoreId = new DmoStoreId(folderItem.getSid());

            visibleTo = getReadableValuesFor(folderStoreId, VisibleTo.class);
            accessibleTo = getReadableValuesFor(folderStoreId, AccessibleTo.class);
            creator = getReadableValuesFor(folderStoreId, CreatorRole.class);

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

    private static String getReadableValuesFor(DmoStoreId container, Class<? extends FileItemVOAttribute> attribute) {
        FileStoreAccess fileStoreAccess = Data.getFileStoreAccess();
        StringBuffer result = new StringBuffer();
        try {
            for (FileItemVOAttribute value : fileStoreAccess.getValuesFor(container, attribute))
                result.append(makeValueReadable(value));
        }
        catch (IllegalArgumentException e) {
            logError(container, attribute, e);
        }
        catch (StoreAccessException e) {
            logError(container, attribute, e);
        }
        // remove last comma
        return result.toString().replaceAll(", $", "");
    }

    private static String makeValueReadable(FileItemVOAttribute value) {
        return StringUtil.firstCharToUpper(value.toString().replaceAll("_", " ").toLowerCase());
    }

    private static void logError(DmoStoreId dmoStoreId, Class<? extends FileItemVOAttribute> attribute, Exception e) {
        logger.error("could not fetch {} values for {} {}", dmoStoreId, attribute.getName(), e);
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
