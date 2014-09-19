package nl.knaw.dans.common.wicket.components.search.criteria;

import java.util.List;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;

import org.apache.wicket.model.IModel;

/**
 * A search criterium that adds a filter query on one or more fields. This comes in handy for an advanced search. 26-7-2010: Unfortantely Solr does not support
 * hit highlighting for filter queries, but only for field queries. But field queries are not allowed for the Dismax handler. A highly recommended handler for
 * user based searches. Apparently Solr 1.5 will have a great solution for that, but by that time I am already in Thailand drinking from a coconut.
 * 
 * @author lobo
 */
public class MultiFilterCriterium extends AbstractSearchCriterium {
    private static final long serialVersionUID = -4451822971051448512L;

    private final List<Field<?>> fields;

    private IModel<String> prefixLabelModel;

    public MultiFilterCriterium(List<Field<?>> fields, IModel<String> labelModel) {
        super(labelModel);
        this.fields = fields;
    }

    @Override
    public void apply(SimpleSearchRequest sr) {
        for (Field<?> field : fields)
            sr.addFilterQuery(field);
    }

    public List<Field<?>> getFields() {
        return fields;
    }

}
