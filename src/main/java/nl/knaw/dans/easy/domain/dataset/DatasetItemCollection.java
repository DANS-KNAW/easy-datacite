package nl.knaw.dans.easy.domain.dataset;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoCollectionMember;

public class DatasetItemCollection extends AbstractDmoCollection
{
	private static final long serialVersionUID = -3829949809753365029L;

	private static final String DATASET_ITEM_COLLECTION_SID = "dataset-item-collection:1";

	private static Set<Class<? extends DmoCollectionMember>> classes;
	 
	private static final DatasetItemCollection INSTANCE = new DatasetItemCollection();  
	
	public static  DatasetItemCollection getInstance()
	{
		return INSTANCE;
	}

	public DatasetItemCollection()
	{
		super(DATASET_ITEM_COLLECTION_SID);

		classes = new HashSet<Class<? extends DmoCollectionMember>>();
		classes.add(DatasetImpl.class);
		classes.add(FolderItemImpl.class);
		classes.add(FileItemImpl.class);
		
		addRelationConstraint(1, DatasetImpl.class,    Integer.MAX_VALUE, FolderItemImpl.class);
		addRelationConstraint(1, DatasetImpl.class,    Integer.MAX_VALUE, FileItemImpl.class);
		addRelationConstraint(1, FolderItemImpl.class, Integer.MAX_VALUE, FileItemImpl.class);
	}


	public Set<Class<? extends DmoCollectionMember>> getMemberClasses()
	{
		return classes;
	}

	public DmoNamespace getObjectNamespace()
	{
		return new DmoNamespace("dataset-item-collection");
	}
	
	@Override
	public String getStoreId()
	{
		return DATASET_ITEM_COLLECTION_SID;
	}

	public boolean isDeletable()
	{
		return false;
	}

	
}
