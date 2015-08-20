package nl.knaw.dans.easy.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class JointMapTest {

    @Test
    public void construct() throws Exception {
        JointMap c = new JointMap();

        c.put("NullObject", null);
        c.put("primitive", 42);
        c.put("a test class", this);
        c.printObjects(System.out);
    }

    @Test
    public void state() {
        JointMap joint = new JointMap();
        assertTrue(joint.isFitForDraft());

        joint.setFitForDraft(false);
        assertFalse(joint.isFitForDraft());

        joint.setFitForDraft(true);
        assertFalse(joint.isFitForDraft());

        joint.clearCycleState();
        assertTrue(joint.isFitForDraft());
    }

}
