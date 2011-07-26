package nl.knaw.dans.easy.web.search;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.FacetField;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.exceptions.FieldNotFoundException;
import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.results.SortLinkConfig;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;

public class StateSortLinkConfig extends SortLinkConfig
{
	private static final long serialVersionUID = 2497638297166331847L;
	
	private DatasetState[] mustHaveStates;

	public StateSortLinkConfig(String name, SortType type, DatasetState... mustHaveStates)
	{
		super(name, type);
		this.mustHaveStates = mustHaveStates;
	}
	
	public StateSortLinkConfig(String name, SortType type, SortOrder initialSortOrder, DatasetState... mustHaveStates)
    {
        super(name, type, initialSortOrder);
        this.mustHaveStates = mustHaveStates;
    }

	@Override
	public boolean isVisible(SearchData sdata)
	{
		FacetField stateFacet;
		try
		{
			stateFacet = sdata.getResult().getFacetByName(
					EasyDatasetSB.DS_STATE_FIELD);
		} catch (FieldNotFoundException e)
		{
			return false;
		}
		
		if (mustHaveStates == null || mustHaveStates.length == 0)
		{
			// no must have states means we have to have at least
			// 2 or more facets of different kinds to show this link
			int countEnabledFacets = 0;
			for (FacetValue<?> value : stateFacet.getValue())
			{
				if (value.getCount() > 0)
					countEnabledFacets++;
			}
			
			return countEnabledFacets > 1;
		}
		else
		{
			for (FacetValue<?> value : stateFacet.getValue())
			{
				if (isMustHaveState(value) && value.getCount() > 0)
					return true;
			}
			return false;
		}
	}

	private boolean isMustHaveState(FacetValue<?> value)
	{
		for (int i = 0; i < mustHaveStates.length; i++)
		{
			if (mustHaveStates[i].toString().equals(value.getValue()))
				return true;
		}
		return false;
	}
	
}
