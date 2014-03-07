package nl.knaw.dans.common.lang.xml;

import java.io.Serializable;

/**
 * Minimal XML bean is an object that can return an XML representation of itself. It is minimal in that
 * sense that is has only the one method that is needed to accomplish that goal. For a richer interface
 * see XMLBean
 * 
 * @see XMLBean
 * @author lobo Apr 1, 2010
 */
public interface MinimalXMLBean extends Serializable
{
    /**
     * Serialize this bean to a byte array.
     * 
     * @return bean as byte array
     * @throws XMLSerializationException
     *         if something goes wrong
     */
    byte[] asObjectXML() throws XMLSerializationException;
}
