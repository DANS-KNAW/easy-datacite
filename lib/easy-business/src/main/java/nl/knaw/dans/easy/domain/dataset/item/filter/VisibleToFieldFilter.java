package nl.knaw.dans.easy.domain.dataset.item.filter;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.VisibleTo;

public class VisibleToFieldFilter extends AbstractItemFieldFilter<VisibleTo> {
    public VisibleToFieldFilter(VisibleTo... desiredValues) {
        addDesiredValues(desiredValues);
    }

    public ItemFilterField getFilterField() {
        return ItemFilterField.VISIBLETO;
    }

    @Override
    VisibleTo getFieldValue(FileItemVO item) {
        return item.getVisibleTo();
    }
}
