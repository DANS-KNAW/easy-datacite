package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;

public interface Unit extends Serializable {

    /**
     * Get the string representing the global id of the group of units this Unit is part of.
     * 
     * @return the global id of the group of units this instance is part of
     */
    String getUnitId();

    String getUnitLabel();

    boolean isVersionable();

    void setVersionable(boolean versionable);

}
