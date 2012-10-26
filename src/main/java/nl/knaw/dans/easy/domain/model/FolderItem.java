package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DmoNamespace;

public interface FolderItem extends DatasetItem, DatasetItemContainer
{
    DmoNamespace NAMESPACE = new DmoNamespace("easy-folder");
}
