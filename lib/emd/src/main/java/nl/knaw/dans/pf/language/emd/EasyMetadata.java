package nl.knaw.dans.pf.language.emd;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.TimestampedObject;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

/**
 * Meta data on collections in the Easy repository.
 * 
 * @author ecco
 */
public interface EasyMetadata extends TimestampedObject, EmdBean, Serializable {

    String UNIT_ID = "EMD";

    String UNIT_LABEL = "Descriptive metadata for this dataset";

    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/easymetadata/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    /**
     * The default separator used when listing items in a container.
     */
    String DEFAULT_ITEM_SEPARATOR = ";";

    /**
     * The default line separator used when listing containers.
     */
    String DEFAULT_LINE_SEPERATOR = "\n";

    /**
     * Get the version of this EasyMetadata.
     * 
     * @return the version
     */
    String getVersion();

    /**
     * Gets a (unmodifiable) map that contains a list of {@link Term}s in EasyMetadata mapped to their {@link MDContainer}. The contents of the map may vary
     * with the version of the underlying implementation.
     * 
     * @return a mapping of terms to containers
     */
    Map<Term, MDContainer> getTermsMap();

    /**
     * Get the (unmodifiable) set of {@link Term}s in EasyMetadata. The set of terms may vary with the version of the underlying implementation. Same as
     * {@link #getTermsMap()}.keySet().
     * 
     * @return the set of terms in use
     */
    Set<Term> getTerms();

    /**
     * Is this EasyMetadata empty. This method complies to the general contract of {@link List#isEmpty()} , meaning that if there are any null-items contained
     * in this EasyMetadata it is not empty.
     * 
     * @return <code>true</code> if it contains term-values, <code>false</code> otherwise
     */
    boolean isEmpty();

    /**
     * Visit the children ({@link EmdContainer}s) of this EasyMetadata.
     * 
     * @param includeEmpty
     *        include empty containers in the visit
     * @param visitor
     *        visitor to receive containers
     * @return object from last visit
     */
    Object visitChildren(boolean includeEmpty, EmdVisitor visitor);

    /**
     * Get a string-representation of this EasyMetadata. Terms are separated with a {@link #DEFAULT_LINE_SEPERATOR}. Term-items are separated with the given
     * <code>separator</code>. Each term is preceded by it's name and name space, also separated with the given <code>separator</code>.
     * <p/>
     * Example (the separator was ';'):
     * 
     * <pre>
     * title;http://purl.org/dc/elements/1.1/;The Foo and the Bar;De Foo en de Bar
     * alternative;http://purl.org/dc/terms/;Foo Bar behavior;Gedrag van Foo in Bar
     * creator;http://purl.org/dc/elements/1.1/;Jan Klaasen
     * creator;http://easy.dans.knaw.nl/easy/easymetadata/eas;Fields, DC;prof. dr. Foo, ABCD de la
     * subject;http://purl.org/dc/elements/1.1/;subject foo;subject bar
     * </pre>
     * 
     * @param separator
     *        term-item separator
     * @return a string-representation of the meta data
     */
    String toString(String separator);

    /**
     * Get a string-representation of the EmdContainer corresponding to the given MDContainer.
     * 
     * @param separator
     *        the separator to use between items of the container
     * @param mdContainer
     *        MDContainer pointing to wanted EmdContainer
     * @return a list of item values, separated by <code>separator</code>
     */
    String toString(String separator, MDContainer mdContainer);

    /**
     * Get a string-representation of the list corresponding to the given term.
     * 
     * @param separator
     *        the separator to use between items
     * @param term
     *        the wanted term, can be qualified (with name space) or unqualified
     * @return a list of item values, separated by <code>separator</code>
     * @throws NoSuchTermException
     *         if the term does not exist
     */
    String toString(String separator, Term term) throws NoSuchTermException;

    /**
     * Get a string-representation of the list corresponding to the given term name.
     * 
     * @param separator
     *        the separator to use between items
     * @param termName
     *        name of the wanted term
     * @return a list of item values, separated by <code>separator</code>
     * @throws NoSuchTermException
     *         if the term does not exist
     */
    String toString(String separator, Term.Name termName) throws NoSuchTermException;

    String getPreferredTitle();

    /**
     * Get the EmdContainer corresponding to the given mdField, or <code>null</code>.
     * 
     * @param mdContainer
     *        the MDContainer wanted
     * @param returnNull
     *        should we return null if the container wasn't instantiated (<code>true</code>), or never return null ( <code>false</code>)
     * @return EmdContainer corresponding to the given mdField, or <code>null</code>
     */
    EmdContainer getContainer(MDContainer mdContainer, boolean returnNull);

    /**
     * Get the list of items for the given term. This method will create the container for the term -if it didn't exist- and return the list of items for the
     * given term.
     * 
     * @param term
     *        the term wanted
     * @return the list of items for the given term
     * @throws NoSuchTermException
     *         if a term is not provided by this EasyMetadata
     */
    List<MetadataItem> getTerm(Term term) throws NoSuchTermException;

    /**
     * Get the Dublin Core metadata representation of this EasyMetadata.
     * 
     * @return Dublin Core metadata
     */
    DublinCoreMetadata getDublinCoreMetadata();

    /**
     * Get the EmdTitle container, never <code>null</code>.
     * 
     * @return the EmdTitle container, never <code>null</code>
     */
    EmdTitle getEmdTitle();

    /**
     * Get the EmdCreator container, never <code>null</code>.
     * 
     * @return the EmdCreator container, never <code>null</code>
     */
    EmdCreator getEmdCreator();

    /**
     * Get the EmdSubject container, never <code>null</code>.
     * 
     * @return the EmdSubject container, never <code>null</code>
     */
    EmdSubject getEmdSubject();

    /**
     * Get the EmdDescription container, never <code>null</code>.
     * 
     * @return the EmdDescription container, never <code>null</code>
     */
    EmdDescription getEmdDescription();

    /**
     * Get the EmdPublisher container, never <code>null</code>.
     * 
     * @return the EmdPublisher container, never <code>null</code>
     */
    EmdPublisher getEmdPublisher();

    /**
     * Get the EmdContributorcontainer, never <code>null</code>.
     * 
     * @return the EmdContributor container, never <code>null</code>
     */
    EmdContributor getEmdContributor();

    /**
     * Get the EmdDate container, never <code>null</code>.
     * 
     * @return the EmdDate container, never <code>null</code>
     */
    EmdDate getEmdDate();

    /**
     * Get the EmdType container, never <code>null</code>.
     * 
     * @return the EmdType container, never <code>null</code>
     */
    EmdType getEmdType();

    /**
     * Get the EmdFormat container, never <code>null</code>.
     * 
     * @return the EmdFormat container, never <code>null</code>
     */
    EmdFormat getEmdFormat();

    /**
     * Get the EmdIdentifier container, never <code>null</code>.
     * 
     * @return the EmdIdentifier container, never <code>null</code>
     */
    EmdIdentifier getEmdIdentifier();

    /**
     * Get the EmdSource container, never <code>null</code>.
     * 
     * @return the EmdSource container, never <code>null</code>
     */
    EmdSource getEmdSource();

    /**
     * Get the EmdLanguage container, never <code>null</code>.
     * 
     * @return the EmdLanguage container, never <code>null</code>
     */
    EmdLanguage getEmdLanguage();

    /**
     * Get the EmdRelation container, never <code>null</code>.
     * 
     * @return the EmdRelation container, never <code>null</code>
     */
    EmdRelation getEmdRelation();

    /**
     * Get the EmdCoverage container, never <code>null</code>.
     * 
     * @return the EmdCoverage container, never <code>null</code>
     */
    EmdCoverage getEmdCoverage();

    /**
     * Get the EmdRights container, never <code>null</code>.
     * 
     * @return the EmdRights container, never <code>null</code>
     */
    EmdRights getEmdRights();

    /**
     * Get the EmdAudience container, never <code>null</code>.
     * 
     * @return the EmdAudience container, never <code>null</code>
     */
    EmdAudience getEmdAudience();

    /**
     * Get the EmdOther container, never <code>null</code>.
     * 
     * @return the EmdOther container, never <code>null</code>
     */
    EmdOther getEmdOther();

}
