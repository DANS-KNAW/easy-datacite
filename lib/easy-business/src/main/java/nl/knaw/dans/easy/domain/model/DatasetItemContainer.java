package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public interface DatasetItemContainer extends DataModelObject {
    void addFileOrFolder(DatasetItem item) throws DomainException;
}
