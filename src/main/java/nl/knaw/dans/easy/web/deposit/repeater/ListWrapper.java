package nl.knaw.dans.easy.web.deposit.repeater;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * ListWrapper is a converter for Lists. A ListWrapper provides methods to obtain and handle instances of
 * type T.
 * 
 * @author ecco Mar 31, 2009
 * @param <T>
 *        the type handled by this ListWrapper
 */
public interface ListWrapper<T extends Object> extends Serializable
{
    /**
     * Convert the given list with type T instances to the wrapped list.
     * 
     * @param listItems
     *        a list of type T instances
     */
    void setComponent(Component component);

    Component getComponent();

    /**
     * Convert the wrapped list to a list of type T instances.
     * 
     * @return a list of type T instances
     */
    List<T> getInitialItems();

    List<T> getInitialEditableItems();

    /**
     * Convert the given list with type T instances to the wrapped list.
     * 
     * @param listItems
     *        a list of type T instances
     * @return the amount of errors while synchronizing
     */
    int synchronize(List<T> listItems);

    /**
     * Get an instance of type T that is a null or empty T.
     * 
     * @return an instance of type T
     */
    T getEmptyValue();

    /**
     * Get a {@link ChoiceRenderer} suited for the type T.
     * 
     * @return a ChoiceRenderer suited for type T
     */
    ChoiceRenderer getChoiceRenderer();

    /**
     * The size of the wrapped list.
     * 
     * @return size of the wrapped list
     */
    int size();

}
