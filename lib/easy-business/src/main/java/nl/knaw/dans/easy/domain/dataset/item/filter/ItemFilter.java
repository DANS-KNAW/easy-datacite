package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

/**
 * A filter object that can filter out file or folder items.
 * 
 * @author lobo
 */
public interface ItemFilter {

    /**
     * Applies filtering on a list of items. Items are filtered out on the itemList parameter.
     * 
     * @param itemList
     *        input list
     * @return filtered list
     */
    List<? extends ItemVO> apply(final List<? extends ItemVO> itemList) throws DomainException;
}
