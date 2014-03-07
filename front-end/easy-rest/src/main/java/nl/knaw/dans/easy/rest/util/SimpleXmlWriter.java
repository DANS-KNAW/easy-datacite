package nl.knaw.dans.easy.rest.util;

/**
 * Simple writer class for XML.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class SimpleXmlWriter
{

    /**
     * Throw an AssertionError if this class or one of it's subclasses is ever instantiated.
     */
    protected SimpleXmlWriter()
    {
        throw new AssertionError("Instantiating utility class...");
    }

    /**
     * Create a start node.
     * 
     * @param name
     *        Node name.
     * @return Starting node.
     */
    public static String startNode(String name)
    {
        return "<" + name + ">";
    }

    /**
     * Create an ending node.
     * 
     * @param name
     *        Node name.
     * @return Ending node.
     */
    public static String endNode(String name)
    {
        return "</" + name + ">";
    }

    /**
     * Create a simple node with the given node name and value.
     * 
     * @param nodeName
     *        Name of the node.
     * @param value
     *        Value of the node.
     * @return The node with it's value.
     */
    public static String addNode(String nodeName, String value)
    {
        return value != null && value.length() > 0 ? "<" + nodeName + ">" + value + "</" + nodeName + ">" : "<" + nodeName + "/>";
    }

}
