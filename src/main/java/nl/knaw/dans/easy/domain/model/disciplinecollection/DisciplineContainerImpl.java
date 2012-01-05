package nl.knaw.dans.easy.domain.model.disciplinecollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.RepoUtil;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoRecursiveItem;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public class DisciplineContainerImpl extends AbstractDmoRecursiveItem
		implements DisciplineContainer
{
	private static final long serialVersionUID = -1773156502403047307L;
	private List<DisciplineContainer> childDisciplinesCache = null;
	private DisciplineMetadata metadata; 

	public DisciplineContainerImpl(String storeId)
	{
		super(storeId);
	}
	
	public DmoNamespace getObjectNamespace()
	{
	 	return NAMESPACE;
	}

	@Override
	public Set<String> getContentModels()
	{
		 Set<String> contentModels = super.getContentModels();
		 contentModels.add(DisciplineContainer.CONTENT_MODEL);
		 return contentModels;
	}
	
	@Override
	public List<MetadataUnit> getMetadataUnits()
	{
		List<MetadataUnit> mdUnits = super.getMetadataUnits();
		mdUnits.add( getDisciplineMetadata() );
		return mdUnits;
	}
	
	public boolean isDeletable()
	{
		return true;
	}

	public String getName()
	{
		return getLabel();
	}

	public void setName(String name)
	{
		setLabel(name);
	}

	public Set<DmoCollection> getCollections()
	{
		HashSet<DmoCollection> col = new HashSet<DmoCollection>(1);
		col.add(DisciplineCollectionImpl.getInstance());
		return null;
	}
	
	public List<DisciplineContainer> getSubDisciplines() throws DomainException 
	{
		try
		{
			boolean childInvalidated = false;
			if (childDisciplinesCache != null)
			{
				// check if one of the children has been invalidated
				for (DisciplineContainer discipline : childDisciplinesCache)
				{
					if (discipline.isInvalidated())
					{
						childInvalidated = true;
						break;
					}
				}
			}
			
			if (childDisciplinesCache == null || childInvalidated)
			{
				List<DisciplineContainer> childDisciplines = new ArrayList<DisciplineContainer>();
				
				// get all child disciplines from the store
				Set<String> childSids = getChildSids();
				for (String childSid : childSids)
				{
					String namespace = RepoUtil.getNamespaceFromSid(childSid);
					if (namespace.equals(DisciplineContainer.NAMESPACE.getValue()))
					{
						// check if we still have a validated copy of the object in the old cache
						if (childDisciplinesCache != null)
						{
							DisciplineContainer validCachedChild = null;
							for (DisciplineContainer cachedChild : childDisciplinesCache)
							{
								if (!cachedChild.isInvalidated() &&
										cachedChild.getStoreId().equals(childSid))
								{
									validCachedChild = cachedChild;
									break;
								}
							}
							
							if (validCachedChild != null)
							{
								childDisciplines.add(validCachedChild);
								continue;
							}
						}
						
						// get child from the store
						DisciplineContainer child = (DisciplineContainer) getStore().retrieve(childSid);
						childDisciplines.add( child );
					}
				}
	
				childDisciplinesCache = childDisciplines;
			}
		}
		catch(RepositoryException e)
		{
			throw new DomainException(e);
		}
		
		return childDisciplinesCache;
	}

	public DisciplineMetadata getDisciplineMetadata()
	{
		if (metadata == null)
			metadata = new DisciplineMetadataImpl();
		return metadata;
	}

	public void setDisciplineMetadata(DisciplineMetadata dmd)
	{
		metadata = dmd;
	}

}
