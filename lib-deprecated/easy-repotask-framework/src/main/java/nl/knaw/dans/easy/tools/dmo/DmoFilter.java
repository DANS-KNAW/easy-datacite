package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.common.lang.repo.DataModelObject;

public interface DmoFilter<T extends DataModelObject> {

    boolean accept(T dmo);

    /**
     * Provide a readable String representation of the filter.
     * 
     * @return a String containing the filter options.
     */
    String toString();

}
