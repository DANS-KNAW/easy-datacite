package nl.knaw.dans.common.wicket.components.search.criteria;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A label used for a criteria
 * 
 * @author lobo
 */
public class CriteriumLabel extends Label
{
    public static final String FILTER_SEPARATOR = ": ";

    private static final long serialVersionUID = -690179496317797759L;

    public CriteriumLabel(String id, IModel<String> model)
    {
        super(id, model);
    }

    /**
     * Creates a label text using a standard layout
     * @param filter what are we filtering for?
     * @param filterBy what are we filtering on?
     * @return a label text using a standard layout
     */
    public static String createFilterText(String filter, String filterBy)
    {
        return filter + FILTER_SEPARATOR + filterBy;
    }

}
