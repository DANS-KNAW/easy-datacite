package nl.knaw.dans.common.wicket.components.explorer;

import java.util.ArrayList;

public interface ITreeItem extends Comparable<ITreeItem> {
    enum Type {
        FILE, FOLDER
    };

    void addChild(ITreeItem item);

    ArrayList<ITreeItem> getChildren();

    ArrayList<ITreeItem> getChildrenWithFiles();

    ITreeItem getParent();

    Type getType();

    String getId();

    boolean isLoaded();

    void setLoaded(boolean loaded);

    String getName();

    void removeChild(ITreeItem item);
}
