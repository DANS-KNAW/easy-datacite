package nl.knaw.dans.common.fedora.fox;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nl.knaw.dans.common.fedora.fox.Datastream.State;
import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.RepoUtil;
import nl.knaw.dans.common.lang.repo.StorableObject;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.InvalidSidException;
import nl.knaw.dans.common.lang.xml.MinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.joda.time.DateTime;

import fedora.common.Constants;

/**
 * Java representation of a digital object.
 * 
 * @author ecco Sep 12, 2009
 */
public class DigitalObject extends AbstractTimestampedJiBXObject<DigitalObject>
{

    /**
     * Version of foxml.
     */
    public static final String VERSION_FOXML_1_1 = "1.1";

    /**
     * Default version of foxml.
     */
    public static final String DEFAULT_VERSION = VERSION_FOXML_1_1;

    /**
     * The format for digital objects.
     */
    public static String FORMAT_FOXML_1_1 = Constants.FOXML1_1.uri;

    private static final long serialVersionUID = -4684869544920361362L;
    private static final String SID_TOKEN = "([A-Za-z0-9]|-|\\.)+:(([A-Za-z0-9])|-|\\.|~|_|(%[0-9A-F]{2}))+";
    private static final int MAX_SID_LENGTH = 64;

    private String objectNamespace;
    private String version;
    private String sid;
    private URI fedoraURI;
    private DigitalObjectProperties objectProperties = new DigitalObjectProperties();
    private Map<String, Datastream> datastreams = new LinkedHashMap<String, Datastream>();

    /**
     * Constructor used for JiBX serialization.
     */
    protected DigitalObject()
    {
        // needed for JiBX serialization.
    }

    public DigitalObject(final String objectNamespace)
    {
        this(DobState.Active, objectNamespace);
    }

    /**
     * Constructs a new DigitalObject with the given state and objectNamespace.
     * 
     * @param state
     *        digitalObject state
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     */
    public DigitalObject(final DobState state, final String objectNamespace)
    {
        setDigitalObjectState(state);
        this.objectNamespace = objectNamespace;
    }

    /**
     * Get the objectNamespace of this digital object.
     * 
     * @return the objectNamespace or <code>null</code> if the objectNamespace is not set or cannot be derived
     */
    public String getObjectNamespace()
    {
        if (objectNamespace == null && sid != null)
        {
            try
            {
                objectNamespace = RepoUtil.getNamespaceFromSid(sid);
            }
            catch (InvalidSidException e)
            {
                objectNamespace = null;
            }
        }
        return objectNamespace;
    }

    /**
     * Set the objectNamespace for this digital object.
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @throws IllegalStateException
     *         if objectNamespace was set previously
     */
    public void setObjectNamespace(String objectNamespace) throws IllegalStateException
    {
        if (this.objectNamespace != null)
        {
            throw new IllegalStateException("Object namespace already set: " + objectNamespace);
        }
        this.objectNamespace = objectNamespace;
    }

    /**
     * Get the version number of this digital object.
     */
    public String getVersion()
    {
        if (version == null)
        {
            version = DEFAULT_VERSION;
        }
        return version;
    }

    /**
     * Set the version number of this digital object.
     * 
     * @param version
     *        version number of this digital object
     */
    public void setVersion(final String version)
    {
        this.version = version;
    }

    /**
     * Get the format of this digital object.
     * 
     * @return format of this digital object
     */
    public String getFormat()
    {
        return FORMAT_FOXML_1_1;
    }

    /**
     * Get the system id of this digital object.
     * 
     * @return the system id of this digital object
     */
    public String getSid()
    {
        return sid;
    }

    /**
     * Set the system id of this digital object. A system id must comply to the pattern
     * 
     * <pre>
     *    ([A-Za-z0-9]|-|\\.)+:(([A-Za-z0-9])|-|\\.|&tilde;|_|(%[0-9A-F]{2}))+
     * </pre>
     * 
     * and it's length may not exceed 64 characters.
     * 
     * @param sid
     *        system id of this digital object
     * @throws IllegalArgumentException
     *         if the given string does not comply to above mentioned restrictions
     */
    public void setSid(final String sid) throws IllegalArgumentException
    {
        if (sid == null)
        {
            this.sid = null;
        }
        else if (Pattern.matches(SID_TOKEN, sid) && sid.length() <= MAX_SID_LENGTH)
        {
            this.sid = sid;
        }
        else
        {
            throw new IllegalArgumentException("The string '" + sid + "' is not a valid fedora sid.");
        }
    }

    /**
     * Get the Fedora URI of this digital object.
     * 
     * @return Fedora URI of this digital object or <code>null</code> if it is not known
     */
    public URI getFedoraURI()
    {
        return fedoraURI;
    }

    void setFedoraURI(final URI uri)
    {
        fedoraURI = uri;
    }

    /**
     * Get the object properties for this digital object.
     * 
     * @return object properties for this digital object
     */
    public DigitalObjectProperties getObjectProperties()
    {
        return objectProperties;
    }

    /**
     * Set the object properties for this digital object.
     * 
     * @param objectProperties
     *        object properties for this digital object
     */
    public void setObjectProperties(DigitalObjectProperties objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    /**
     * Write the DigitalObjectProperties of this DigitalObject to the given StorableObject.
     * 
     * @param storable
     *        StorableObject to set properties on
     */
    public void writeObjectProperties(StorableObject storable)
    {
        storable.setStoreId(getSid());
        storable.setLabel(objectProperties.getLabel());
        storable.setOwnerId(objectProperties.getOwnerId());
        storable.setState(objectProperties.getStateAsString());
        storable.setDateCreated(objectProperties.getDateCreated());
        storable.setlastModified(objectProperties.getLastModified());
        storable.setTimestamp(objectProperties.getTimestamp());
    }

    /**
     * Read the DigitalObjectProperties for this DigitalObject from the given StorableObject.
     * 
     * @param storable
     *        StorableObject to get properties from
     */
    public void readObjectProperties(StorableObject storable)
    {
        objectProperties.setLabel(storable.getLabel());
        objectProperties.setOwnerId(storable.getOwnerId());
        if (storable.getState() == null)
        {
            objectProperties.setDigitalObjectState(DobState.Inactive);
        }
        else
        {
            objectProperties.setState(storable.getState());
        }
    }

    /**
     * Get the state of this digital object.
     * 
     * @return the state of this digital object or <code>null</code> if it is not known
     */
    public DobState getDigitalObjectState()
    {
        return objectProperties.getDigitalObjectState();
    }

    /**
     * Set the state of this digital object.
     * 
     * @param state
     *        the state of this digital object
     */
    public void setDigitalObjectState(final DobState state)
    {
        objectProperties.setDigitalObjectState(state);
    }

    /**
     * Get the label of this digital object.
     * 
     * @return the label of this digital object or <code>null</code> if it is not known
     */
    public String getLabel()
    {
        return objectProperties.getLabel();
    }

    /**
     * Set the label of this digital object.
     * 
     * @param label
     *        the label of this digital object
     */
    public void setLabel(final String label)
    {
        objectProperties.setLabel(label);
    }

    /**
     * Get the ownerId of this digital object.
     * 
     * @return the ownerId of this digital object or <code>null</code> if it is not known
     */
    public String getOwnerId()
    {
        return objectProperties.getOwnerId();
    }

    /**
     * Set the ownerId of this digital object.
     * 
     * @param ownerId
     *        the ownerId of this digital object
     */
    public void setOwnerId(final String ownerId)
    {
        objectProperties.setOwnerId(ownerId);
    }

    /**
     * Get the creation date of this digital object.
     * 
     * @return the creation date of this digital object or <code>null</code> if it is not known
     */
    public DateTime getCreatedDate()
    {
        return objectProperties.getDateCreated();
    }

    /**
     * Get the date of last modification of this digital object.
     * 
     * @return the date of last modification of this digital object or <code>null</code> if it is not known
     */
    public DateTime getLastModifiedDate()
    {
        return objectProperties.getLastModified();
    }

    /**
     * Get the value of an external property of the given name.
     * 
     * @param name
     *        name of the property
     * @return value of the property or <code>null</code> if it is not known
     */
    public String getExternalProperty(final String name)
    {
        return objectProperties.getExtProperty(name);
    }

    /**
     * Set the value of an external property.
     * 
     * @param name
     *        name of the property
     * @param value
     *        value of the property
     */
    public void setExternalProperty(final String name, final String value)
    {
        objectProperties.setExtProperty(name, value);
    }

    /**
     * Put the given datastream or replace it if a datastream with the same streamId already exists.
     * 
     * @param datastream
     *        a datastream
     * @return previous datastream associated with the streamId of the given datastream, or null if there was no mapping
     *         for the streamId
     */
    public Datastream putDatastream(final Datastream datastream)
    {
        return datastreams.put(datastream.getStreamId(), datastream);
    }

    /**
     * Get the datastream associated with the given streamId.
     * 
     * @param streamId
     *        streamId of the datastream
     * @return datastream associated with the given streamId, or null if there was no mapping for the given streamId
     */
    public Datastream getDatastream(final String streamId)
    {
        return datastreams.get(streamId);
    }

    /**
     * Remove the datastream associated with the given streamId.
     * 
     * @param streamId
     *        streamId of the datastream
     * @return datastream associated with the given streamId, or null if there was no mapping for the given streamId
     */
    public Datastream removeDatastream(final String streamId)
    {
        return datastreams.remove(streamId);
    }

    /**
     * Create and add a datastream with the given streamId and controlGroup.
     * 
     * @param streamId
     *        streamId of the datastream
     * @param controlGroup
     *        controlGroup of the datastream
     * @return newly created datastream
     * @throws IllegalStateException
     *         if a datastream associated with the given streamId already existed
     */
    public Datastream addDatastream(String streamId, ControlGroup controlGroup) throws IllegalStateException
    {
        if (getDatastream(streamId) != null)
        {
            throw new IllegalStateException("A Datastream with the streamId '" + streamId + "' already exists!");
        }
        Datastream stream = new Datastream(streamId, controlGroup);
        putDatastream(stream);
        return stream;
    }

    /**
     * Get a list of all the datastreams in this digital object.
     * 
     * @return a list of all the datastreams in this digital object
     */
    public List<Datastream> getDatastreams()
    {
        return new ArrayList<Datastream>(datastreams.values());
    }

    /**
     * Replace all datastreams in this digital object with datastreams from the given list.
     * 
     * @param streams
     *        a list of datastreams
     */
    public void setDatastreams(List<Datastream> streams)
    {
        datastreams.clear();
        if (streams != null)
        {
            for (Datastream stream : streams)
            {
                datastreams.put(stream.getStreamId(), stream);
            }
        }
    }

    /**
     * Add a datastreamVersion to the datastream of the given streamId, consisting of the root element of given
     * document. If a datastream associated with the given streamId did not exist it will be created. The
     * datastreamVersion will be of {@link ControlGroup#X} and will have {@link State#A}.
     * 
     * @param streamId
     *        streamId of the datastream
     * @param document
     *        the document to add as datastreamVersion
     * @return newly created datastreamVersion
     */
    public DatastreamVersion addDatastreamVersion(final String streamId, final Document document)
    {
        return addDatastreamVersion(streamId, document.getRootElement());
    }

    /**
     * Add a datastreamVersion to the datastream of the given streamId, consisting of the given element. If a datastream
     * associated with the given streamId did not exist it will be created. The datastreamVersion will be of
     * {@link ControlGroup#X} and will have {@link State#A}. Namespaces and prefixes not declared on the element must be
     * added.
     * 
     * @see Element#addNamespace(String, String)
     * @see Element#add(org.dom4j.Namespace)
     * @param streamId
     *        streamId of the datastream
     * @param element
     *        the element to add as datastreamVersion
     * @return newly created datastreamVersion
     */
    public DatastreamVersion addDatastreamVersion(final String streamId, final Element element)
    {
        Datastream datastream = datastreams.get(streamId);
        if (datastream == null)
        {
            datastream = addDatastream(streamId, ControlGroup.X);
            datastream.setState(Datastream.State.A);
        }
        final DatastreamVersion version = datastream.addDatastreamVersion(datastream.nextVersionId(), FoxConstants.MIMETYPE_XML);
        version.setXmlContent(element);
        return version;
    }

    /**
     * Add a datastreamVersion to the datastream of the given streamId, consisting of the serialization of the given
     * JiBXObject. If a datastream associated with the given streamId did not exist it will be created. The
     * datastreamVersion will be of {@link ControlGroup#X} and will have {@link State#A}.
     * 
     * @param streamId
     *        streamId of the datastream
     * @param xmlBean
     *        the xmlBean to add as datastreamVersion
     * @return newly created datastreamVersion
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    public DatastreamVersion addDatastreamVersion(final String streamId, final MinimalXMLBean xmlBean) throws XMLSerializationException
    {
        Datastream datastream = datastreams.get(streamId);
        if (datastream == null)
        {
            datastream = addDatastream(streamId, ControlGroup.X);
            datastream.setState(Datastream.State.A);
        }
        final DatastreamVersion version = datastream.addDatastreamVersion(datastream.nextVersionId(), FoxConstants.MIMETYPE_XML);
        try
        {
            version.setXmlContent(xmlBean.asObjectXML());
        }
        catch (DocumentException e)
        {
            throw new XMLSerializationException(e);
        }
        return version;
    }

    /**
     * Add a datastreamVersion to the datastream "DC", consisting of the serialization of the given DublinCoreMetadata.
     * If a datastream associated with the streamId "DC" did not exist it will be created. The datastreamVersion will be
     * of {@link ControlGroup#X} and will have {@link State#A}.
     * 
     * @param dcmd
     *        DublinCoreMetadata to add
     * @return newly created datastreamVersion
     * @throws XMLSerializationException
     *         for exceptions during serialization
     */
    public DatastreamVersion addDatastreamVersion(final DublinCoreMetadata dcmd) throws XMLSerializationException
    {
        final DatastreamVersion version = addDatastreamVersion(DublinCoreMetadata.UNIT_ID, dcmd);
        version.setFormatURI(DublinCoreMetadata.UNIT_FORMAT_URI);
        version.setLabel(DublinCoreMetadata.UNIT_LABEL);
        return version;
    }

    /**
     * Get the latest datastreamVersion of the datastream associated with the given streamId.
     * 
     * @param streamId
     *        streamId of the datastream
     * @return the latest datastreamVersion of the datastream associated with the given streamId or <code>null</code> if
     *         the datastream does not exist
     */
    public DatastreamVersion getLatestVersion(final String streamId)
    {
        DatastreamVersion version = null;
        final Datastream stream = datastreams.get(streamId);
        if (stream != null)
        {
            version = stream.getLatestVersion();
        }
        return version;
    }

    /**
     * Get the latest datastreamVersion of the datastream associated with the given streamId as element.
     * 
     * @param streamId
     *        streamId of the datastream
     * @return the latest datastreamVersion of the datastream associated with the given streamId as element or
     *         <code>null</code> if the datastream does not exist
     */
    public Element getLatestVersionElement(final String streamId)
    {
        Element element = null;
        final DatastreamVersion version = getLatestVersion(streamId);
        if (version != null)
        {
            element = version.getXmlContentElement();
        }
        return element;
    }

    /**
     * Get the latest DublinCoreMetadata.
     * 
     * @return latest DublinCoreMetadata
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    public DublinCoreMetadata getLatestDublinCoreMetadata() throws XMLDeserializationException
    {
        DublinCoreMetadata dcmd = null;
        final DatastreamVersion version = getLatestVersion(DublinCoreMetadata.UNIT_ID);
        if (version != null)
        {
            Element element = version.getXmlContentElement();
            dcmd = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, element);
            dcmd.setTimestamp(version.getTimestamp());
        }
        return dcmd;
    }

    /**
     * Get the audit trail of this digital object.
     * 
     * @return audit trail of this digital object or <code>null</code> if it is not known
     * @throws XMLDeserializationException
     *         for exceptions during deserialization
     */
    public AuditTrail getAuditTrail() throws XMLDeserializationException
    {
        AuditTrail auditTrail = null;
        Element atElement = getLatestVersionElement(AuditTrail.STREAM_ID);
        if (atElement != null)
        {
            auditTrail = (AuditTrail) JiBXObjectFactory.unmarshal(AuditTrail.class, atElement);
        }
        return auditTrail;
    }

    protected void postJiBXDeserialization()
    {
        if (objectProperties != null)
        {
            setTimestamp(objectProperties.getTimestamp());
        }
    }

}
