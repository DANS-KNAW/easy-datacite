package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.Set;

import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;

/**
 * A filter object that filters out file or folder items based on a field (property) of that file or folder item.
 * 
 * @author lobo
 * @param <T>
 *        the type of the filter values
 */
public interface ItemFieldFilter<FIELD extends FileItemVOAttribute> extends ItemFilter {

    /**
     * @return The field on which filtering is to be applied
     */
    ItemFilterField getFilterField();

    /**
     * Adds a filter value to the list of filter values. The values in the filter list are the values that are kept. If an item does not have this value it is
     * filtered out.
     * 
     * @param desiredValues
     *        the values that need to be filtered upon
     */
    void addDesiredValues(FIELD... values);

    /**
     * @return The values that need to be filtered upon
     */
    Set<FIELD> getDesiredValues();
}
