package nl.knaw.dans.common.wicket.components.search.model;

import java.io.Serializable;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;

import org.apache.wicket.model.IModel;

/**
 * A search criterium is an object that can apply changes to a search request.
 * Search criteria are the primary way of updating the search request. 
 * 
 * The search criteria are made displayable by the SearchCriteriaPanel.
 * @see nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel
 *
 * @author lobo
 */
public interface SearchCriterium extends Serializable
{
	/**
	 * @return a string model that is used by the CriteriaPanel for displaying
	 * a link or label text belonging to this criterium.
	 */
	IModel<String> getLabelModel();
	
	/**
	 * Applies this criterium to a searchRequest 
	 * @param searchRequest
	 */
	void apply(SimpleSearchRequest searchRequest);
}
