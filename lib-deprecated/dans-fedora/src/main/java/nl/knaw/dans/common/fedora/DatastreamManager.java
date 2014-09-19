package nl.knaw.dans.common.fedora;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.fedora.fox.ContentDigestType;
import nl.knaw.dans.common.fedora.fox.ContentLocation;
import nl.knaw.dans.common.fedora.fox.ControlGroup;
import nl.knaw.dans.common.fedora.fox.DataStreamMetaData;
import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.Datastream.State;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.FoxConstants;
import nl.knaw.dans.common.jibx.util.Converter;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.TimestampedObject;
import nl.knaw.dans.common.lang.repo.TimestampedMinimalXMLBean;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.xml.MinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements datastream management methods on the APIM interface.
 * 
 * @author ecco Sep 13, 2009
 */
public class DatastreamManager {

    private static final Logger logger = LoggerFactory.getLogger(DatastreamManager.class);

    private final Repository repository;

    /**
     * Constructs a new DatastreamManager with the given Repository as target.
     * 
     * @param repository
     *        Repository to manage
     */
    public DatastreamManager(final Repository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new datastream in the object - only for Externally Referenced or Redirected Content.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID (64 characters max). If null, Fedora will generate the value.
     * @param altIds
     *        Alternate identifiers for the datastream. Can be null.
     * @param dsLabel
     *        The label for the datastream. Can be null.
     * @param versionable
     *        Enable versioning of the datastream.
     * @param mimeType
     *        The mime-type of the datastream. Can be null.
     * @param formatURI
     *        The format URI of the datastream. Can be null.
     * @param dsLocation
     *        Location of managed, redirect, or external referenced datastream content
     * @param controlGroup
     *        the control group of the datastream, one of {@link ControlGroup#E} or {@link ControlGroup#R}
     * @param dsState
     *        the state of the datastream
     * @param contentDigestType
     *        The algorithm used to compute the checksum
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string. Can be null.
     * @param logMessage
     *        A log message
     * @return The datastreamID of the newly added datastream
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String addDatastream(final String sid, final String streamId, final String[] altIds, final String dsLabel, final boolean versionable,
            final String mimeType, final String formatURI, final String dsLocation, final ControlGroup controlGroup, final State dsState,
            final String contentDigestType, final String checksum, final String logMessage) throws RepositoryException
    {
        String datastreamId = null;
        try {
            datastreamId = repository.getFedoraAPIM().addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsLocation,
                    controlGroup.toString(), dsState.toString(), contentDigestType, checksum, logMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Added datastream. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to add a datastream: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return datastreamId;
    }

    /**
     * Creates a new datastream in the object - only for Externally Referenced or Redirected Content. The {@link Datastream} should at least have one
     * {@link DatastreamVersion} . The DatastreamVersion should at least have one {@link ContentLocation}. Only the first DatastreamVersion is added.
     * <p/>
     * <b>Example with external or referenced content</b>
     * 
     * <pre>
     * Datastream datastream = new Datastream(&quot;DS&quot;, ControlGroup.E);
     * DatastreamVersion version = datastream.addDatastreamVersion(null, &quot;text/plain&quot;);
     * version.setContentLocation(Type.URL, URI.create(&quot;http://some.uri&quot;));
     * </pre>
     * 
     * @param sid
     *        The sid of the object
     * @param datastream
     *        datastream to add with {@link ControlGroup#E} or {@link ControlGroup#R}
     * @param logMessage
     *        A log message
     * @return The datastreamID of the newly added datastream
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String addDatastream(final String sid, final Datastream datastream, final String logMessage) throws RepositoryException {
        String[] altIds = null;
        String label = null;
        String mimeType = null;
        String formatURI = null;
        String contentLocation = null;
        String contentDigestType = ContentDigestType.DISABLED.code;
        String checksum = null;

        final List<DatastreamVersion> datastreamVersions = datastream.getDatastreamVersions();
        if (datastreamVersions.size() > 0) {
            final DatastreamVersion version = datastreamVersions.get(0);
            altIds = version.getAltIdArray();
            label = version.getLabel();
            mimeType = version.getMimeType();
            formatURI = version.getFormatURI() == null ? null : version.getFormatURI().toString();
            contentLocation = version.getDsLocation();
            contentDigestType = version.getChecksumType();
            checksum = version.getContentDigest();
        }
        final String streamId = datastream.getStreamId();
        final boolean versionable = datastream.isVersionable();
        final ControlGroup controlGroup = datastream.getControlGroup();
        final State dsState = datastream.getState();

        return addDatastream(sid, streamId, altIds, label, versionable, mimeType, formatURI, contentLocation, controlGroup, dsState, contentDigestType,
                checksum, logMessage);
    }

    /**
     * Creates a new datastream in the object. This method takes an {@link InputStream} as carrier for the content. The input stream is closed after this method
     * has done with it.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID (64 characters max). If null, Fedora will generate the value.
     * @param altIds
     *        Alternate identifiers for the datastream. Can be null.
     * @param dsLabel
     *        The label for the datastream. Can be null.
     * @param versionable
     *        Enable versioning of the datastream.
     * @param mimeType
     *        The mime-type of the datastream. Can be null.
     * @param formatURI
     *        The format URI of the datastream. Can be null.
     * @param inStream
     *        the content of the datastream
     * @param controlGroup
     *        the control group of the datastream
     * @param dsState
     *        the state of the datastream
     * @param contentDigestType
     *        The algorithm used to compute the checksum
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string. Can be null.
     * @param logMessage
     *        A log message
     * @return The datastreamID of the newly added datastream
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String addDatastream(final String sid, final String streamId, final String[] altIds, final String dsLabel, final boolean versionable,
            final String mimeType, final String formatURI, final InputStream inStream, final ControlGroup controlGroup, final State dsState,
            final String contentDigestType, final String checksum, final String logMessage) throws RepositoryException
    {
        String dsLocation = null;
        try {
            dsLocation = repository.upload(inStream);
        }
        catch (final IOException e) {
            final String msg = "Could not close io-streams: ";
            logger.error(msg, e);
            throw new RepositoryException(msg, e);
        }
        return addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsLocation, controlGroup, dsState, contentDigestType, checksum,
                logMessage);
    }

    /**
     * Creates a new datastream in the object. This method takes a {@link File} as carrier for the content.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID (64 characters max). If null, Fedora will generate the value.
     * @param altIds
     *        Alternate identifiers for the datastream. Can be null.
     * @param dsLabel
     *        The label for the datastream. Can be null.
     * @param versionable
     *        Enable versioning of the datastream.
     * @param mimeType
     *        The mime-type of the datastream. Can be null.
     * @param formatURI
     *        The format URI of the datastream. Can be null.
     * @param file
     *        the content of the datastream
     * @param controlGroup
     *        the control group of the datastream
     * @param dsState
     *        the state of the datastream
     * @param contentDigestType
     *        The algorithm used to compute the checksum
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string. Can be null.
     * @param logMessage
     *        A log message
     * @return The datastreamID of the newly added datastream
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String addDatastream(final String sid, final String streamId, final String[] altIds, final String dsLabel, final boolean versionable,
            final String mimeType, final String formatURI, final File file, final ControlGroup controlGroup, final State dsState,
            final String contentDigestType, final String checksum, final String logMessage) throws RepositoryException
    {
        final String dsLocation = repository.upload(file);
        return addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsLocation, controlGroup, dsState, contentDigestType, checksum,
                logMessage);
    }

    /**
     * Creates a new datastream in the object. This method takes a {@link MinimalXMLBean} as carrier for the content.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID (64 characters max). If null, Fedora will generate the value.
     * @param altIds
     *        Alternate identifiers for the datastream. Can be null.
     * @param dsLabel
     *        The label for the datastream. Can be null.
     * @param versionable
     *        Enable versioning of the datastream.
     * @param mimeType
     *        The mime-type of the datastream. Can be null.
     * @param formatURI
     *        The format URI of the datastream. Can be null.
     * @param file
     *        the content of the datastream
     * @param controlGroup
     *        the control group of the datastream
     * @param dsState
     *        the state of the datastream
     * @param contentDigestType
     *        The algorithm used to compute the checksum
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string. Can be null.
     * @param logMessage
     *        A log message
     * @return The datastreamID of the newly added datastream
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String addDatastream(final String sid, final String streamId, final String[] altIds, final String dsLabel, final boolean versionable,
            final String mimeType, final String formatURI, final XMLBean jibxObject, final ControlGroup controlGroup, final State dsState,
            final String contentDigestType, final String checksum, final String logMessage) throws RepositoryException
    {
        String dsLocation = null;
        dsLocation = repository.upload(jibxObject);
        return addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsLocation, controlGroup, dsState, contentDigestType, checksum,
                logMessage);
    }

    /**
     * Verifies that the Datastream content has not changed since the checksum was initially computed.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID
     * @param versionDate
     *        A dateTime indicating the version of the datastream to verify. If null, Fedora will use the most recent version.
     * @return The checksum if there is no difference, a message indicating checksum failure otherwise
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public String compareDatastreamChecksum(final String sid, final String streamId, final DateTime versionDate) throws RepositoryException {
        String checksumMessage = null;
        final String versionDateString = Converter.serializeToXml(versionDate);
        try {
            checksumMessage = repository.getFedoraAPIM().compareDatastreamChecksum(sid, streamId, versionDateString);
            if (logger.isDebugEnabled()) {
                logger.debug("Compared datastream checksum. message=" + checksumMessage);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to compare datastream checksum: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return checksumMessage;
    }

    /**
     * Gets the specified datastream.
     * 
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID
     * @param versionDate
     *        The date/time stamp specifying the desired version of the object. If null, the current version of the object (the most recent time) is assumed.
     * @return fedora.server.types.gen.Datastream
     *         <ul>
     *         <li>DatastreamControlGroup controlGroup - String restricted to the values of "X", "M", "R", or "E" (InlineXML,Managed Content,Redirect, or
     *         External Referenced).</li>
     *         <li>String ID - The datastream ID (64 characters max).</li>
     *         <li>String versionID - The ID of the most recent datastream version</li>
     *         <li>String[] altIDs - Alternative IDs for the datastream, if any.</li>
     *         <li>String label - The Label of the datastream.</li>
     *         <li>boolean versionable - Whether the datastream is versionable.</li>
     *         <li>String MIMEType - The mime-type for the datastream, if set.</li>
     *         <li>String formatURI - The format uri for the datastream, if set.</li>
     *         <li>String createDate - The date the first version of the datastream was created.</li>
     *         <li>long size - The size of the datastream in Fedora. Not the size of any referenced contents, but only the fedora stored xml.</li>
     *         <li>String state - The state of the datastream. Will be "A" (active), "I" (inactive) or "D" (deleted).</li>
     *         <li>String location - If the datastream is an external reference or redirect, the url to the contents.</li>
     *         <li>String checksumType - The algorithm used to compute the checksum. One of "DEFAULT", "DISABLED", "MD5", "SHA-1", "SHA-256", "SHA-385",
     *         "SHA-512", "HAVAL", "TIGER", "WHIRLPOOL".</li>
     *         <li>String checksum - The value of the checksum represented as a hexadecimal string.</li>
     *         </ul>
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public fedora.server.types.gen.Datastream getDatastream(final String sid, final String streamId, final DateTime versionDate) throws RepositoryException {
        final String versionDateString = Converter.serializeToXml(versionDate);
        fedora.server.types.gen.Datastream ds = null;
        try {
            ds = repository.getFedoraAPIM().getDatastream(sid, streamId, versionDateString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got datastream. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to retrieve a datastream:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return ds;
    }

    /**
     * Gets all versions of a datastream, sorted from most to least recent.
     * 
     * @see #getDatastream(String, String, DateTime)
     * @param sid
     *        The sid of the object
     * @param streamId
     *        The datastream ID
     * @return fedora.server.types.gen.Datastream[]
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public fedora.server.types.gen.Datastream[] getDatastreamHistory(final String sid, final String streamId) throws RepositoryException {
        fedora.server.types.gen.Datastream[] dss = null;
        try {
            dss = repository.getFedoraAPIM().getDatastreamHistory(sid, streamId);
            if (logger.isDebugEnabled()) {
                logger.debug("Got datastream history. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to retrieve a datastream history:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return dss;
    }

    public List<UnitMetadata> getDatastreamMetadata(final String sid, final String streamId) throws RepositoryException {
        final List<UnitMetadata> result = new ArrayList<UnitMetadata>();
        final fedora.server.types.gen.Datastream[] streams = getDatastreamHistory(sid, streamId);
        if (streams != null) {
            for (final fedora.server.types.gen.Datastream ds : streams) {
                result.add(new DataStreamMetaData(ds, sid));
            }
        }
        return result;
    }

    public List<UnitMetadata> getDatastreamMetadata(final String storeId) throws RepositoryException {
        final List<UnitMetadata> result = new ArrayList<UnitMetadata>();
        final fedora.server.types.gen.Datastream[] streams = getDatastreams(storeId, null, State.A);
        if (streams != null) {
            for (final fedora.server.types.gen.Datastream ds : streams) {
                result.add(new DataStreamMetaData(ds, storeId));
            }
        }
        return result;
    }

    /**
     * Gets all datastreams in the object.
     * 
     * @see #getDatastream(String, String, DateTime)
     * @param sid
     *        The sid of the object
     * @param asOfDateTime
     *        The date/time stamp specifying the desired version of the object. If null, the current version of the object (the most recent time) is assumed.
     * @param dsState
     *        the state of the datastream
     * @return fedora.server.types.gen.Datastream[]
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public fedora.server.types.gen.Datastream[] getDatastreams(final String sid, final DateTime asOfDateTime, final State dsState) throws RepositoryException {
        final String asOfDateTimeString = Converter.serializeToXml(asOfDateTime);
        fedora.server.types.gen.Datastream[] dss = null;
        try {
            dss = repository.getFedoraAPIM().getDatastreams(sid, asOfDateTimeString, dsState.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("Got all datastreams. sid=" + sid + " state=" + dsState.toString());
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to retrieve datastreams:  sid=" + sid;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return dss;
    }

    /**
     * Change the referenced location for a datastream. This operation is only relevant for managed, redirect or external reference datastreams (controlgroup
     * E,M,R).
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param altIds
     *        Alternate identifiers for the datastream, if any
     * @param dsLabel
     *        The label for the datastream
     * @param mimeType
     *        The mime type
     * @param formatUri
     *        Optional format URI of the datastream
     * @param dsLocation
     *        Location of managed, redirect, or external referenced datastream content
     * @param contentDigestType
     *        The algorithm used to compute the checksum. One of "DEFAULT", "DISABLED", "MD5", "SHA-1", "SHA-256", "SHA-385", "SHA-512", "HAVAL", "TIGER",
     *        "WHIRLPOOL".
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string
     * @param logMessage
     *        A log message
     * @param force
     *        Force the update even if it would break a data contract
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByReference(final String sid, final String streamId, final String[] altIds, final String dsLabel, final String mimeType,
            final String formatUri, final String dsLocation, final String contentDigestType, final String checksum, final String logMessage, final boolean force)
            throws RepositoryException
    {
        String modificationTime = null;
        try {
            modificationTime = repository.getFedoraAPIM().modifyDatastreamByReference(sid, streamId, altIds, dsLabel, mimeType, formatUri, dsLocation,
                    contentDigestType, checksum, logMessage, force);
            if (logger.isDebugEnabled()) {
                logger.debug("Modified datastream by reference. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to modify datastream by reference:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(modificationTime);
    }

    /**
     * Change the referenced location for a datastream. This operation is only relevant for managed, redirect or external reference datastreams (controlgroup
     * E,M,R).
     * 
     * @see #modifyDatastreamByReference(String, String, String[], String, String, String, String, String, String, String, boolean)
     * @see DatastreamVersion#setContentLocation(ContentLocation)
     * @see DatastreamVersion#setContentLocation(nl.knaw.dans.common.fedora.fox.ContentLocation.Type, java.net.URI)
     * @param sid
     *        the sid of the object
     * @param version
     *        the new DatastreamVersion with content location set
     * @param logMessage
     *        a log message
     * @param force
     *        Force the update even if it would break a data contract
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByReference(final String sid, final DatastreamVersion version, final String logMessage, final boolean force)
            throws RepositoryException
    {
        final String formatUri = version.getFormatURI() == null ? null : version.getFormatURI().toString();

        return modifyDatastreamByReference(sid, version.getVersionId(), version.getAltIdArray(), version.getLabel(), version.getMimeType(), formatUri,
                version.getDsLocation(), version.getChecksumType(), version.getContentDigest(), logMessage, force);
    }

    /**
     * Modifies an existing Datastream in an object, by value. This operation is only valid for Inline XML Datastreams (i.e. controlGroup "X").
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param altIds
     *        Alternate identifiers for the datastream, if any
     * @param dsLabel
     *        The label for the datastream
     * @param mimeType
     *        The mime type
     * @param formatUri
     *        Optional format URI of the datastream
     * @param dsContent
     *        The content of the datastream
     * @param contentDigestType
     *        The algorithm used to compute the checksum. One of "DEFAULT", "DISABLED", "MD5", "SHA-1", "SHA-256", "SHA-385", "SHA-512", "HAVAL", "TIGER",
     *        "WHIRLPOOL".
     * @param checksum
     *        The value of the checksum represented as a hexadecimal string
     * @param logMessage
     *        A log message
     * @param force
     *        Force the update even if it would break a data contract
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByValue(final String sid, final String streamId, final String[] altIds, final String dsLabel, final String mimeType,
            final String formatUri, final byte[] dsContent, final String contentDigestType, final String checksum, final String logMessage, final boolean force)
            throws RepositoryException
    {
        String modificationTime = null;
        try {
            modificationTime = repository.getFedoraAPIM().modifyDatastreamByValue(sid, streamId, altIds, dsLabel, mimeType, formatUri, dsContent,
                    contentDigestType, checksum, logMessage, force);
            if (logger.isDebugEnabled()) {
                logger.debug("Modified datastream by value. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to modify datastream by value:  sid=" + sid + " streamId=" + streamId + "\ndsContent=\n" + new String(dsContent) + "\n";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(modificationTime);
    }

    /**
     * Modifies an existing Datastream in an object, by value. This operation is only valid for Inline XML Datastreams (i.e. controlGroup "X").
     * 
     * @see #modifyDatastreamByValue(String, String, String[], String, String, String, byte[], String, String, String, boolean)
     * @see DatastreamVersion#setBinaryContent(byte[])
     * @param sid
     *        the sid of the object
     * @param version
     *        the new DatastreamVersion with binary content set
     * @param logMessage
     *        A log message
     * @param force
     *        Force the update even if it would break a data contract
     * @return The timestamp of the operation according to the server
     * @throws nl.knaw.dans.common.lang.repo.exception.RemoteException
     *         as the common base class for remote exceptions
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByValue(final String sid, final DatastreamVersion version, final String logMessage, final boolean force)
            throws RepositoryException
    {
        final String streamId = version.getStreamId();
        final String formatUri = version.getFormatURI() == null ? null : version.getFormatURI().toString();

        return modifyDatastreamByValue(sid, streamId, version.getAltIdArray(), version.getLabel(), version.getMimeType(), formatUri,
                version.getBinaryContent(), version.getChecksumType(), version.getContentDigest(), logMessage, force);
    }

    /**
     * Modifies the Dublin core metadata of an object.
     * 
     * @param sid
     *        the sid of the object
     * @param dcMetadata
     *        the new Dublin core metadata
     * @param logMessage
     *        A log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDublinCoreMetadata(final String sid, final DublinCoreMetadata dcMetadata, final String logMessage) throws RepositoryException {
        final DateTime modificationTime = modifyDatastreamByValue(sid, DublinCoreMetadata.UNIT_ID, DublinCoreMetadata.UNIT_LABEL,
                DublinCoreMetadata.UNIT_FORMAT, dcMetadata, logMessage);
        // dcMetadata.setTimestamp(modificationTime); done by called method
        return modificationTime;
    }

    /**
     * Modifies an existing Datastream in an object, by value. This operation is only valid for Inline XML Datastreams (i.e. controlGroup "X"). This method uses
     * {@link TimestampedMinimalXMLBean} as the carrier of the content.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param label
     *        The label for the datastream. Can be <code>null</code>.
     * @param formatUri
     *        Optional format URI of the datastream
     * @param timestampedXMLBean
     *        the TimestampedXMLBean that carries the content of the new datastream
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws ConcurrentUpdateException
     *         if the timestamp of the TimestampedObject is older than the last registered modification of the datastream in the object
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByValue(final String sid, final String streamId, final String label, final String formatUri,
            final TimestampedMinimalXMLBean timestampedXMLBean, final String logMessage) throws ConcurrentUpdateException, RepositoryException
    {
        byte[] objectXml = null;
        try {
            objectXml = timestampedXMLBean.asObjectXML();
        }
        catch (final XMLSerializationException e) {
            throw new ObjectSerializationException(e);
        }

        return modifyDatastreamByValue(sid, streamId, label, formatUri, timestampedXMLBean, objectXml, logMessage);
    }

    /**
     * Modifies an existing Datastream in an object, by value. This operation is only valid for Inline XML Datastreams (i.e. controlGroup "X"). This method uses
     * a TimestampedObject and a byte array as the carrier of the content.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param label
     *        The label for the datastream. Can be <code>null</code>.
     * @param formatUri
     *        Optional format URI of the datastream
     * @param timestampedObject
     *        the TimestampedObject that carries the content of the new datastream
     * @param objectXml
     *        the new XML content of the datastream
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime modifyDatastreamByValue(final String sid, final String streamId, final String label, final String formatUri,
            final TimestampedObject timestampedObj, final byte[] objectXml, final String logMessage) throws RepositoryException
    {
        final DateTime modificationTime = modifyDatastreamByValue(sid, streamId, null, label, FoxConstants.MIMETYPE_XML, formatUri, objectXml, null, null,
                logMessage, false);
        timestampedObj.setTimestamp(modificationTime);
        timestampedObj.setDirty(false);
        return modificationTime;
    }

    /**
     * Sets the state of a Datastream to the specified state value.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param state
     *        the desired state
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime setDatastreamState(final String sid, final String streamId, final Datastream.State state, final String logMessage)
            throws RepositoryException
    {
        String modificationTime = null;
        try {
            modificationTime = repository.getFedoraAPIM().setDatastreamState(sid, streamId, state.toString(), logMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Changed datastream state. sid=" + sid + " streamId=" + streamId + " state=" + state.toString());
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to set datastream state:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(modificationTime);
    }

    /**
     * Selectively turn versioning on or off for selected datastream. When versioning is disabled, subsequent modifications to the datastream replace the
     * current datastream contents and no versioning history is preserved. To put it another way: No new datastream versions will be made, but all the existing
     * versions will be retained. All changes to the datastream will be to the current version.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param versionable
     *        Enable versioning of the datastream
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime setDatastreamVersionable(final String sid, final String streamId, final boolean versionable, final String logMessage)
            throws RepositoryException
    {
        String modificationTime = null;
        try {
            modificationTime = repository.getFedoraAPIM().setDatastreamVersionable(sid, streamId, versionable, logMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Set datastream versionable. sid=" + sid + " streamId=" + streamId + " versionable=" + versionable);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to set datastream versionable:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(modificationTime);
    }

    /**
     * Permanently removes one or more versions of a Datastream from an object.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        The datastream ID
     * @param startDT
     *        The (inclusive) start date-time stamp of the range. If null, this is taken to be the lowest possible value, and thus, the entire version history
     *        up to the endDT be purged.
     * @param endDT
     *        The (inclusive) ending date-time stamp of the range. If null, this is taken to be the greatest possible value, and thus, the entire version
     *        history back to the startDT will be purged.
     * @param force
     *        Force the update even if it would break a data contract
     * @param logMessage
     *        a log message
     * @return The timestamp of the operation according to the server
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DateTime purgeDatastream(final String sid, final String streamId, final DateTime startDT, final DateTime endDT, final boolean force,
            final String logMessage) throws RepositoryException
    {
        String modificationTime = null;
        final String start = Converter.serializeToXml(startDT);
        final String end = Converter.serializeToXml(endDT);
        try {
            final String[] mTimes = repository.getFedoraAPIM().purgeDatastream(sid, streamId, start, end, logMessage, force);
            modificationTime = mTimes.length > 0 ? mTimes[0] : null;
            if (logger.isDebugEnabled()) {
                logger.debug("Purged datastream. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (final RemoteException e) {
            final String msg = "Unable to purge datastream:  sid=" + sid + " streamId=" + streamId;
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return new DateTime(modificationTime);
    }

}
