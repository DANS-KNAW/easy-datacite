package nl.knaw.dans.common.fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.RelationshipTuple;

public class RelationshipManagerOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(RelationshipManagerOnlineTest.class);

    private static ObjectManager objManager;
    private static RelationshipManager relManager;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        objManager = new ObjectManager(getRepository());
        relManager = new RelationshipManager(getRepository());
    }

    @Test
    public void addGetPurge() throws ObjectExistsException, RepositoryException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Active, "test");
        String sid = objManager.ingest(dob0, "test ingest");

        String subject = sid;
        //String subject = "info:fedora/" + sid; // > ObjectNotInLowlevelStorageException

        RelationshipTuple[] tuples = relManager.getRelationShips(subject, null);
        assertEquals(1, tuples.length);

        boolean addRel = relManager.addRelationship(subject, "http://foo.org#isA", "testObject", true, RelsConstants.RDF_LITERAL);
        assertTrue(addRel);

        tuples = relManager.getRelationShips(subject, null);
        if (verbose)
            printTuples(tuples);
        assertEquals(2, tuples.length);

        boolean purgeRel = relManager.purgeRelationship(subject, "http://foo.org#isA", "testObject", true, RelsConstants.RDF_LITERAL);
        assertTrue(purgeRel);

        tuples = relManager.getRelationShips(subject, null);
        if (verbose)
            printTuples(tuples);
        assertEquals(1, tuples.length);

        objManager.purgeObject(sid, false, "toedeledoki");
    }

    @Test
    public void addGetNonLiteral() throws ObjectExistsException, RepositoryException
    {
        DigitalObject dob0 = new DigitalObject(DobState.Active, "test");
        String sid = objManager.ingest(dob0, "test ingest");

        String subject = sid;

        boolean addRel = relManager.addRelationship(subject, "http://foo.org#isMasterOf", "info:fedora/test:123", false, null);
        assertTrue(addRel);

        RelationshipTuple[] tuples = relManager.getRelationShips(subject, "http://foo.org#isMasterOf");
        if (verbose)
            printTuples(tuples);

        // switch to other abstraction
        String storeId = subject;
        List<Relation> relations = relManager.getRelations(storeId, "http://foo.org#isMasterOf");
        Relation relation = relations.get(0);
        assertEquals(tuples[0].getDatatype(), relation.getDatatype());
        assertEquals(tuples[0].getObject(), relation.getObject());
        assertEquals(tuples[0].getPredicate(), relation.getPredicate());
        assertEquals(tuples[0].isIsLiteral(), relation.isLiteral());
        assertEquals(RelsConstants.FEDORA_URI + storeId, relation.getSubject());
        assertEquals(RelsConstants.FEDORA_URI + "test:123", relation.getObject());
        if (verbose)
            logger.debug("relation:\n" + relation + "\n");

        objManager.purgeObject(sid, false, "toedeledoki");
    }

    private void printTuples(RelationshipTuple[] tuples)
    {
        StringBuilder sb = new StringBuilder("\n");
        for (RelationshipTuple tuple : tuples)
        {
            sb
                    .append(tuple.getSubject() + " | " + tuple.getPredicate() + " | " + tuple.getObject() + " | " + tuple.isIsLiteral() + " | "
                            + tuple.getDatatype());
            sb.append("\n");
        }
        logger.debug(sb.toString());
    }

}
