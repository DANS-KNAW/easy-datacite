package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.Set;

import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

/**
 * A filter object that filters out file or folder items based on a field (property) of that file or
 * folder item.
 * 
 * @author lobo
 * @param <T>
 *        the type of the filter values
 */
public interface ItemFieldFilter<FIELD> extends ItemFilter
{

    /**
     * @return The field on which filtering is to be applied
     */
    ItemFilterField getFilterField();

    /**
     * Adds a filter value to the list of filter values. The values in the filter list are the values
     * that are kept. If an item does not have this value it is filtered out.
     * 
     * @param desiredValues
     *        the values that need to be filtered upon
     */
    void addDesiredValues(FIELD... values);

    /**
     * @return The values that need to be filtered upon
     */
    Set<FIELD> getDesiredValues();

    /**
     * Tells if this item needs to be filtered out (removed from the list). This method cannot be placed
     * on the ItemFilter interface, since it might be possible to write an ItemFilter that filters out
     * items based on information of more than one item.
     * 
     * @param item
     *        the item to be checked for filtering
     * @return true if the objects need to be removed from the list
     */
    boolean filterOut(ItemVO item) throws DomainException;
}
