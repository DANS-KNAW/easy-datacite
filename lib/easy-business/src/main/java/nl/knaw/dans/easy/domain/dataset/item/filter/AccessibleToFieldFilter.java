package nl.knaw.dans.easy.domain.dataset.item.filter;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;

public class AccessibleToFieldFilter extends AbstractItemFieldFilter<AccessibleTo> {
    public AccessibleToFieldFilter(AccessibleTo... desiredValues) {
        addDesiredValues(desiredValues);
    }

    public ItemFilterField getFilterField() {
        return ItemFilterField.ACCESSBLETO;
    }

    @Override
    AccessibleTo getFieldValue(FileItemVO item) {
        return item.getAccessibleTo();
    }
}
