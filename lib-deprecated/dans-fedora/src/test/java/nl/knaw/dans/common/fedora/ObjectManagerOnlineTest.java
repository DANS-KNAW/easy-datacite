package nl.knaw.dans.common.fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.fedora.ObjectManager.ExportContext;
import nl.knaw.dans.common.fedora.fox.AuditTrail;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.fedora.fox.DublinCoreMetadataTest;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectManagerOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(ObjectManagerOnlineTest.class);

    private static ObjectManager manager;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass() throws RepositoryException
    {
        manager = new ObjectManager(getRepository());
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void getObjectXml() throws RepositoryException
    {
        manager.getObjectXML("na:221");
    }

    @Test
    public void nextSid() throws RepositoryException
    {
        String sid = manager.nextSid("foo");
        if (verbose)
            logger.debug("Next impossible sid is " + sid);
        // retainPIDs is inactive since...?
    }

    @Test
    public void ingestGetPurge() throws RepositoryException, XMLException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Deleted, "foo-state");
        //if (verbose)
        logger.debug("before ingest\n" + dob0.asXMLString(4));

        String sid = manager.ingest(dob0, "test ingest");

        byte[] objectXml = manager.getObjectXML(sid);
        System.err.println(new String(objectXml));

        assertEquals(sid, dob0.getSid());
        assertTrue(dob0.getSid().startsWith("foo"));

        DigitalObject dob1 = manager.getDigitalObject(sid);
        //if (verbose)
        logger.debug(dob1.asXMLString(4));
        DublinCoreMetadata dc = dob1.getLatestDublinCoreMetadata();
        assertEquals(sid, dc.getFirst(PropertyName.Identifier));
        assertNotNull(dc.getTimestamp());
        DatastreamVersion version = dob1.getLatestVersion(DublinCoreMetadata.UNIT_ID);
        assertEquals(dc.getTimestamp(), version.getTimestamp());

        // ingest returned dob.
        // if we do not set the objectNamespace it will be ingested as
        // changeme:xxx
        dob1.setSid(null);
        dob1.setObjectNamespace("foo");
        String sid1 = manager.ingest(dob1, "test ingest returned dob");
        assertEquals(sid1, dob1.getSid());

        DigitalObject dob2 = manager.getDigitalObject(sid1);
        if (verbose)
            logger.debug(dob2.asXMLString(4));
        dc = dob2.getLatestDublinCoreMetadata();
        assertEquals(2, dc.get(PropertyName.Identifier).size());
        AuditTrail auditTrail = dob2.getAuditTrail();
        if (verbose)
            logger.debug("\n" + auditTrail.asXMLString(4) + "\n");

        DateTime time = manager.purgeObject(dob0, false, "test purge");
        assertNotNull(time);
        time = manager.purgeObject(dob1, false, "test purge second ingest");
        assertNotNull(time);
    }

    @Test(expected = ObjectExistsException.class)
    public void ingestWithExistingSid() throws RepositoryException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Active, "foo");
        String sid = manager.ingest(dob0, "test ingest");
        assertEquals(sid, dob0.getSid());

        try
        {
            manager.ingest(dob0, "test ingest with existing sid");
        }
        catch (RepositoryException e)
        {
            manager.purgeObject(sid, false, "cleaning up");
            throw e;
        }
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void purgeNonExistingObject() throws RepositoryException
    {
        manager.purgeObject("na:123", false, "should not be there");
    }

    @Test
    public void modifyObject() throws RepositoryException, XMLSerializationException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Active, "foo");
        dob0.setOwnerId("fooBar");
        dob0.setLabel("label before test");
        String sid = manager.ingest(dob0, "test ingest");

        DigitalObject dob1 = manager.getDigitalObject(sid);
        if (verbose)
            logger.debug(dob1.asXMLString(4));
        assertEquals(DobState.Active, dob1.getDigitalObjectState());
        assertEquals("label before test", dob0.getLabel());
        assertEquals("fooBar", dob1.getOwnerId());

        DateTime time = manager.modifyObject(sid, DobState.Inactive, "label after test", "barFoo", "test modify");
        assertNotNull(time);

        DigitalObject dob2 = manager.getDigitalObject(sid);
        if (verbose)
            logger.debug(dob2.asXMLString(4));
        assertEquals(DobState.Inactive, dob2.getDigitalObjectState());
        assertEquals("label after test", dob2.getLabel());
        assertEquals("barFoo", dob2.getOwnerId());

        manager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void modifyObject2() throws RepositoryException, XMLSerializationException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Active, "foo");
        dob0.setOwnerId("fooBar");
        dob0.setLabel("label before test");
        String sid = manager.ingest(dob0, "test ingest");

        DigitalObject dob1 = manager.getDigitalObject(sid);
        if (verbose)
            logger.debug(dob1.asXMLString(4));
        assertEquals(DobState.Active, dob1.getDigitalObjectState());
        assertEquals("label before test", dob0.getLabel());
        assertEquals("fooBar", dob1.getOwnerId());

        dob1.setDigitalObjectState(DobState.Inactive);
        dob1.setLabel("label after test");
        dob1.setOwnerId("barFoo");
        DateTime time = manager.modifyObject(dob1, "test modify");
        assertNotNull(time);

        DigitalObject dob2 = manager.getDigitalObject(sid);
        if (verbose)
            logger.debug(dob2.asXMLString(4));
        assertEquals(DobState.Inactive, dob2.getDigitalObjectState());
        assertEquals("label after test", dob2.getLabel());
        assertEquals("barFoo", dob2.getOwnerId());

        manager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void exportAtom11() throws XMLSerializationException, RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        dob.setOwnerId("atom 1.1");
        dob.setLabel("label test atom 1.1");
        dob.addDatastreamVersion(DublinCoreMetadataTest.createFull());
        String sid = manager.ingest(dob, "test export");

        byte[] atom = manager.export(sid, ObjectManager.EXPORT_FORMAT_ATOM_ZIP1_1, ExportContext.MIGRATE);
        if (verbose)
            logger.debug("\n" + new String(atom));

        manager.purgeObject(sid, false, "cleaning up");
    }

}
