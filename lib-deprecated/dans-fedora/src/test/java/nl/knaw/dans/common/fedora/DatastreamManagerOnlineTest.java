package nl.knaw.dans.common.fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import nl.knaw.dans.common.fedora.fox.ContentDigestType;
import nl.knaw.dans.common.fedora.fox.ContentLocation.Type;
import nl.knaw.dans.common.fedora.fox.ControlGroup;
import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.Datastream.State;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DigitalObjectProperties;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.fedora.fox.DublinCoreMetadataTest;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.RemoteException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastreamManagerOnlineTest extends AbstractRepositoryOnlineTest
{
    private static final Logger logger = LoggerFactory.getLogger(DatastreamManagerOnlineTest.class);

    private static ObjectManager objManager;
    private static DatastreamManager dsManager;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforelass() throws RepositoryException
    {
        objManager = new ObjectManager(getRepository());
        dsManager = new DatastreamManager(getRepository());
    }

    @Test
    public void addDatastream() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        String streamId = "DS";
        String[] altIds = {"een:1", "twee:2"};
        String dsLabel = "The datastream label";
        boolean versionable = false;
        String mimeType = "text/xml";
        String formatURI = "http://format.com";
        String dsLocation = "http://localhost:8082/fedoragsearch/rest";
        ControlGroup controlGroup = ControlGroup.E;
        State dsState = State.A;
        String contentDigestType = ContentDigestType.MD5.code;
        String checksum = null;// "4551b17f2380a3be999c57fbc0b274f7";
        String logMessage = "testing addDatastream";

        dsManager.addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsLocation, controlGroup, dsState, contentDigestType,
                checksum, logMessage);

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void addDatastreamWithDatastream() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        Datastream datastream = new Datastream("DS", ControlGroup.E);
        DatastreamVersion version = datastream.addDatastreamVersion(null, "text/plain");
        version.setContentLocation(Type.URL, URI.create("http://localhost:8082/fedoragsearch/rest"));

        dsManager.addDatastream(sid, datastream, "testing addDatastream with object");

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void addDatastreamWithJiBXObject() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DigitalObjectProperties props = new DigitalObjectProperties();
        props.setLabel("Created arbitrary object in order to have a JiBXObject");

        String streamId = "DS";
        String[] altIds = null;
        String dsLabel = "Add datastream as inline xml. content is JiBXObject";
        boolean versionable = false;
        String mimeType = "text/xml";
        String formatURI = "http://format.com";
        ControlGroup controlGroup = ControlGroup.X;
        State dsState = State.A;
        String contentDigestType = ContentDigestType.MD5.code;
        String checksum = null;
        String logMessage = "testing addDatastream JiBXObject";

        dsManager.addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, props, controlGroup, dsState, contentDigestType, checksum,
                logMessage);

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void addDatastreamIOStream() throws RepositoryException, XMLSerializationException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DigitalObjectProperties props = new DigitalObjectProperties();
        props.setLabel("Created arbitrary object in order to get an Inputstream");
        InputStream inStream = props.asXMLInputStream();

        String streamId = "DS";
        String[] altIds = null;
        String dsLabel = "Add datastream as inline xml";
        boolean versionable = false;
        String mimeType = "text/xml";
        String formatURI = "http://format.com";
        InputStream dsContent = inStream;
        ControlGroup controlGroup = ControlGroup.X;
        State dsState = State.A;
        String contentDigestType = ContentDigestType.MD5.code;
        String checksum = null;
        String logMessage = "testing addDatastream IO";

        dsManager.addDatastream(sid, streamId, altIds, dsLabel, versionable, mimeType, formatURI, dsContent, controlGroup, dsState, contentDigestType,
                checksum, logMessage);

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void modifyDatastreamRelsExt() throws Exception
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        dob.setSid("test:rel2");
        if (verbose)
            System.err.println(dob.asXMLString(4));
        String sid = objManager.ingest(dob, "ingest for test");
        try
        {

            File file = Tester.getFile("test-files/rels_ext.xml");
            //String streamId = "AMD";
            String streamId = "RELS-EXT";
            String dsLabel = "The datastream label";
            String mimeType = "text/xml";
            //String formatURI = "info:fedora/fedora-system:FedoraRELSExt-1.0";
            String formatURI = "http://co.foo/bar";
            String[] altIds = {};
            ControlGroup controlGroup = ControlGroup.X;
            State dsState = State.A;
            String contentDigestType = ContentDigestType.DISABLED.code;
            String checksum = null;// "4551b17f2380a3be999c57fbc0b274f7";
            String logMessage = "testing addRel";
            byte[] dsContent = FileUtil.readFile(file);

            dsManager.addDatastream(sid, streamId, altIds, dsLabel, false, mimeType, formatURI, file, controlGroup, dsState, contentDigestType, checksum,
                    logMessage);

            dsManager.modifyDatastreamByValue(sid, streamId, altIds, dsLabel, mimeType, formatURI, dsContent, contentDigestType, checksum, logMessage, false);

            byte[] objXml = objManager.getObjectXML(sid);
            if (verbose)
                logger.debug("\n" + new String(objXml) + "\n");
        }
        finally
        {
            objManager.purgeObject(sid, false, "cleaning up");
        }
    }

    @Test
    public void compareDatastreamChecksum() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        Datastream datastream = new Datastream("DS", ControlGroup.E);
        DatastreamVersion version = datastream.addDatastreamVersion(null, "text/plain");
        version.setContentLocation(Type.URL, URI.create("http://localhost:8080/fedora/search"));
        version.setContentDigest(ContentDigestType.MD5, "b75075ebf109feeb884061da472bdc30");

        dsManager.addDatastream(sid, datastream, "testing compareDatastreamChecksum");

        String message = dsManager.compareDatastreamChecksum(sid, "DS", null);
        if (verbose)
            logger.debug(message);

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void getDatastream() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        fedora.server.types.gen.Datastream ds = dsManager.getDatastream(sid, "DC", null);

        if (verbose)
        {
            logger.debug("checksum=" + ds.getChecksum());
            logger.debug("checksumType=" + ds.getChecksumType());
            logger.debug("createDate=" + ds.getCreateDate());
            logger.debug("formatURI=" + ds.getFormatURI());
            logger.debug("id=" + ds.getID());
            logger.debug("label=" + ds.getLabel());
            logger.debug("location=" + ds.getLocation());
            logger.debug("mimeType=" + ds.getMIMEType());
            logger.debug("size=" + ds.getSize());
            logger.debug("state=" + ds.getState());
            logger.debug("versionId=" + ds.getVersionID());
            logger.debug("versionable=" + ds.isVersionable());
            logger.debug("altIds=" + ds.getAltIDs());
            logger.debug("controlGroup=" + ds.getControlGroup());
        }

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void modifyDatastreamByReference() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        Datastream datastream = new Datastream("DS", ControlGroup.E);
        DatastreamVersion version = datastream.addDatastreamVersion(null, "text/plain");
        version.setContentLocation(Type.URL, URI.create("http://localhost:8082/fedoragsearch/rest"));
        //version.setContentDigest(ContentDigestType.MD5, "4551b17f2380a3be999c57fbc0b274f7");

        dsManager.addDatastream(sid, datastream, "creating datastream");

        DatastreamVersion newVersion = new DatastreamVersion("DS", "text/plain");
        newVersion.setContentLocation(Type.URL, URI.create("http://localhost:8080/fedora/search"));
        newVersion.setContentDigest(ContentDigestType.MD5, "b75075ebf109feeb884061da472bdc30");

        dsManager.modifyDatastreamByReference(sid, newVersion, "test mbr", false);

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void modifyDatastreamByValue() throws RepositoryException, XMLException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DatastreamVersion newVersion = new DatastreamVersion("DC.22", null);
        newVersion.setBinaryContent(DublinCoreMetadataTest.createFull().asObjectXML());

        dsManager.modifyDatastreamByValue(sid, newVersion, "test mbv", false);

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        // let's see if the timestamps are set correctly
        DigitalObject dob2 = objManager.getDigitalObject(sid);
        // not so fast! mostly No rows were returned while querying last modification time
        // DateTime objLastModified = getRepository().getLastModified(sid);
        assertEquals(2, dob2.getDatastream("DC").getDatastreamVersions().size());
        DatastreamVersion version0 = dob2.getDatastream("DC").getDatastreamVersions().get(0);
        DatastreamVersion version1 = dob2.getDatastream("DC").getDatastreamVersions().get(1);
        assertSame(version1, dob2.getLatestVersion("DC"));
        assertTrue(version0.getCreated().compareTo(version1.getCreated()) < 0);
        assertTrue(version0.getTimestamp().compareTo(version1.getTimestamp()) == 0);
        DublinCoreMetadata dcLatest = dob2.getLatestDublinCoreMetadata();
        assertTrue(version0.getTimestamp().compareTo(dcLatest.getTimestamp()) == 0);

        dcLatest.add(PropertyName.Title, "A new title");
        dsManager.modifyDublinCoreMetadata(sid, dcLatest, "Add title");

        // This will cause a ConcurrentUpdateException eventually, if Fedora is not too lazy
        // for (int i = 0; i < 10; i++)
        // {
        // try
        // {
        // Thread.sleep(10000);
        // }
        // catch (InterruptedException e1)
        // {
        // //
        // }
        // dcLatest.add(PropertyName.Title, "A new title #" + i);
        // dsManager.modifyDublinCoreMetadata(sid, dcLatest, "Add title #" + i);
        // }

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Ignore("If Fedora is not ready with ingest process, no rows are returned.")
    @Test(expected = ConcurrentUpdateException.class)
    public void modifyDatastreamByValueConcurrent() throws RepositoryException, XMLSerializationException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        try
        {
            Thread.sleep(10000); // even 10 secs will not always help.
        }
        catch (InterruptedException e1)
        {
            //
        }
        DatastreamVersion newVersion = new DatastreamVersion("DC", "text/xml");
        newVersion.setBinaryContent(DublinCoreMetadataTest.createFull().asObjectXML());
        newVersion.setTimestamp("1977");

        try
        {
            dsManager.modifyDatastreamByValue(sid, newVersion, "test mbv", false);
        }
        finally
        {
            objManager.purgeObject(sid, false, "cleaning up");
        }
    }

    @Test
    public void modifyDublinCoreMetadata() throws RepositoryException, XMLException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DublinCoreMetadata dc = DublinCoreMetadataTest.createFull();
        dsManager.modifyDublinCoreMetadata(sid, dc, "test modifyDublinCoreMetadata");

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        DigitalObject dob2 = objManager.getDigitalObject(sid);
        DublinCoreMetadata dc2 = dob2.getLatestDublinCoreMetadata();

        //dc:identifier is set by fedora 3.5
        dc.getXl(PropertyName.Identifier).clear();
        dc2.getXl(PropertyName.Identifier).clear();
        assertEquals(dc.asXMLString(), dc2.asXMLString());

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void modifyDatastreamByValueWithJiBXObject() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DigitalObjectProperties props = new DigitalObjectProperties();
        props.setLabel("Just an arbitrary JiBXObject");

        dsManager.modifyDatastreamByValue(sid, "DC", "I'm the label", null, props, "modify the DublinCore with strange object");

        byte[] objXml = objManager.getObjectXML(sid);
        if (verbose)
            logger.debug("\n" + new String(objXml) + "\n");

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test(expected = RemoteException.class)
    public void purgeDatastream() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        try
        {
            dsManager.purgeDatastream(sid, "DC", null, null, false, "Can we purge DC?");
        }
        finally
        {
            objManager.purgeObject(sid, false, "cleaning up");
        }
    }

    @Test(expected = RemoteException.class)
    public void purgeDatastream2() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        try
        {
            dsManager.purgeDatastream(sid, "DC", null, null, true, "Can we purge DC?");
        }
        finally
        {
            objManager.purgeObject(sid, false, "cleaning up");
        }
    }

    @Test
    public void getDataStreams() throws Exception
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");
        fedora.server.types.gen.Datastream[] datatStreams = dsManager.getDatastreamHistory(sid, "DC");
        for (fedora.server.types.gen.Datastream ds : datatStreams)
        {
            if (verbose)
                System.err.println(ds.getCreateDate() + " " + ds.getID() + " " + ds.getSize() + " " + ds.getVersionID() + " " + ds.getLocation());
        }

        objManager.purgeObject(sid, false, "cleaning up");
    }
}
