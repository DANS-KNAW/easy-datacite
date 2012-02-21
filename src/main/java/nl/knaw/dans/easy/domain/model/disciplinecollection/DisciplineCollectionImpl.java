package nl.knaw.dans.easy.domain.model.disciplinecollection;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoCollectionMember;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;

public class DisciplineCollectionImpl extends AbstractDmoCollection implements
		 DisciplineCollection
{
	public static final String EASY_DISCIPLINE_ROOT = "easy-discipline:root";
	
	public static final DmoStoreId EASY_DISCIPLINE_ROOT_DMO_STORE_ID = new DmoStoreId(EASY_DISCIPLINE_ROOT);

	private static final long serialVersionUID = -382996789753365029L;

	private static final String DISCIPLINE_COLLECTION_SID = "dataset-item-collection:root";
	
	private static final DmoStoreId DISCIPLINE_COLLECTION_DMO_STORE_ID = new DmoStoreId(DISCIPLINE_COLLECTION_SID);

	private static Set<Class<? extends DmoCollectionMember>> classes;
	 
	private static final DisciplineCollectionImpl INSTANCE = new DisciplineCollectionImpl();  
	
	private DisciplineContainer rootDiscipline;

	public static  DisciplineCollectionImpl getInstance()
	{
		return INSTANCE;
	}
	
	private DisciplineCollectionImpl()
	{
		super(DISCIPLINE_COLLECTION_SID);

		classes = new HashSet<Class<? extends DmoCollectionMember>>();
		classes.add(DisciplineContainerImpl.class);
		classes.add(DatasetImpl.class);
		
		addRelationConstraint(Integer.MAX_VALUE, DisciplineContainerImpl.class,    
				Integer.MAX_VALUE, DisciplineContainerImpl.class);
		addRelationConstraint(1, DisciplineContainerImpl.class,    
				Integer.MAX_VALUE, DatasetImpl.class);
	}

	public Set<Class<? extends DmoCollectionMember>> getMemberClasses()
	{
		return classes;
	}

	public DmoNamespace getDmoNamespace()
	{
		return NAME_SPACE;
	}
	
	@Override
	public String getStoreId()
	{
		return DISCIPLINE_COLLECTION_SID;
	}

	public boolean isDeletable()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.IDisciplineCollection#getRootDiscipline()
	 */
	public DisciplineContainer getRootDiscipline() throws DomainException, ObjectNotFoundException
	{
		try
		{
		    // 
			if (rootDiscipline == null || rootDiscipline.isInvalidated())
			{	
				rootDiscipline = (DisciplineContainer) Data.getEasyStore().retrieve(EASY_DISCIPLINE_ROOT_DMO_STORE_ID);
			}
			return rootDiscipline;
		}
		catch(ObjectNotInStoreException e)
		{
			throw new ObjectNotFoundException("Root discipline not found in store", e);
		}
		catch(RepositoryException e)
		{
			throw new DomainException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.IDisciplineCollection#getDisciplineById(java.lang.String)
	 */
	public DisciplineContainer getDisciplineBySid(DmoStoreId disciplineId) throws ObjectNotFoundException, DomainException
	{
		DisciplineContainer d = null;
		d = getSubDisciplineById(getRootDiscipline(), disciplineId);
		if (d == null)
			throw new ObjectNotFoundException("Discipline with id '"+ disciplineId +"' was not found.");
		return d;
	}

	private DisciplineContainer getSubDisciplineById(DisciplineContainer discipline,
				DmoStoreId disciplineId) throws DomainException 
	{
		for (DisciplineContainer subDiscipline : discipline.getSubDisciplines() )
		{
			if (subDiscipline.getDmoStoreId().equals(disciplineId))
				return subDiscipline;
			DisciplineContainer result = getSubDisciplineById(subDiscipline, disciplineId);
			if (result != null)
				return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.IDisciplineCollection#getDisciplineByName(java.lang.String)
	 */
	public DisciplineContainer getDisciplineByName(String disciplineName) throws ObjectNotFoundException, DomainException
	{
		DisciplineContainer d = null;
		d = getSubDisciplineByName(getRootDiscipline(), disciplineName);
		if (d == null)
			throw new ObjectNotFoundException("Discipline with name '"+ disciplineName +"' was not found.");
		return d;
	}

	private DisciplineContainer getSubDisciplineByName(DisciplineContainer discipline,
			String disciplineName) throws DomainException 
	{
		for (DisciplineContainer subDiscipline : discipline.getSubDisciplines() )
		{
			if (subDiscipline.getLabel().equalsIgnoreCase(disciplineName))
				return subDiscipline;
			DisciplineContainer result = getSubDisciplineByName(subDiscipline, disciplineName);
			if (result != null)
				return result;
		}
		return null;
	}
}
