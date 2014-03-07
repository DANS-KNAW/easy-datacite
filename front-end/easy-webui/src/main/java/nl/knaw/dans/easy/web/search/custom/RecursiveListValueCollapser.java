package nl.knaw.dans.easy.web.search.custom;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.bean.RecursiveEntry;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.repo.bean.RecursiveNode;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.facets.CollapsedFacetValue;
import nl.knaw.dans.common.wicket.components.search.facets.FacetValueCollapser;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecursiveListValueCollapser implements FacetValueCollapser<String>
{

    private static final long serialVersionUID = -2972737430430819895L;
    private static final Logger logger = LoggerFactory.getLogger(RecursiveListValueCollapser.class);

    private final String recursiveListId;
    private boolean showZeroCountFacets;

    public RecursiveListValueCollapser(String recursiveListId, boolean showZeroCountFacets)
    {
        this.recursiveListId = recursiveListId;
        this.showZeroCountFacets = showZeroCountFacets;
    }

    @Override
    public List<CollapsedFacetValue<String>> collapse(List<FacetValue<String>> originalValues, FacetValue<String> selectedValue)
    {
        List<CollapsedFacetValue<String>> collapsedValues = new ArrayList<CollapsedFacetValue<String>>();
        RecursiveList recursiveList;
        try
        {
            recursiveList = Services.getSearchService().getRecursiveList(recursiveListId, null);
        }
        catch (ServiceException e)
        {
            logger.error("Unable to collapse facet values: ", e);
            throw new InternalWebError();
        }

        RecursiveNode recursiveNode;
        if (selectedValue == null)
        {
            recursiveNode = recursiveList;
        }
        else
        {
            recursiveNode = recursiveList.get(selectedValue.getValue());
        }

        for (RecursiveEntry entry : recursiveNode.getChildren())
        {
            CollapsedFacetValue<String> collapsed = new CollapsedFacetValue<String>();
            collapsed.setValue(entry.getKey());
            for (FacetValue<String> facetValue : originalValues)
            {
                RecursiveEntry kid = entry.get(facetValue.getValue());
                if (kid != null)
                {
                    collapsed.addFacetValue(facetValue);
                }
            }
            if (showZeroCountFacets || collapsed.getCount() > 0)
            {
                collapsedValues.add(collapsed);
            }
        }
        return collapsedValues;
    }

}
