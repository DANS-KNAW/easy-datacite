package nl.knaw.dans.easy.domain.dataset.item.filter;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class CreatorRoleFieldFilter extends AbstractItemFieldFilter<CreatorRole> {
    public CreatorRoleFieldFilter(CreatorRole... desiredValues) {
        addDesiredValues(desiredValues);
    }

    public ItemFilterField getFilterField() {
        return ItemFilterField.CREATORROLE;
    }

    @Override
    CreatorRole getFieldValue(FileItemVO item) {
        return item.getCreatorRole();
    }
}
