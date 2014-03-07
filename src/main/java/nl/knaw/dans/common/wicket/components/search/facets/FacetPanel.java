package nl.knaw.dans.common.wicket.components.search.facets;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.search.FacetField;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.exceptions.FieldNotFoundException;
import nl.knaw.dans.common.wicket.components.search.BaseSearchPanel;
import nl.knaw.dans.common.wicket.components.search.Translator;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.FacetCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig.Order;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultPanel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shows a single facet. Configure the facet panel with the FacetConfig object. The facet panel works
 * directly with the SearchModel. It uses the search model for reading the facet results and updates the
 * search model's request builder when a facet is clicked with a FacetCriterium. The facet panel is meant
 * to be used by another component, because the facet panel does not execute a search when the search
 * model gets dirty. The onFacetClick method can be component by that component to catch facet click
 * events.
 * 
 * @see FacetConfig
 * @see SearchModel
 * @see FacetCriterium
 * @author lobo
 */
public class FacetPanel extends BaseSearchPanel
{
    private static final long serialVersionUID = -8118364638125839710L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultPanel.class);

    /**
     * Contains the configuration for this panel.
     */
    private final FacetConfig config;

    public FacetPanel(String wicketId, SearchModel model, FacetConfig config)
    {
        super(wicketId, model);
        this.config = config;
        init();
    }

    public FacetConfig getConfig()
    {
        return config;
    }

    private String getFacetName()
    {
        return getConfig().getFacetName();
    }

    private FacetField getFacetField()
    {
        try
        {
            return getSearchResult().getFacetByName(getFacetName());
        }
        catch (FieldNotFoundException e)
        {
            String msg = fatalMessage(ERROR_FACET_NOT_FOUND, getFacetName());
            LOGGER.error(msg, e);
            throw new InternalWebError();
        }
    }

    private void init()
    {
        if (!isVisible())
        {
            hide("facetName");
            hide("facetValuesUL");
            hide("facetParentValue");
            return;
        }

        // get the facet values from the search results
        List<FacetValue<?>> facetValues = getFacetField().getValue();

        FacetValueCollapser facetCollapser = getConfig().getFacetValueCollapser();
        FacetCriterium parentFacet = null;
        if (facetCollapser != null)
        {
            // collapse values
            parentFacet = FacetPanel.getPreviousSelection(getConfig(), getRequestBuilder());
            facetValues = facetCollapser.collapse(facetValues, parentFacet != null ? parentFacet.getFacetValue() : null);
        }

        // prune, sort and limit the facet values
        pruneEmptyValues(facetValues);
        sort(facetValues);
        facetValues = limit(facetValues);

        // parent facet
        if (parentFacet != null && getConfig().showParentFacet())
        {
            add(new Label("facetParentValue", getFacetValueModel(parentFacet.getFacetValue(), true)));
        }
        else
        {
            hide("facetParentValue");
        }

        // name label
        add(new Label("facetName", getFacetNameModel(getFacetName())));

        // facet values UL
        // set column count with a css class (see FacetPanel.css)
        WebMarkupContainer facetValueUl = new WebMarkupContainer("facetValuesUL");
        add(CSSPackageResource.getHeaderContribution(new ResourceReference(FacetPanel.class, "FacetPanel.css")));
        int colCount = getConfig().getColumnCount();
        if (colCount > 1)
        {
            facetValueUl.add(new AttributeAppender("class", new Model("fvCol" + colCount), " "));
        }
        add(facetValueUl);

        // facet values LI
        ListView<FacetValue<?>> facetValueList = new ListView<FacetValue<?>>("facetValuesLI", facetValues)
        {
            private static final long serialVersionUID = -711250532805369414L;

            @Override
            protected void populateItem(ListItem<FacetValue<?>> item)
            {
                final FacetValue<?> facetValue = item.getModelObject();

                // facet link
                Link facetLink = new Link("facetValueLink")
                {
                    private static final long serialVersionUID = 1356587833L;

                    {
                        // value label
                        add(new Label("facetValue", getFacetValueModel(facetValue, false)));
                        // count label
                        add(new Label("facetCount", String.valueOf(facetValue.getCount())));
                    }

                    public void onClick()
                    {
                        getRequestBuilder().addCriterium(new FacetCriterium(getFacetName(), facetValue, new AbstractReadOnlyModel<String>()
                        {
                            private static final long serialVersionUID = 13434324L;

                            @Override
                            public String getObject()
                            {
                                return CriteriumLabel.createFilterText(getFacetNameDisplay(), getFacetValueDisplay(facetValue));
                            }
                        }));

                        onFacetClick(facetValue);
                    }
                };
                item.add(facetLink);
            }
        };
        facetValueUl.add(facetValueList);

    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();
    }

    private void pruneEmptyValues(List<FacetValue<?>> facetValues)
    {
        Iterator<FacetValue<?>> it = facetValues.iterator();
        while (it.hasNext())
        {
            FacetValue<?> facetValue = it.next();
            if (getConfig().hideEmptyFacets() && facetValue.getCount() == 0)
                it.remove();
        }
    }

    private List<FacetValue<?>> limit(List<FacetValue<?>> facetValues)
    {
        if (getConfig().getLimit() > 0)
            return facetValues.subList(0, Math.min(getConfig().getLimit(), facetValues.size()));
        else
            return facetValues;
    }

    private void sort(List<FacetValue<?>> facetValues)
    {
        if (getConfig().getOrder().equals(Order.NONE))
        {
            return;
        }
        else if (getConfig().getOrder().equals(Order.BY_ALPHABET))
        {
            Collections.sort(facetValues, new Comparator<FacetValue<?>>()
            {
                @Override
                public int compare(FacetValue<?> o1, FacetValue<?> o2)
                {
                    IModel<String> v1 = getFacetValueModel(o1, false);
                    IModel<String> v2 = getFacetValueModel(o2, false);
                    return v1.getObject().compareTo(v2.getObject());
                }
            });
        }
        else if (getConfig().getOrder().equals(Order.BY_COUNT))
        {
            Collections.sort(facetValues, new Comparator<FacetValue<?>>()
            {
                @Override
                public int compare(FacetValue<?> o1, FacetValue<?> o2)
                {
                    Integer c1 = new Integer(o1.getCount());
                    Integer c2 = new Integer(o2.getCount());
                    return c2.compareTo(c1);
                }
            });
        }
        else if (getConfig().getOrder().equals(Order.BY_VALUE))
        {
            Collections.sort(facetValues, new Comparator<FacetValue<?>>()
            {
                @Override
                public int compare(FacetValue<?> o1, FacetValue<?> o2)
                {
                    Comparable c1 = (Comparable) o1.getValue();
                    Comparable c2 = (Comparable) o2.getValue();
                    return c1.compareTo(c2);
                }
            });
        }
        else if (getConfig().getOrder().equals(Order.CUSTOM))
        {
            Collections.sort(facetValues, getConfig().getCustomOrderComparator());
        }
    }

    private IModel<String> getFacetNameModel(String facetName)
    {
        IModel<String> facetNameModel;
        Translator<String> facetNameTranslator = getConfig().getFacetNameTranslator();
        if (facetNameTranslator != null)
            facetNameModel = facetNameTranslator.getTranslation(facetName, getLocale(), false);
        else
            facetNameModel = new Model<String>(facetName);
        return facetNameModel;
    }

    private String getFacetNameDisplay()
    {
        return getFacetNameModel(getFacetName()).getObject();
    }

    private IModel<String> getFacetValueModel(final FacetValue<?> facetValue, boolean fullName)
    {
        IModel<String> facetValueModel;
        String sfv = facetValue.getValue().toString();
        Translator<String> translator = getConfig().getFacetValueTranslator();
        if (translator == null)
            facetValueModel = new Model<String>(sfv);
        else
            facetValueModel = translator.getTranslation(sfv, getLocale(), fullName);
        return facetValueModel;
    }

    private String getFacetValueDisplay(final FacetValue<?> facetValue)
    {
        return getFacetValueModel(facetValue, true).getObject();
    }

    protected void onFacetClick(final FacetValue<?> facetValue)
    {
    }

    private boolean isFacetAvailable()
    {
        try
        {
            return getSearchResult().getFacetByName(getFacetName()) != null;
        }
        catch (FieldNotFoundException e)
        {
            return false;
        }
    }

    public static boolean isVisible(FacetConfig config, SearchModel searchModel)
    {
        FacetField facetField = null;
        try
        {
            facetField = searchModel.getObject().getResult().getFacetByName(config.getFacetName());
        }
        catch (FieldNotFoundException e)
        {
            return false;
        }
        if (facetField == null)
            return false;

        // check if this facet was already selected and if the
        // previous selection was not by any chance part of a
        // collapsed facet
        FacetCriterium previousSelection = getPreviousSelection(config, searchModel.getObject().getRequestBuilder());
        if (previousSelection != null)
        {
            FacetValue oldFacetValue = previousSelection.getFacetValue();
            if (oldFacetValue instanceof CollapsedFacetValue)
            {
                // if it was selected, but was a collapsed facet value then it might
                // contain more values that can be selected.
                CollapsedFacetValue<?> cOldValue = (CollapsedFacetValue<?>) oldFacetValue;
                if (cOldValue.getCount() <= 1 || cOldValue.getCollapsedValues().size() <= 1)
                {
                    return false;
                }
                else
                {
                    return hasMultipleNonZeroCounts(cOldValue.getCollapsedValues());
                }
            }
            else
            {
                // the facet has been selected and was not part of a hierarchy
                return false;
            }
        }

        // if the facet field is available and not already selected and contains multiple facets
        // with one or more values then it can be shown
        if (facetField.getValue().size() > 0)
        {
            return hasMultipleNonZeroCounts(facetField.getValue());
        }
        else
            return false;
    }

    private static boolean hasMultipleNonZeroCounts(List facetValues)
    {
        int facetOneOrMoreCount = 0;
        for (FacetValue<?> facetValue : (List<FacetValue<?>>) facetValues)
        {
            if (facetValue.getCount() > 0)
            {
                facetOneOrMoreCount++;
            }
        }
        return facetOneOrMoreCount > 0;
    }

    private static FacetCriterium getPreviousSelection(FacetConfig config, SearchRequestBuilder requestBuilder)
    {
        List<SearchCriterium> criteria = requestBuilder.getCriteria();
        for (int i = criteria.size() - 1; i >= 0; i--)
        {
            SearchCriterium criterium = criteria.get(i);
            if (criterium instanceof FacetCriterium)
            {
                FacetCriterium fc = (FacetCriterium) criterium;
                if (fc.getFacetName().equals(config.getFacetName()))
                    return fc;
            }
        }
        return null;
    }

    @Override
    public boolean isVisible()
    {
        return FacetPanel.isVisible(getConfig(), getSearchModel());
    }

}
