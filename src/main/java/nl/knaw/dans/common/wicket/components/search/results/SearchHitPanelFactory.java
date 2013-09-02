package nl.knaw.dans.common.wicket.components.search.results;

import java.io.Serializable;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * The search hit panel factory is responsible for creating the hit panels that display one search hit.
 * By implementing this factory one may choose to use different panels for different search hit objects.
 * 
 * @author lobo
 */
public interface SearchHitPanelFactory extends Serializable
{
    Panel createHitPanel(String wicketId, SearchHit<?> searchHit, SearchModel sModel);
}
