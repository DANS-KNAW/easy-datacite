package nl.knaw.dans.common.lang.repo.relations;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RelationTest
{

    @Test
    public void equals()
    {
        Relation a = new Relation("bla", "foo", "bar", false, null);
        Relation b = new Relation(RelsConstants.getObjectURI("bla"), RelsConstants.getObjectURI("foo"), RelsConstants.getObjectURI("bar"), false, null);

        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

}
