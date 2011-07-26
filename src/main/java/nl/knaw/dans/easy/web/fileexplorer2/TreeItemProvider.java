package nl.knaw.dans.easy.web.fileexplorer2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy.TriState;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wickettree.ITreeProvider;
import wickettree.util.IntermediateTreeProvider;

public class TreeItemProvider implements ITreeProvider<ITreeItem>
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TreeItemProvider.class);
    
	
	private final EasyUser sessionUser;
	private final Dataset dataset;
	
	private ArrayList<ITreeItem> roots = new ArrayList<ITreeItem>();

	private HashMap<Enum<?>, CheckBox> filters;
	
	private boolean intermediate;
	
	public TreeItemProvider(String datasetSid, HashMap<Enum<?>, CheckBox> filters)
	{
		//this(false);
		this.filters = filters;
	    sessionUser = EasySession.getSessionUser();
	    try
        {
            dataset = Services.getDatasetService().getDataset(sessionUser, datasetSid);
        }
        catch (ServiceException e)
        {
            logger.error("Unable to load dataset: ", e);
            throw new InternalWebError();
        }
        
        // initialize tree data
		reload();
	}
	
	@SuppressWarnings("unchecked")
	public void reload() {
		try {
			roots.clear();
			FolderItemVO rootFolder = new FolderItemVO();
			rootFolder.setName("Dataset contents");
			rootFolder.setSid(dataset.getStoreId());
			TreeItem root = new TreeItem(rootFolder, null);
			
			List<ItemVO> items = Services.getItemService().getFilesAndFolders(sessionUser, dataset, dataset.getStoreId(), -1, -1, null, null);
			for(ItemVO item : items) {
				AuthzStrategy strategy = item.getAuthzStrategy();
				if(item instanceof FileItemVO && strategy.canUnitBeDiscovered("EASY_FILE")) {
					FileItemVO file = (FileItemVO)item;
					if(filters.get(file.getVisibleTo())!=null && 
							filters.get(file.getAccessibleTo()) != null &&
							filters.get(file.getCreatorRole()) != null &&
							filters.get(file.getVisibleTo()).getModelObject() && 
							filters.get(file.getAccessibleTo()).getModelObject() &&
							filters.get(file.getCreatorRole()).getModelObject()) {
						root.addChild(new TreeItem(item, root));
					}
				} else if(item instanceof FolderItemVO && !strategy.canChildrenBeDiscovered().equals(TriState.NONE)) {
					FolderItemVO folder = (FolderItemVO)item;
					boolean vision = false;
					boolean access = false;
					boolean creator = false;
					if(folder.getVisibleToList()!=null) {
						for(FolderItemVisibleTo v:folder.getVisibleToList()) {
							if(v.getVisibleTo()!=null && filters.get(v.getVisibleTo()) != null && filters.get(v.getVisibleTo()).getModelObject()) {	
								vision=true;
								break;
							}
						}
					}
					if(folder.getAccessibleToList()!=null) {
						for(FolderItemAccessibleTo a:folder.getAccessibleToList()) {
							if(a.getAccessibleTo()!=null && filters.get(a.getAccessibleTo()) != null && filters.get(a.getAccessibleTo()).getModelObject()) {	
								access=true;
								break;
							}
						}
					}
					if(folder.getCreatorRoles()!=null) {
						for(FolderItemCreatorRole c:folder.getCreatorRoles()) {
							if(c.getCreatorRole()!=null && filters.get(c.getCreatorRole()) != null && filters.get(c.getCreatorRole()).getModelObject()) {	
								creator=true;
								break;
							}
						}
					}
					if(vision && access && creator) {
						root.addChild(new TreeItem(item, root));
					}
				}
			}
			Collections.sort(root.getChildren());
			roots.add(root);
		} catch (ServiceException e) {
			logger.error("Error while trying to load TreeItemProvider.", e);
		}
	}

	public TreeItemProvider(boolean intermediate)
	{
		this.intermediate = intermediate;
		sessionUser = EasySession.getSessionUser();
		dataset = null;
	}

	public void detach()
	{
		// nothing to do
	}

	public Iterator<ITreeItem> getRoots()
	{
		return roots.iterator();
	}
	
	public ITreeItem getRoot() {
		return roots.get(0);
	}

	public boolean hasChildren(ITreeItem item)
	{
		boolean result = false;
		try {
			result = item.getParent() == null || !item.getChildren().isEmpty() || Services.getItemService().hasChildItems(item.getId());
		} catch (ServiceException e) {
			logger.error("Error while fetching children.", e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Iterator<ITreeItem> getChildren(final ITreeItem item)
	{
		try {
			if(Services.getItemService().hasChildItems(item.getId()) && item.getChildrenWithFiles().isEmpty()) {
				List<ItemVO> items = Services.getItemService().getFilesAndFolders(sessionUser, dataset, item.getId(), -1, -1, null, null);
				for(ItemVO child : items) {
					AuthzStrategy strategy = child.getAuthzStrategy();
					if(child instanceof FileItemVO && strategy.canUnitBeDiscovered("EASY_FILE")) {
						FileItemVO file = (FileItemVO)child;
						if(filters.get(file.getVisibleTo())!=null && 
								filters.get(file.getAccessibleTo()) != null &&
								filters.get(file.getCreatorRole()) != null &&
								filters.get(file.getVisibleTo()).getModelObject() && 
								filters.get(file.getAccessibleTo()).getModelObject() &&
								filters.get(file.getCreatorRole()).getModelObject()) {
							item.addChild(new TreeItem(child, (TreeItem)item));
						}
					} else if(child instanceof FolderItemVO && !strategy.canChildrenBeDiscovered().equals(TriState.NONE)) {
						FolderItemVO folder = (FolderItemVO)child;
						boolean vision = false;
						boolean access = false;
						boolean creator = false;
						if(folder.getVisibleToList()!=null) {
							for(FolderItemVisibleTo v:folder.getVisibleToList()) {
								if(v.getVisibleTo()!=null && filters.get(v.getVisibleTo()) != null && filters.get(v.getVisibleTo()).getModelObject()) {	
									vision=true;
									break;
								}
							}
						}
						if(folder.getAccessibleToList()!=null) {
							for(FolderItemAccessibleTo a:folder.getAccessibleToList()) {
								if(a.getAccessibleTo()!=null && filters.get(a.getAccessibleTo()) != null && filters.get(a.getAccessibleTo()).getModelObject()) {	
									access=true;
									break;
								}
							}
						}
						if(folder.getCreatorRoles()!=null) {
							for(FolderItemCreatorRole c:folder.getCreatorRoles()) {
								if(c.getCreatorRole()!=null && filters.get(c.getCreatorRole()) != null && filters.get(c.getCreatorRole()).getModelObject()) {	
									creator=true;
									break;
								}
							}
						}
						if(vision && access && creator) {
							item.addChild(new TreeItem(child, (TreeItem)item));
						}
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("Error while fetching children.", e);
		}
		
		if (intermediate)
		{
			if (!item.isLoaded())
			{
				asynchronuous(new Runnable()
				{
					public void run()
					{
						item.setLoaded(true);
					}
				});

				// mark children intermediate
				return IntermediateTreeProvider.intermediate(Collections.<ITreeItem> emptyList().iterator());
			}
		}
		
		// sort the children alphabetically
		Collections.sort(item.getChildren());
		
		return item.getChildren().iterator();
	}
	
	/**
	 * We're cheating here - the given runnable is run immediately.
	 */
	private void asynchronuous(Runnable runnable)
	{
		runnable.run();
	}
	
	public void resetLoaded()
	{
		for (ITreeItem item : roots)
		{
			resetLoaded(item);
		}
	}

	private static void resetLoaded(ITreeItem item)
	{
		item.setLoaded(false);

		for (Object child : item.getChildren())
		{
			resetLoaded((ITreeItem)child);
		}
	}

	/**
	 * Creates a {@link FooModel}.
	 */
	public IModel<ITreeItem> model(ITreeItem item)
	{
		return new ItemModel(item);
	}

	/**
	 * Get a {@link Foo} by its id.
	 */
	public ITreeItem get(String id)
	{
		return get(roots, id);
	}
	
	private static ITreeItem get(List<ITreeItem> items, String id)
	{
		for (ITreeItem item : items)
		{
			if (item.getId().equals(id))
			{
				return item;
			}

			ITreeItem temp = get(item.getChildren(), id);
			if (temp != null)
			{
				return temp;
			}
		}

		return null;
	}
	
	/**
	 * A {@link Model} which uses an id to load its {@link Foo}.
	 * 
	 * If {@link Foo}s were {@link Serializable} you could just use a standard
	 * {@link Model}.
	 * 
	 * @see #equals(Object)
	 * @see #hashCode()
	 */
	private class ItemModel extends LoadableDetachableModel<ITreeItem>
	{
		private static final long serialVersionUID = 1L;

		private String id;

		public ItemModel(ITreeItem item)
		{
			super(item);
			
			id = item.getId();
		}

		@Override
		protected ITreeItem load()
		{
			return get(id);
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ItemModel)
			{
				return ((ItemModel)obj).id.equals(this.id);
			}
			return false;
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public int hashCode()
		{
			return id.hashCode();
		}
	}

	public HashMap<Enum<?>, CheckBox> getFilters() {
		return filters;
	}

	public void setFilters(HashMap<Enum<?>, CheckBox> filters) {
		this.filters = filters;
	}
}
