package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

public interface DmoProcessor<T extends DataModelObject> {

    void process(T dmo) throws TaskExecutionException;

    boolean hasChangedDmo();

}
