package nl.knaw.dans.easy.web.search;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.wicket.components.search.facets.CollapsedFacetValue;
import nl.knaw.dans.common.wicket.components.search.facets.FacetValueCollapser;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineFacetValueCollapser implements FacetValueCollapser<String>
{
	private static final long serialVersionUID = 7665921824841244218L;
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(DisciplineFacetValueCollapser.class);
	
	private boolean showZeroCountFacets;

	public DisciplineFacetValueCollapser(boolean showZeroCountFacets)
	{
		this.showZeroCountFacets = showZeroCountFacets;
	}

	public List<CollapsedFacetValue<String>> collapse(
			List<FacetValue<String>> originalValues,
			FacetValue<String> selectedValue)
	{
		List<CollapsedFacetValue<String>> collapsedValues = new ArrayList<CollapsedFacetValue<String>>();
		try
		{		
			DisciplineContainer searchDiscipline = null;
			if (selectedValue == null)
				searchDiscipline = Services.getDisciplineService().getRootDiscipline();
			else
				searchDiscipline = Services.getDisciplineService().getDisciplineById(new DmoStoreId(selectedValue.getValue()));
		
			for(DisciplineContainer subDiscipline : searchDiscipline.getSubDisciplines())
			{
				List<FacetValue<String>> foundFacetValues = getFacetValuesOfDiscipline(subDiscipline, originalValues);
				int facetCount = getSummedFacetCount(foundFacetValues);

				if (facetCount > 0 || showZeroCountFacets)
				{
					CollapsedFacetValue<String> cfv = new CollapsedFacetValue<String>();
					cfv.setValue(subDiscipline.getStoreId());
					cfv.setCount(facetCount);
					cfv.setCollapsedValues(foundFacetValues);
					
					collapsedValues.add(cfv);
				}
			}
		} 
		catch (Exception e)
		{
			LOGGER.error("Unable to collapse facet values for disciplines", e);
			throw new InternalWebError();
		}
		
		return collapsedValues;
	}
	
	private int getSummedFacetCount(List<FacetValue<String>> foundFacetValues)
	{
		int count = 0;
		for (FacetValue<String> facetValue : foundFacetValues)
		{
			count += facetValue.getCount();
		}
		return count;
	}

	private List<FacetValue<String>> getFacetValuesOfDiscipline(
				DisciplineContainer discipline,
				List<FacetValue<String>> facetValues) throws DomainException
	{
		List<FacetValue<String>> result = new ArrayList<FacetValue<String>>(); 
		
		// search on this level
		for (FacetValue<String> facetValue : facetValues)
		{
			if (facetValue.getValue().equals(discipline.getStoreId()))
				result.add(facetValue);
		}
		
		// search on sub levels
		for(DisciplineContainer subDiscipline : discipline.getSubDisciplines())
		{
			result.addAll( getFacetValuesOfDiscipline(subDiscipline, facetValues) );
		}
		
		return result;
	}

}
