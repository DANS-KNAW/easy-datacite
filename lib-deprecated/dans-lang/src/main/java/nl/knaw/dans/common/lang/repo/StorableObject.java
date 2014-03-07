package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.TimestampedObject;

import org.joda.time.DateTime;

/**
 * 
 *
 */
public interface StorableObject extends TimestampedObject
{

    String getStoreId();

    void setStoreId(String storeId);

    String getLabel();

    void setLabel(String label);

    String getOwnerId();

    void setOwnerId(String ownerId);

    String getState();

    void setState(String state);

    DateTime getDateCreated();

    void setDateCreated(DateTime dateCreated);

    DateTime getLastModified();

    void setlastModified(DateTime lastModified);

    void setLoaded(boolean loaded);

    boolean isLoaded();

    /**
     * @return the time that the object was loaded in system nanotime
     */
    long getloadTime();
}
