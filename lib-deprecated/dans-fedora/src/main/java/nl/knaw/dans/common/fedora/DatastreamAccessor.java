package nl.knaw.dans.common.fedora;

import java.rmi.RemoteException;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.jibx.util.Converter;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;

public class DatastreamAccessor
{

    private static final Logger logger = LoggerFactory.getLogger(DatastreamAccessor.class);

    private final Repository repository;

    /**
     * Constructs a new DatastreamAccessor with the given Repository as target.
     * 
     * @param repository
     *        Repository to access
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DatastreamAccessor(Repository repository)
    {
        this.repository = repository;
    }

    /**
     * Gets the content of a datastream.
     * 
     * @param sid
     *        the sid of the object
     * @param streamId
     *        the datastream id
     * @param asOfDateTime
     *        A dateTime indicating the version of the datastream to retrieve. If null, Fedora will use the most recent
     *        version.
     * @return MIMETypedStream
     *         <ul>
     *         <li>String MIMEType The mimetype of the stream</li>
     *         <li>byte[] stream The contents of the Stream</li>
     *         <li>Property[] header The header will be empty, or if applicable, contain the http header as name/value
     *         pairs.</li>
     *         <ul>
     *         <li>String name</li>
     *         <li>String value</li>
     *         </ul>
     *         </ul>
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public MIMETypedStream getDatastreamDissemination(String sid, String streamId, DateTime asOfDateTime) throws RepositoryException
    {
        MIMETypedStream mimeTypedStream = null;
        String asOfDateTimeString = Converter.serializeToXml(asOfDateTime);
        try
        {
            mimeTypedStream = repository.getFedoraAPIA().getDatastreamDissemination(sid, streamId, asOfDateTimeString);
            if (logger.isDebugEnabled())
            {
                logger.debug("Got datastream dissemination. sid=" + sid + " streamId=" + streamId);
            }
        }
        catch (RemoteException e)
        {
            String msg = "Unable to get datastream dissemination: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return mimeTypedStream;
    }

    /**
     * Get the DublinCoreMetadata of an object.
     * 
     * @param sid
     *        the sid of the object
     * @param asOfDateTime
     *        A dateTime indicating the version of the datastream to retrieve. If null, Fedora will use the most recent
     *        version.
     * @return DublinCoreMetadata
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public DublinCoreMetadata getDublinCoreMetadata(String sid, DateTime asOfDateTime) throws RepositoryException
    {
        DublinCoreMetadata dcMetadata = null;
        MIMETypedStream mts = getDatastreamDissemination(sid, DublinCoreMetadata.UNIT_ID, asOfDateTime);
        try
        {
            dcMetadata = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, mts.getStream());
            //dcMetadata.setTimestamp(repository.getServerDate()); lacks millisecond precision
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
        return dcMetadata;
    }

    public DatastreamDef[] listDatastreams(String sid, DateTime asOfDateTime) throws RepositoryException
    {
        DatastreamDef[] streamDefs = null;
        String asOfDateTimeString = Converter.serializeToXml(asOfDateTime);
        try
        {
            streamDefs = repository.getFedoraAPIA().listDatastreams(sid, asOfDateTimeString);
            if (logger.isDebugEnabled())
            {
                logger.debug("Got datastream definitions. sid=" + sid);
            }
        }
        catch (RemoteException e)
        {
            String msg = "Unable to list datastream definitions: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return streamDefs;
    }

}
