package nl.knaw.dans.common.lang.repo.bean;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.TimestampedMinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLBean;

/**
 * XMLBean capable of containing the fifteen terms of the Dublin Core Metadata Element Set. The Dublin
 * Core Metadata Element Set is a vocabulary of fifteen properties for use in resource description. The
 * name "Dublin" is due to its origin at a 1995 invitational workshop in Dublin, Ohio; "core" because its
 * elements are broad and generic, usable for describing a wide range of resources.
 * <p/>
 * Each property can have a value and an xml-language attribute as in the definition of
 * <code>elementType</code> in the next schema fragment.
 * 
 * <pre>
 * &lt;xs:complexType name=&quot;elementType&quot;&gt;
 *   &lt;xs:simpleContent&gt;
 *     &lt;xs:extension base=&quot;xs:string&quot;&gt;
 *       &lt;xs:attribute ref=&quot;xml:lang&quot; use=&quot;optional&quot;/&gt;
 *     &lt;/xs:extension&gt;
 *   &lt;/xs:simpleContent&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 * 
 * For convenience of use this interface constitutes methods for setting and getting properties of type
 * {@link String} as well as {@link XMLLangString}.
 * <p/>
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/">DCMI Metadata Terms</a>
 * @author ecco Sep 17, 2009
 */
public interface DublinCoreMetadata extends TimestampedMinimalXMLBean, MetadataUnit, XMLBean
{
    /**
     * Properties in the legacy namespace. (/elements/1.1/)
     * 
     * @author ecco Sep 17, 2009
     */
    public enum PropertyName
    {
        // CHECKSTYLE: OFF
        Contributor, Coverage, Creator, Date, Description, Format, Identifier, Language, Publisher, Relation, Rights, Source, Subject, Title, Type
    }

    String UNIT_ID = "DC";

    String UNIT_LABEL = "Dublin Core Record for this object";

    String UNIT_FORMAT = "http://www.openarchives.org/OAI/2.0/oai_dc/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    void addContributor(String contributor);

    void addCoverage(String coverage);

    void addCreator(String creator);

    void addDate(String date);

    void addDescription(String description);

    void addFormat(String format);

    void addIdentifier(String identifier);

    void addLanguage(String language);

    void addPublisher(String publisher);

    void addRelation(String relation);

    void addRights(String rights);

    void addSource(String source);

    void addSubject(String subject);

    void addTitle(String title);

    void addType(String type);

    List<String> getContributor();

    List<String> getCoverage();

    List<String> getCreator();

    List<String> getDate();

    List<String> getDescription();

    List<String> getFormat();

    List<String> getIdentifier();

    List<String> getLanguage();

    List<String> getPublisher();

    List<String> getRelation();

    List<String> getRights();

    List<String> getSource();

    List<String> getSubject();

    List<String> getTitle();

    List<String> getType();

    void setContributor(List<String> contributor);

    void setCoverage(List<String> coverage);

    void setCreator(List<String> creator);

    void setDate(List<String> date);

    void setDescription(List<String> description);

    void setFormat(List<String> format);

    void setIdentifier(List<String> identifier);

    void setLanguage(List<String> language);

    void setPublisher(List<String> publisher);

    void setRelation(List<String> relation);

    void setRights(List<String> rights);

    void setSource(List<String> source);

    void setSubject(List<String> subject);

    void setTitle(List<String> title);

    void setType(List<String> type);

    void set(PropertyName name, List<String> values);

    void set(PropertyName name, String value);

    void setXl(PropertyName name, List<XMLLangString> values);

    List<String> get(PropertyName name);

    List<XMLLangString> getXl(PropertyName name);

    String getFirst(PropertyName name);

    XMLLangString getFirstXl(PropertyName name);

    void add(PropertyName name, String value);

    void add(PropertyName name, String value, String language);

    void add(PropertyName name, String value, Locale locale);

    void add(PropertyName name, XMLLangString xmlLangString);

}
