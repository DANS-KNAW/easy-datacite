package nl.knaw.dans.easy.web.fileexplorer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
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

    @SpringBean(name = "itemService")
    private ItemService itemService;

    public TreeItem(ItemVO itemVO, ITreeItem parent) throws StoreAccessException {
        InjectorHolder.getInjector().inject(this);

        this.itemVO = itemVO;
        this.parent = parent;

        visibleTo = new ReadableValues(itemService.getItemVoVisibilities(itemVO)).toString();
        accessibleTo = new ReadableValues(itemService.getItemVoAccessibilities(itemVO)).toString();
        creator = new ReadableValues(itemService.getItemVoCreatorRoles(itemVO)).toString();
        if (itemVO instanceof FolderItemVO) {
            size = 0;
            sizeAsString = "";
            type = Type.FOLDER;
            mimeType = "folder";
        } else {
            FileItemVO fileItem = (FileItemVO) itemVO;
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
        try {
            return new TreeItem(itemVO, parent);
        }
        catch (StoreAccessException e) {
            throw new RuntimeException(e);
        }
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
