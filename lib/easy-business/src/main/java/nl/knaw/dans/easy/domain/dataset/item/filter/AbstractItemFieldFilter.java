package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;

public abstract class AbstractItemFieldFilter<FIELD extends FileItemVOAttribute> implements ItemFieldFilter<FIELD> {
    protected Set<FIELD> desiredValues = new HashSet<FIELD>();

    public AbstractItemFieldFilter() {}

    public void addDesiredValues(FIELD... values) {
        desiredValues.addAll(Arrays.asList(values));
    }

    public Set<FIELD> getDesiredValues() {
        return this.desiredValues;
    }

    abstract FIELD getFieldValue(final FileItemVO item);

    @Override
    public String toString() {
        String result = this.getClass().getName() + " on field " + this.getFilterField().filePropertyName;
        Iterator<FIELD> i = desiredValues.iterator();
        result += "desiredValues = {";
        while (i.hasNext()) {
            result += i.next().toString();
            if (i.hasNext())
                result += ", ";
        }
        result += "}";
        return result;
    }
}
