package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.*;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.NoFilterValuesSelectedException;
import nl.knaw.dans.easy.domain.model.VisibleTo;

public class VisibleToFieldFilter extends AbstractItemFieldFilter<VisibleTo>
{
    public VisibleToFieldFilter (VisibleTo... desiredValues) {
        addDesiredValues(desiredValues);
    }


    public ItemFilterField getFilterField()
    {
        return ItemFilterField.VISIBLETO;
    }

    public boolean filterOut(ItemVO item) throws DomainException
    {
        final Set<VisibleTo> filterValues = getDesiredValues();
        if (filterValues.size() == 0)
        	throw new NoFilterValuesSelectedException();

        if (item instanceof FileItemVO)
        {
            final FileItemVO fileItem = (FileItemVO) item;
            return !filterValues.contains(fileItem.getVisibleTo());
        }
        else
        {
            final FolderItemVO folderItem = (FolderItemVO) item;
            for (final FolderItemVisibleTo visibleTo : folderItem.getVisibleToList())
            {
                if (filterValues.contains(visibleTo.getVisibleTo()))
                    return false;
            }
        }
        return true;

    }

}
