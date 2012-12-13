package nl.knaw.dans.pf.language.emd;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

/**
 * Container for a category of resource properties.
 *
 * @author ecco
 */
public interface EmdContainer extends EmdBean, Serializable
{

    /**
     * Get a list of terms this container contains.
     *
     * @return the list of terms
     */
    List<Term> getTerms();

    /**
     * Get the list of the specified term. This method checks all contained lists against the name and the name space of
     * the given term and returns the list, if found. If a term with no name space is given, the method checks all
     * contained lists against the name, indiscriminate of name space, and returns an aggregated list of all the lists
     * found.
     *
     * @param term
     *        specifies which list to get
     * @return list of the specified term
     * @throws NoSuchTermException
     *         if the given term is not contained in this container
     */
    List<MetadataItem> get(Term term) throws NoSuchTermException;

    /**
     * Get the list of items of the given term name. The method checks all contained lists against the name,
     * indiscriminate of name space, and returns an aggregated list of all the lists found.
     *
     * @param termName
     *        the term name to get the list of
     * @return list of the specified term name, could be an aggregate
     * @throws NoSuchTermException
     *         if the given term is not contained in this container
     */
    List<MetadataItem> get(Term.Name termName) throws NoSuchTermException;

    /**
     * Get the values of this EmdContainer as a list of strings.
     *
     * @return a list of strings
     */
    List<String> getValues();

    /**
     * Tells if this container has items or not. This method complies to the general contract of {@link List#isEmpty()},
     * meaning that if there are any null-items contained in this container it is not empty.
     *
     * @return <code>true</code> if this container is empty, <code>false</code> otherwise
     */
    boolean isEmpty();

    /**
     * Returns the number of items in this EmdContainer. Null-items are counted.
     * @return the number of items
     */
    int size();

    /**
     * Get a string-representation of this container. Items are separated with the
     * {@link EasyMetadata#DEFAULT_ITEM_SEPARATOR}.
     *
     * @return a string-representation of this container
     */
    String toString();

    /**
     * Get a string-representation of this container, consisting of the items of this container separated with
     * <code>separator</code>.
     *
     * @param separator
     *        the string used to separate the items of this container
     * @return a string-representation of this container
     */
    String toString(String separator);

    /**
     * This method has two forms:
     * <ul>
     * <li> If <code>includeTerm is false</code>: Get a string-representation of this container, consisting of the
     * items of this container separated with <code>separator</code>. </li>
     * <li> If <code>includeTerm is true</code>: Get a string-representation of this container, consisting of name,
     * name space and the items of this container separated with <code>separator</code>. Each term is separated with
     * the {@link EasyMetadata#DEFAULT_LINE_SEPERATOR} . </li>
     * </ul>
     *
     * @param separator
     *        the separator to use
     * @param includeTerm
     *        <code>true</code> if name and name space of the term should be included, <code>false</code> otherwise
     * @return a string-representation of this container
     */
    String toString(String separator, boolean includeTerm);

    /**
     * Get a string-representation of the items of the given term, separated by separator.
     *
     * @param separator
     *        the separator to use
     * @param term
     *        the wanted term
     * @return string-representation of the item values
     * @throws NoSuchTermException
     *         if the term is not in this container
     */
    String toString(String separator, Term term) throws NoSuchTermException;

    /**
     * Get a string-representation of the items of the given term name, separated by separator.
     *
     * @param separator
     *        the separator to use
     * @param termName
     *        the wanted term
     * @return string-representation of the item values
     * @throws NoSuchTermException
     *         if the term is not in this container
     */
    String toString(String separator, Term.Name termName) throws NoSuchTermException;

}
