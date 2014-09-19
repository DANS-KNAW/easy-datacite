package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimpleXmlWriterTest {

    @Test(expected = AssertionError.class)
    public void notInstantiable() {
        new DisciplineConverter();
    }

    @Test
    public void addNode() {
        String result = SimpleXmlWriter.addNode("node", "value");
        assertEquals("<node>value</node>", result);
    }

    @Test
    public void startNode() {
        String result = SimpleXmlWriter.startNode("node");
        assertEquals("<node>", result);
    }

    @Test
    public void endNode() {
        String result = SimpleXmlWriter.endNode("node");
        assertEquals("</node>", result);
    }

    @Test
    public void addNodeWithNullValue() {
        String result = SimpleXmlWriter.addNode("node", null);
        assertEquals("<node/>", result);
    }

    @Test
    public void addNodeWithNullNameAndValue() {
        String result = SimpleXmlWriter.addNode(null, null);
        assertEquals("<null/>", result);
    }

    @Test
    public void addNodeWithEmptyValue() {
        String result = SimpleXmlWriter.addNode("node", "");
        assertEquals("<node/>", result);
    }

}
