package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.*;

import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public abstract class AbstractItemFieldFilter<FIELD> implements ItemFieldFilter<FIELD>
{
	protected Set<FIELD> desiredValues = new HashSet<FIELD>();

	public AbstractItemFieldFilter()
	{
	}

    public void addDesiredValues(FIELD... values)
    {
        desiredValues.addAll(Arrays.asList(values));
    }

	public Set<FIELD> getDesiredValues()
	{
		return this.desiredValues;
	}

	public List<? extends ItemVO> apply(final List<? extends ItemVO> itemList) throws DomainException
	{
		List<ItemVO> result = new ArrayList<ItemVO>();
		for (ItemVO item : itemList)
		{
			if (!filterOut(item))
				result.add(item);
		}
		return result;
	}

	@Override
	public String toString()
	{
		String result = this.getClass().getName() +" on field "+ this.getFilterField().filePropertyName;
		Iterator<FIELD> i = desiredValues.iterator();
		result += "desiredValues = {";
		while(i.hasNext())
		{
			result += i.next().toString();
			if (i.hasNext()) result += ", ";
		}
		result += "}";
		return result;
	}
}
