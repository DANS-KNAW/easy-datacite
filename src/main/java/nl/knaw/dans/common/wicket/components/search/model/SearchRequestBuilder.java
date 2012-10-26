package nl.knaw.dans.common.wicket.components.search.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;

import org.apache.commons.lang.StringUtils;

/**
 * The search request builder holds all information on how to create a search
 * request. First and foremost it uses a sequence of SearchCriterium objects to 
 * create a search request. Secondly it has some properties on how to sort and
 * paginate, etc. If any of these properties change this object is set to dirty.
 * This object does not reset its own dirty property, but relies on its users
 * to do so.
 * 
 * @see SearchCriterium
 * 
 * @author lobo
 */
public class SearchRequestBuilder implements Serializable
{
    private static final long serialVersionUID = -7268417198242303154L;

    private List<SearchCriterium> criteria = new ArrayList<SearchCriterium>();

    private List<SortField> sortFields = new ArrayList<SortField>();

    private int offset = 0;

    private int limit = 10;

    private boolean dirty = true;

    private List<FacetConfig> facets;

    private Set<CriteriumListener> criteriumListeners = new HashSet<CriteriumListener>();

    public SearchRequestBuilder()
    {

    }

    public SimpleSearchRequest getRequest()
    {
        SimpleSearchRequest sr = new SimpleSearchRequest();

        // facets
        if (facets != null && facets.size() > 0)
        {
            Set<String> facetFields = new HashSet<String>(facets.size());
            for (FacetConfig facet : facets)
            {
                facetFields.add(facet.getFacetName());
            }
            sr.setFacetFields(facetFields);
        }

        // sorting
        sr.setSortFields(sortFields);

        // paging
        sr.setOffset(offset);
        sr.setLimit(limit);

        // apply criteria
        for (SearchCriterium criterium : criteria)
        {
            criterium.apply(sr);
        }

        // highlighting (this line of code must execute AFTER criteria have been applied)
        sr.setHighlightingEnabled(!StringUtils.isBlank(sr.getQuery().getQueryString()));

        return sr;
    }

    public List<SearchCriterium> getCriteria()
    {
        return criteria;
    }

    public void setCriteria(List<SearchCriterium> criteria)
    {
        dirty = true;
        this.criteria = criteria;
    }

    public List<SortField> getSortFields()
    {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields)
    {
        dirty = true;
        this.sortFields = sortFields;
    }

    public void setFirstSortField(SortField sortField)
    {
        removeSortField(sortField.getName());
        sortFields.add(0, sortField);
        dirty = true;
    }

    public boolean removeSortField(String name)
    {
        SortField foundSortField = getSortField(name);
        boolean removed = sortFields.remove(foundSortField);
        dirty |= removed;
        return removed;
    }

    public SortField getSortField(String name)
    {
        SortField foundSortField = null;
        for (SortField sf : sortFields)
        {
            if (sf.getName().equals(name))
            {
                foundSortField = sf;
                break;
            }
        }
        return foundSortField;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        checkDirty(this.offset, offset);
        this.offset = offset;
    }

    private void checkDirty(Object o1, Object o2)
    {
        if (!o1.equals(o2))
            dirty = true;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        checkDirty(this.limit, limit);
        this.limit = limit;
    }

    public void addCriterium(SearchCriterium criterium)
    {
        if (criterium == null)
            return;

        // don't repeat yourself
        String labelModelObject = criterium.getLabelModel().getObject();
        for (SearchCriterium crit : criteria)
        {
            if (crit.getLabelModel().getObject().equals(labelModelObject))
            {
                return;
            }
        }

        setOffset(0);
        onCriteriumAdded(criterium);

        criteria.add(criterium);
        dirty = true;
    }

    public void removeCriterium(SearchCriterium criterium)
    {
        setOffset(0);
        onCriteriumRemoved(criterium);

        if (criteria.remove(criterium))
            dirty = true;
    }

    public void removeCriterium(int idx)
    {
        setOffset(0);

        onCriteriumRemoved(criteria.remove(idx));
        dirty = true;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public void setDirty(boolean b)
    {
        dirty = b;
    }

    public void setFacets(final List<FacetConfig> facets)
    {
        this.facets = facets;
        for (CriteriumListener listener : criteriumListeners)
        {
            listener.addFacets(this.facets, this);
        }
    }

    public List<FacetConfig> getFacets()
    {
        return facets;
    }

    public void addCriteriumListener(CriteriumListener criteriumListener)
    {
        criteriumListeners.add(criteriumListener);
    }

    public boolean removeCriteriumListener(CriteriumListener criteriumListener)
    {
        return criteriumListeners.remove(criteriumListener);
    }

    public Set<CriteriumListener> getCriteriumListeners()
    {
        return Collections.unmodifiableSet(criteriumListeners);
    }

    private void onCriteriumAdded(SearchCriterium searchCriterium)
    {
        for (CriteriumListener listener : criteriumListeners)
        {
            listener.onCriteriumAdded(searchCriterium, this);
        }
    }

    private void onCriteriumRemoved(SearchCriterium searchCriterium)
    {
        for (CriteriumListener listener : criteriumListeners)
        {
            listener.onCriteriumRemoved(searchCriterium, this);
        }
    }

}
