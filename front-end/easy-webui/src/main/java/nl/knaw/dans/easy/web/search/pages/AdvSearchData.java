package nl.knaw.dans.easy.web.search.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchQuery;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;

@SuppressWarnings("serial")
public class AdvSearchData implements Serializable {
    private static final long serialVersionUID = -5849253482755111398L;

    public String query = new String();
    public SimpleField<String> title = new SimpleField<String>(DatasetSB.DC_TITLE_FIELD);
    public SimpleField<String> creator = new SimpleField<String>(DatasetSB.DC_CREATOR_FIELD);
    public SimpleField<String> description = new SimpleField<String>(DatasetSB.DC_DESCRIPTION_FIELD);
    public SimpleField<String> subject = new SimpleField<String>(DatasetSB.DC_SUBJECT_FIELD);
    public SimpleField<String> coverage = new SimpleField<String>(DatasetSB.DC_COVERAGE_FIELD);
    public SimpleField<String> identifier = new SimpleField<String>(DatasetSB.DC_IDENTIFIER_FIELD);

    public SimpleField<String> depositor = new SimpleField<String>(EasyDatasetSB.DEPOSITOR_ID_FIELD);
    public SimpleField<String> assignedTo = new SimpleField<String>(EasyDatasetSB.ASSIGNEE_ID_FIELD);

    public boolean scopeMyDatasets;

    public ArrayList<DatasetState> states = new ArrayList<DatasetState>() {
        {
            // default state
            add(DatasetState.PUBLISHED);
        }
    };

    @SuppressWarnings("unchecked")
    public List<SimpleField> fields = new ArrayList<SimpleField>() {
        {
            add(title);
            add(creator);
            add(description);
            add(subject);
            add(coverage);
            add(identifier);
            // add(relation);
            add(depositor);
            add(assignedTo);
        }
    };

    public List<Field<?>> getFields(boolean includeStates) {
        List<Field<?>> filterFields = new ArrayList<Field<?>>(fields.size());
        for (SimpleField field : fields) {
            if (field.getValue() != null)
                filterFields.add(field);
        }
        if (includeStates && states.size() > 0) {
            filterFields.add(new SimpleField<String>(DatasetSB.DS_STATE_FIELD, SimpleSearchQuery.OrValues(states.toArray())));
        }
        return filterFields;
    }
}
