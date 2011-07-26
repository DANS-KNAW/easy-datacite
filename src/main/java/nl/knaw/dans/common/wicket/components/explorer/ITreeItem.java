package nl.knaw.dans.common.wicket.components.explorer;

import java.util.ArrayList;

public interface ITreeItem {
	public static enum Type {FILE, FOLDER};
	
	public void addChild(ITreeItem item);
	public ArrayList getChildren();
	public ArrayList getChildrenWithFiles();
	public ITreeItem getParent();
	public Type getType();
	public String getId();
	public boolean isLoaded();
	public void setLoaded(boolean loaded);
	public String getName();
}
