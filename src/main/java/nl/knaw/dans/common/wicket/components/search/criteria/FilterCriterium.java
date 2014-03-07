package nl.knaw.dans.common.wicket.components.search.criteria;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;

import org.apache.wicket.model.IModel;

public class FilterCriterium extends AbstractSearchCriterium
{

    private static final long serialVersionUID = 1795048166011045297L;

    private final Field<?> field;

    public FilterCriterium(Field<?> field, IModel<String> labelModel)
    {
        super(labelModel);
        this.field = field;
    }

    @Override
    public void apply(SimpleSearchRequest searchRequest)
    {
        searchRequest.addFilterQuery(field);
    }

    public Field<?> getField()
    {
        return field;
    }

}
