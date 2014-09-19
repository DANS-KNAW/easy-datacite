package nl.knaw.dans.pf.language.emd;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Describes a metadata term.
 * 
 * @author ecco
 */
public class Term implements Serializable {

    /**
     * Terms found in easymetadata, heavily inspired by <a href="http://dublincore.org/documents/dcmi-terms/">dcmi-terms</a>.
     * 
     * @author ecco
     */
    public enum Name {
        // CHECKSTYLE: OFF
        // @formatter:off
        TITLE("title"), 
        ALTERNATIVE("alternative"), 
        CREATOR("creator"), 
        SUBJECT("subject"), 
        DESCRIPTION("description"), 
        TABLEOFCONTENTS("tableOfContents"), 
        ABSTRACT("abstract"), 
        RIGHTSHOLDER("rightsHolder"), 
        PUBLISHER("publisher"), 
        CONTRIBUTOR("contributor"), 
        DATE("date"), 
        CREATED("created"), 
        VALID("valid"), 
        AVAILABLE("available"), 
        ISSUED("issued"), 
        MODIFIED("modified"), 
        DATE_ACCEPTED("dateAccepted"), 
        DATE_COPYRIGHTED("dateCopyrighted"), 
        DATE_SUBMITTED("dateSubmitted"), 
        TYPE("type"), 
        FORMAT("format"), 
        EXTENT("extent"), 
        MEDIUM("medium"), 
        IDENTIFIER("identifier"), 
        RELATION("relation"), 
        CONFORMS_TO("conformsTo"), 
        IS_VERSION_OF("isVersionOf"), 
        HAS_VERSION("hasVersion"), 
        IS_REPLACED_BY("isReplacedBy"), 
        REPLACES("replaces"), 
        IS_REQUIRED_BY("isRequiredBy"), 
        REQUIRES("requires"), 
        IS_PART_OF("isPartOf"), 
        HAS_PART("hasPart"), 
        IS_REFERENCED_BY("isReferencedBy"), 
        REFERENCES("references"), 
        IS_FORMAT_OF("isFormatOf"), 
        HAS_FORMAT("hasFormat"), 
        SOURCE("source"), 
        LANGUAGE("language"), 
        COVERAGE("coverage"), 
        SPATIAL("spatial"), 
        TEMPORAL("temporal"), 
        RIGHTS("rights"), 
        ACCESSRIGHTS("accessRights"), 
        LICENSE("license"), 
        AUDIENCE("audience"), 
        REMARKS("remarks");// ,
        // APPLICATION_SPECIFIC("applicationSpecific");
        // @formatter:on

        public final String termName;

        Name(String termName) {
            this.termName = termName;
        }
        // ecco: CHECKSTYLE: ON
    }

    /**
     * Namespaces found in easymetadata. The namespace makes it possible to have different formats for the same term.
     * 
     * @author ecco
     */
    public enum Namespace {
        // CHECKSTYLE: OFF
        DC("http://purl.org/dc/elements/1.1/", "dc"), //
        DCTERMS("http://purl.org/dc/terms/", "terms"), //
        EAS("http://easy.dans.knaw.nl/easy/easymetadata/eas/", "eas");

        public final String uri;
        public final String prefix;

        Namespace(String uri, String prefix) {
            this.uri = uri;
            this.prefix = prefix;
        }
        // CHECKSTYLE: ON
    }

    private static final long serialVersionUID = 1035199854115482923L;

    private final Name name;
    private final Namespace namespace;
    private final Class<?> clazz;

    /**
     * Constructs a new Term.
     * 
     * @param name
     *        the name of the term
     */
    public Term(final Name name) {
        this.name = name;
        namespace = null;
        clazz = null;
    }

    /**
     * Constructs a new Term.
     * 
     * @param name
     *        name
     * @param namespace
     *        name space
     */
    public Term(final Name name, final Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
        clazz = null;
    }

    /**
     * Constructs a new Term. The value of the parameter <code>qName</code> is a string
     * <ol>
     * <li>consisting of the toString value of a {@link Term.Name} ("TITLE").</li>
     * <li>same as (1) but preceded with the toString value of a {@link Term.Namespace} and a period ("DC.TITLE").
     * </ol>
     * though case is of no importance.
     * 
     * @param qName
     *        string representing name or qualified name of the term
     */
    public Term(final String qName) {
        final String[] nsn = qName.split("\\.");
        if (nsn.length > 1) {
            name = Name.valueOf(Name.class, nsn[1].toUpperCase());
            namespace = Namespace.valueOf(Namespace.class, nsn[0].toUpperCase());
            clazz = null;
        } else {
            name = Name.valueOf(Name.class, nsn[0].toUpperCase());
            namespace = null;
            clazz = null;
        }
    }

    /**
     * Constructs a new Term. Parameter values should correspond to the toString values of {@link Term.Name} ("TITLE") and {@link Term.Namespace} ("DC"), though
     * case is of no importance.
     * 
     * @param name
     *        string representing name
     * @param namespace
     *        string representing namespace
     */
    public Term(final String name, final String namespace) {
        this.name = Name.valueOf(Name.class, name.toUpperCase());
        this.namespace = Namespace.valueOf(Namespace.class, namespace.toUpperCase());
        clazz = null;
    }

    /**
     * Constructs a new Term.
     * 
     * @param name
     *        name
     * @param namespace
     *        namespace
     * @param clazz
     *        the class that holds the values of this term
     */
    public Term(final Name name, final Namespace namespace, final Class<?> clazz) {
        this.name = name;
        this.namespace = namespace;
        this.clazz = clazz;
    }

    /**
     * Get the name.
     * 
     * @return the name
     */
    public Name getName() {
        return name;
    }

    /**
     * Get the namespace.
     * 
     * @return the namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * Get the type that holds the values of this term.
     * 
     * @return type
     */
    public Class<?> getType() {
        return clazz;
    }

    /**
     * Return a -sort of- qualified name of this term.
     * 
     * @return qualified name
     */
    public String getQualifiedName() {
        return (namespace == null ? "" : namespace.name() + ".") + name.name();
    }

    public String getMethodName() {
        return (namespace == null ? "" : namespace.name()) + name.name();
    }

    /**
     * Test if object is equal.
     * 
     * @param obj
     *        object to test
     * @return true if object is equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Term)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Term rhs = (Term) obj;
        return new EqualsBuilder().append(name, rhs.name).append(namespace, rhs.namespace).isEquals();
    }

    /**
     * Return hashCode.
     * 
     * @return hash code of this Term
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 37).append(name).append(namespace).toHashCode();
    }

    /**
     * Returns a string-representation of this term.
     * 
     * @return a string-representation of this term
     */
    @Override
    public String toString() {
        return Term.class.getName() + " [name=" + name + " namespace=" + namespace + " " + clazz + "]";
    }

}
