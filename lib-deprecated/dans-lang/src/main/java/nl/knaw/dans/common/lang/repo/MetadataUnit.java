package nl.knaw.dans.common.lang.repo;

import java.net.URI;

/**
 * A MetadataUnit is a minimal XMLBean capable of describing itself.
 * 
 * @author ecco Oct 9, 2009
 */
public interface MetadataUnit extends Unit, TimestampedMinimalXMLBean
{

    String getUnitFormat();

    URI getUnitFormatURI();

}
