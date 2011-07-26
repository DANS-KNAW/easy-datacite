package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;

public class ChoiceListGetter
{
	private static ChoiceListGetter INSTANCE = new ChoiceListGetter();

	public static final String CHOICELIST_CUSTOM_PREFIX = "custom.";
	
	public static final String CHOICELIST_DISCIPLINES_POSTFIX = "disciplines";

    /**
     * Get the singleton instance of ChoiceListCache.
     * 
     * @return singleton instance of ChoiceListCache
     */
    public static ChoiceListGetter getInstance()
    {
        return INSTANCE;
    }

	/**
	 * Gets a choicelist object based on a listId 
	 * @param listId the id of the chocielist
	 * @param locale the locale in which the choicelist should be returned
	 * @return a choicelist object
	 * @throws DomainException 
	 * @throws ObjectNotFoundException 
	 */
    public ChoiceList getChoiceList(String listId, Locale locale) throws CacheException, ResourceNotFoundException, DomainException, ObjectNotFoundException
	{
		if (listId.startsWith(CHOICELIST_CUSTOM_PREFIX))
		{
			String customListId = listId.substring(CHOICELIST_CUSTOM_PREFIX.length());
			if (customListId.equals(CHOICELIST_DISCIPLINES_POSTFIX))
				return getDisciplinesChoiceList(listId, locale);
			else
	            throw new ResourceNotFoundException("A custom choicelist with id '" + listId + "' was not found.");
		}
		else
			return ChoiceListCache.getInstance().getList(listId, locale);
	}

	/* --------------------------------
	 * DISCIPLINES CUSTOM CHOICE GETTER 
	 * -------------------------------- */
	
	private ChoiceList getDisciplinesChoiceList(String listId, Locale locale) throws DomainException, ObjectNotFoundException 
	{
		DisciplineContainer rootDiscipline = DisciplineCollectionImpl.getInstance().getRootDiscipline();
		
		List<KeyValuePair> disciplineList = new ArrayList<KeyValuePair>();
		createDisciplineKvpList(0, disciplineList, rootDiscipline);

		return new ChoiceList(disciplineList);
	}

	private void createDisciplineKvpList(int indent, List<KeyValuePair> disciplineList,
			DisciplineContainer parentDiscipline) throws DomainException
	{
		List<DisciplineContainer> disciplines = parentDiscipline.getSubDisciplines();
		SortedMap<Integer, DisciplineContainer> disciplineMap = new TreeMap<Integer, DisciplineContainer>(); 

		for (DisciplineContainer discipline : disciplines)
			disciplineMap.put(discipline.getDisciplineMetadata().getOrder(), discipline);
		
		for (DisciplineContainer discipline : disciplineMap.values())
		{		
			String name = discipline.getName();
			KeyValuePair kvp = new KeyValuePair(discipline.getStoreId(), name);
			kvp.setIndent(indent);

			disciplineList.add( kvp );
			
			createDisciplineKvpList(indent+1, disciplineList, discipline);
		}
	}
}
