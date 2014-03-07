package nl.knaw.dans.common.fedora.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.common.Constants;

public class FedoraRelationsConverterTest
{

    private static final Logger logger = LoggerFactory.getLogger(FedoraRelationsConverterTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeEmptyList() throws Exception
    {
        ArrayList<Relation> relations = new ArrayList<Relation>();

        String rdf = FedoraRelationsConverter.relationsToRdf(relations);
        assertEquals("", rdf);
    }

    @Test
    public void serializeAndDeserialize() throws Exception
    {
        ArrayList<Relation> relations = new ArrayList<Relation>();

        Relation rel1 = new Relation("easy-dataset:1", Constants.MODEL.HAS_MODEL.uri, "fedora-system:EDM1DATASET", false, null);
        Relation rel2 = new Relation("easy-dataset:1", RelsConstants.OAI_ITEM_ID, "foo:1", true, RelsConstants.RDF_LITERAL);
        Relation rel3 = new Relation("easy-dataset:1", RelsConstants.OAI_ITEM_ID, "foo:2", true, null);

        relations.add(rel1);
        relations.add(rel2);
        relations.add(rel3);
        String rdf = FedoraRelationsConverter.relationsToRdf(relations);
        //if (verbose)
        logger.debug("\n" + rdf + "\n");

        Set<Relation> relationSet = FedoraRelationsConverter.rdfToRelations(rdf);
        if (verbose)
            logger.debug("\n" + relationSet + "\n");

        // No rels to content models returned. Strange behavior for a converter.
        assertEquals(3, relationSet.size());
        assertTrue(relationSet.contains(rel2));
        // relation with null datatype not in there:
        assertTrue(relationSet.contains(new Relation("easy-dataset:1", RelsConstants.OAI_ITEM_ID, "foo:2", true, RelsConstants.RDF_LITERAL)));
    }

    @Test
    public void nullAllowedAsDataType() throws Exception
    {
        ArrayList<Relation> relations = new ArrayList<Relation>();
        relations.add(new Relation("easy-dataset:1", RelsConstants.OAI_ITEM_ID, "foo:1", true, null));
        FedoraRelationsConverter.relationsToRdf(relations);
    }

    @Test(expected = Exception.class)
    public void deserializeNull() throws Exception
    {
        FedoraRelationsConverter.rdfToRelations(null);
    }

    @Test(expected = Exception.class)
    public void deserializeEmpty() throws Exception
    {
        FedoraRelationsConverter.rdfToRelations("");
    }

}
