package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.Set;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class CreatorRoleFieldFilter extends AbstractItemFieldFilter<CreatorRole>
{
    public CreatorRoleFieldFilter(CreatorRole... desiredValues)
    {
        addDesiredValues(desiredValues);
    }

    public ItemFilterField getFilterField()
    {
        return ItemFilterField.CREATORROLE;
    }

    public boolean filterOut(final ItemVO item)
    {
        final Set<CreatorRole> filterValues = getDesiredValues();
        if (item instanceof FileItemVO)
        {
            final FileItemVO fileItem = (FileItemVO) item;
            return !filterValues.contains(fileItem.getCreatorRole());
        }
        else
        {
            final FolderItemVO folderItem = (FolderItemVO) item;
            for (final FolderItemCreatorRole creatorRole : (folderItem).getCreatorRoles())
            {
                if (filterValues.contains(creatorRole.getCreatorRole()))
                    return false;
            }
        }
        return true;
    }

}
