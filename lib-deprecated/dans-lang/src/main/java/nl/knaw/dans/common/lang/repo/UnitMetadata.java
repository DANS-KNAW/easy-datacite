package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * 
 *
 */
public interface UnitMetadata extends Serializable
{

    DateTime getCreationDate();

    String getId();

    String getVersionId();

    long getSize();

    String getLabel();

    String getMimeType();

    String getLocation();

}
