package nl.knaw.dans.common.fedora;

import static org.junit.Assert.assertNotNull;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.test.Tester;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.Property;

public class DatastreamAccessorOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(DatastreamAccessorOnlineTest.class);

    private static ObjectManager objManager;
    private static DatastreamAccessor dsAccessor;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforelass() throws RepositoryException
    {
        objManager = new ObjectManager(getRepository());
        dsAccessor = new DatastreamAccessor(getRepository());
    }

    @Test
    public void getDatastreamDissemination() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        MIMETypedStream mss = dsAccessor.getDatastreamDissemination(sid, "DC", new DateTime("2073"));
        if (verbose)
        {
            logger.debug("mimeType=" + mss.getMIMEType());
            logger.debug("stream=" + new String(mss.getStream()));
            Property[] props = mss.getHeader();
            if (props != null)
            {
                for (Property prop : props)
                {
                    logger.debug("property: " + prop.getName() + "=" + prop.getValue());
                }
            }
        }

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void getDublinCoreMetadata() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DublinCoreMetadata dcMetadata = dsAccessor.getDublinCoreMetadata(sid, null);
        //assertNotNull(dcMetadata.getTimestamp()); repository.getServerDate() lacks millisecond precision
        assertNotNull(dcMetadata.getFirst(PropertyName.Identifier));

        objManager.purgeObject(sid, false, "cleaning up");
    }

    @Test
    public void listDatastreams() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        DatastreamDef[] streamDefs = dsAccessor.listDatastreams(sid, null);
        for (DatastreamDef def : streamDefs)
        {
            logger.debug("id=" + def.getID());
            logger.debug("label=" + def.getLabel());
            logger.debug("mimType=" + def.getMIMEType());
        }

        objManager.purgeObject(sid, false, "cleaning up");
    }

}
