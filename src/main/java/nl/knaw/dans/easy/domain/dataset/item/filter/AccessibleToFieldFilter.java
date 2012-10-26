package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.Set;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.NoFilterValuesSelectedException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;

public class AccessibleToFieldFilter extends AbstractItemFieldFilter<AccessibleTo>
{
    public AccessibleToFieldFilter(AccessibleTo... desiredValues)
    {
        addDesiredValues(desiredValues);
    }

    public ItemFilterField getFilterField()
    {
        return ItemFilterField.ACCESSBLETO;
    }

    /*
    public boolean filterOut(ItemVO item) throws DomainException
    {
        final Set<AccessibleTo> filterValues = getDesiredValues();
        if (filterValues.size() == 0)
            throw new NoFilterValuesSelectedException();

        if (item instanceof FileItemVO)
        {
            final FileItemVO fileItem = (FileItemVO) item;
            return !filterValues.contains(fileItem.getAccessibleTo());
        }
        // TODO
    //        else
    //        {
    //            final FolderItemVO folderItem = (FolderItemVO) item;
    //            for (final FolderItemAccessibleTo AccessibleTo : folderItem.getAccessibleToList())
    //            {
    //                if (filterValues.contains(AccessibleTo.getAccessibleTo()))
    //                    return false;
    //            }
    //        }
        return true;

    } */

    public boolean filterOut(ItemVO item) throws DomainException
    {
        final Set<AccessibleTo> filterValues = getDesiredValues();
        if (filterValues.size() == 0)
            throw new NoFilterValuesSelectedException();

        if (item instanceof FileItemVO)
        {
            final FileItemVO fileItem = (FileItemVO) item;
            return !filterValues.contains(fileItem.getAccessibleTo());
        }
        else
        {
            final FolderItemVO folderItem = (FolderItemVO) item;
            for (final FolderItemAccessibleTo accessibleTo : folderItem.getAccessibleToList())
            {
                if (filterValues.contains(accessibleTo.getAccessibleTo()))
                    return false;
            }
        }
        return true;

    }

}
