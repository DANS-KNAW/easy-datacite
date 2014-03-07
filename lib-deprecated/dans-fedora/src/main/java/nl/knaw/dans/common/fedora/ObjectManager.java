package nl.knaw.dans.common.fedora;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DigitalObjectProperties;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.StorableObject;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.common.Constants;

/**
 * Implements object management methods on the APIM interface.
 * <p/>
 * The Fedora Management service defines an open interface for administering the repository, including creating,
 * modifying, and deleting digital objects, or components within digital objects. The Management service interacts with
 * the underlying repository system to read content from and write content to the digital object and datastream storage
 * areas. The Management service exposes a set of operations that enable a client to view and manipulate digital objects
 * from an abstract perspective, meaning that a client does not need to know anything about underlying storage formats,
 * storage media, or storage management schemes for objects. Also, the underlying repository system handles the details
 * of storing datastream content within the repository, as well as mediating connectivity for datastreams that reference
 * external content.
 * 
 * @see <a href="http://fedora-commons.org/confluence/display/FCR30/API-M">FedoraCommons APIM</a>
 * @author ecco Sep 6, 2009
 */
public class ObjectManager
{
    /**
     * Export context, which determines how datastream URLs and content are represented in an export.
     * 
     * @see ObjectManager#export(String, String, ExportContext)
     * @author ecco Sep 6, 2009
     */
    public enum ExportContext
    {
        PUBLIC, MIGRATE, ARCHIVE
    }

    public static final String EXPORT_FORMAT_ATOM1_1 = Constants.ATOM1_1.uri;

    public static final String EXPORT_FORMAT_ATOM_ZIP1_1 = Constants.ATOM_ZIP1_1.uri;

    public static final String EXPORT_FORMAT_FOXML1_0 = Constants.FOXML1_0.uri;

    public static final String EXPORT_FORMAT_FOXML1_1 = Constants.FOXML1_1.uri;

    public static final String EXPORT_FORMAT_METS1_0 = Constants.METS_EXT1_0.uri;

    public static final String EXPORT_FORMAT_METS1_1 = Constants.METS_EXT1_1.uri;

    public static final int DEFAULT_SIDLIST_BUFFER_SIZE = 1;

    private static final Logger logger = LoggerFactory.getLogger(ObjectManager.class);

    private final Repository repository;

    private final Map<String, SidList> sidListMap = Collections.synchronizedMap(new HashMap<String, SidList>());

    private int sidListBufferSize = DEFAULT_SIDLIST_BUFFER_SIZE;

    /**
     * Constructs a new ObjectManager with the given Repository as base.
     * 
     * @param repository
     *        Repository to manage
     */
    public ObjectManager(Repository repository)
    {
        this.repository = repository;
    }

    /**
     * Gets the serialization of the digital object to XML appropriate for persistent storage in the repository,
     * ensuring that any URLs that are relative to the local repository are stored with the Fedora local URL syntax. The
     * Fedora local URL syntax consists of the string "local.fedora.server" standing in place of the actual
     * "hostname:port" on the URL). Managed Content (M) datastreams are stored with internal identifiers in dsLocation.
     * Also, within selected inline XML datastreams (i.e., WSDL and SERVICE_PROFILE) any URLs that are relative to the
     * local repository will also be stored with the Fedora local URL syntax.
     * 
     * @param sid
     *        The sid of the object
     * @return The digital object in Fedora's internal storage format
     * @throws ObjectNotInStoreException
     *         if a digital object with the given sid is not in the repository or could not be found
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public byte[] getObjectXML(String sid) throws ObjectNotInStoreException, RepositoryException
    {
        byte[] objectXML = null;
        try
        {
            objectXML = repository.getFedoraAPIM().getObjectXML(sid);
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieved objectXML. sid=" + sid);
            }

        }
        catch (RemoteException e)
        {
            String msg = "Unable to retrieve the objectXML for sid [" + sid + "]: ";
            logger.debug(msg);
            Repository.mapRemoteException(msg, e);
        }
        return objectXML;
    }

    /**
     * Gets the serialization of the digital object deserialized as java object.
     * 
     * @see #getObjectXML(String)
     * @param sid
     *        The sid of the object
     * @return The digital object as java object
     * @throws ObjectNotInStoreException
     *         if a digital object with the given sid is not in the repository or could not be found
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DigitalObject getDigitalObject(String sid) throws ObjectNotInStoreException, RepositoryException
    {
        try
        {
            return (DigitalObject) JiBXObjectFactory.unmarshal(DigitalObject.class, getObjectXML(sid));
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

    /**
     * Creates a new digital object in the repository. If the XML document does not specify the PID attribute of the
     * root element, the repository will generate and return a new pid for the object resulting from this request. That
     * pid will have the namespace of the repository. If the XML document specifies a pid, it will be assigned to the
     * digital object provided that 1. it conforms to the Fedora pid Syntax, 2. it uses a namespace that matches the
     * "retainPIDs" value configured for the repository, and 3. it does not collide with an existing pid of an object in
     * the repository.
     * 
     * @param objectXML
     *        The digital object in an XML submission format
     * @param format
     *        The XML format of objectXML, one of
     * 
     *        <pre>
     *        info:fedora/fedora-system:FOXML-1.1
     *        info:fedora/fedora-system:FOXML-1.0
     *        info:fedora/fedora-system:METSFedoraExt-1.1
     *        info:fedora/fedora-system:METSFedoraExt-1.0
     *        info:fedora/fedora-system:ATOM-1.1
     *        info:fedora/fedora-system:ATOMZip-1.1
     * </pre>
     * @param logMessage
     *        A log message
     * @return The sid of the newly created object
     * @throws ObjectExistsException
     *         if a digital object with the same sid is already in the repository
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String ingest(byte[] objectXML, String format, String logMessage) throws ObjectExistsException, RepositoryException
    {
        String sid = null;
        try
        {
            sid = repository.getFedoraAPIM().ingest(objectXML, format, logMessage);
            if (logger.isDebugEnabled())
            {
                logger.debug("Ingested object. sid=" + sid);
            }
        }
        catch (final RemoteException e)
        {
            final String msg = "Unable to ingest an object: \n" + new String(objectXML) + "\n";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return sid;
    }

    /**
     * Creates a new digital object in the repository.
     * 
     * @see #ingest(byte[], String, String)
     * @param digitalObject
     *        object as java object
     * @param logMessage
     *        A log message
     * @return The sid of the newly created object
     * @throws ObjectExistsException
     *         if a digital object with the same sid is already in the repository
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String ingest(DigitalObject digitalObject, String logMessage) throws ObjectExistsException, RepositoryException
    {
        String sid = null;
        try
        {
            if (digitalObject.getSid() == null)
            {
                digitalObject.setSid(nextSid(digitalObject.getObjectNamespace()));
            }

            sid = ingest(digitalObject.asObjectXML(), digitalObject.getFormat(), logMessage);
            digitalObject.setSid(sid);
        }
        catch (XMLSerializationException e)
        {
            throw new ObjectSerializationException(e);
        }
        return sid;
    }

    /**
     * Permanently removes an object from the repository.
     * 
     * @param sid
     *        the sid of the object
     * @param force
     *        Force the purge, even if it would break a dependency
     * @param logMessage
     *        A log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime purgeObject(String sid, boolean force, String logMessage) throws RepositoryException
    {
        String timeStamp = null;
        try
        {
            timeStamp = repository.getFedoraAPIM().purgeObject(sid, logMessage, force);
            if (logger.isDebugEnabled())
            {
                logger.debug("Purged object. sid=" + sid);
            }
        }
        catch (final RemoteException e)
        {
            final String msg = "Unable to purge an object. sid=" + sid;
            logger.debug(msg);
            Repository.mapRemoteException(msg, e);
        }
        DateTime purgeTime = null;
        if (timeStamp != null)
        {
            purgeTime = new DateTime(timeStamp);
        }
        return purgeTime;
    }

    /**
     * Permanently removes the given digitalObject from the repository.
     * 
     * @param digitalObject
     *        object to remove
     * @param force
     *        Force the purge, even if it would break a dependency
     * @param logMessage
     *        A log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime purgeObject(DigitalObject digitalObject, boolean force, String logMessage) throws RepositoryException
    {
        return purgeObject(digitalObject.getSid(), force, logMessage);
    }

    public DateTime modifyObject(String sid, String state, String label, String ownerId, String logmessage) throws RepositoryException
    {
        String timeStamp = null;
        try
        {
            timeStamp = repository.getFedoraAPIM().modifyObject(sid, state, label, ownerId, logmessage);
            if (logger.isDebugEnabled())
            {
                logger.debug("Modified object. sid=" + sid);
            }
        }
        catch (RemoteException e)
        {
            final String msg = "Unable to modify an object. sid=" + sid;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(timeStamp);
    }

    /**
     * Modify an object. (Method signature will change with fedora 3.2 ??)
     * 
     * @param sid
     *        sid of the object
     * @param state
     *        digitalObject state
     * @param label
     *        label of the object
     * @param ownerId
     *        ownerId of the object
     * @param logmessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyObject(String sid, DobState state, String label, String ownerId, String logmessage) throws RepositoryException
    {
        return modifyObject(sid, state.fedoraQuirck, label, ownerId, logmessage);
    }

    /**
     * Modify the object properties of an object.
     * 
     * @see #modifyObject(String, DobState, String, String, String)
     * @param digitalObject
     *        the object to modify
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws ConcurrentUpdateException
     *         if the timestamp of ObjectProperties is older than the last registered modification of the object
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyObject(DigitalObject digitalObject, String logMessage) throws ConcurrentUpdateException, RepositoryException
    {
        return modifyObject(digitalObject.getSid(), digitalObject.getObjectProperties(), logMessage);
    }

    /**
     * Modify the object properties of an object.
     * 
     * @param sid
     * @param objProperties
     *        ObjectProperties to modify
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyObject(String sid, DigitalObjectProperties objProperties, String logMessage) throws RepositoryException
    {
        DateTime modificationTime = modifyObject(sid, objProperties.getDigitalObjectState(), objProperties.getLabel(), objProperties.getOwnerId(), logMessage);
        objProperties.setTimestamp(modificationTime);
        return modificationTime;
    }

    // TODO state needed for oai: find out how to tackle strange behavior of Fedora.
    public DateTime modifyObjectProperties(StorableObject storable, String logMessage) throws ConcurrentUpdateException, RepositoryException
    {
        DobState dobState = DobState.valueFor(storable.getState());
        String state = dobState == null ? null : dobState.fedoraQuirck;
        DateTime modificationTime = modifyObject(storable.getStoreId(), state, storable.getLabel(), storable.getOwnerId(), logMessage);
        storable.setTimestamp(modificationTime);
        storable.setDirty(false);
        return modificationTime;
    }

    /**
     * Exports the entire digital object in the specified XML format, and encoded appropriately for the specified export
     * context.
     * 
     * @see #EXPORT_FORMAT_ATOM1_1
     * @see #EXPORT_FORMAT_ATOM_ZIP1_1
     * @see #EXPORT_FORMAT_FOXML1_0
     * @see #EXPORT_FORMAT_FOXML1_1
     * @see #EXPORT_FORMAT_METS1_0
     * @see #EXPORT_FORMAT_METS1_1
     * @param sid
     *        sid of the object
     * @param format
     *        The XML format to export, one of "info:fedora/fedora-system:FOXML-1.1",
     *        "info:fedora/fedora-system:FOXML-1.0", "info:fedora/fedora-system:METSFedoraExt-1.1",
     *        "info:fedora/fedora-system:METSFedoraExt-1.0", "info:fedora/fedora-system:ATOM-1.1", or
     *        "info:fedora/fedora-system:ATOMZip-1.1"
     * @param context
     *        The export context, which determines how datastream URLs and content are represented
     * @return the digital object in the requested XML format
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public byte[] export(String sid, String format, ExportContext context) throws RepositoryException
    {
        byte[] objectXML = null;
        try
        {
            objectXML = repository.getFedoraAPIM().export(sid, format, context.toString());
            if (logger.isDebugEnabled())
            {
                logger.debug("Exported object. sid=" + sid + " format=" + format + " context=" + context.toString().toLowerCase());
            }
        }
        catch (RemoteException e)
        {
            final String msg = "Unable to export an object. sid=" + sid;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return objectXML;
    }

    /**
     * Get the next sid for the given objectNamespace.
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @return next sid for the given objectNamespace
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String nextSid(String objectNamespace) throws RepositoryException
    {
        return getSidList(objectNamespace).nextSid();
    }

    /**
     * Get the common buffer size for sidLists.
     * 
     * @return the common buffer size for sidLists
     */
    public int getSidListBufferSize()
    {
        return sidListBufferSize;
    }

    /**
     * Set the common buffer size for sidLists.
     * 
     * @param sidListBufferSize
     *        the common buffer size for sidLists
     * @throws IllegalArgumentException
     *         for sidListBufferSize < 1
     */
    public void setSidListBufferSize(int sidListBufferSize) throws IllegalArgumentException
    {
        if (sidListBufferSize < 1)
        {
            throw new IllegalArgumentException("Buffer size cannot be less than 1.");
        }
        this.sidListBufferSize = sidListBufferSize;
    }

    /**
     * Get the buffer size of the sidList for the given namespace
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @return buffer size of the sidList for the given namespace
     */
    public int getSidListBufferSize(String objectNamespace)
    {
        return getSidList(objectNamespace).getBufferSize();
    }

    /**
     * Set the buffer size of the sidList for the given namespace.
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @param bufferSize
     *        buffer size of the sidList for the given namespace
     */
    public void setSidListBufferSize(String objectNamespace, int bufferSize)
    {
        getSidList(objectNamespace).setBufferSize(bufferSize);
        logger.debug("sidListBufferSize of objectNamespace " + objectNamespace + " has been set to " + bufferSize);
    }

    /**
     * Get the sidList for the given objectNamespae or create one if it didn't exist.
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @return sidList for the given objectNamespae
     */
    public SidList getSidList(String objectNamespace)
    {
        SidList sidList;
        synchronized (sidListMap)
        {
            sidList = sidListMap.get(objectNamespace);
            if (sidList == null)
            {
                sidList = new SidList(repository, objectNamespace, getSidListBufferSize());
                sidListMap.put(objectNamespace, sidList);
            }
        }
        return sidList;
    }

}
