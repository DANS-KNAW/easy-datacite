package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.domain.model.FolderItem;

public class FolderItemFactory extends AbstractDmoFactory<FolderItem>
{

    @Override
    public FolderItem newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }
    
    @Override
    public FolderItem createDmo(String storeId)
    {
        return new FolderItemImpl(storeId);
    }

    @Override
    public DmoNamespace getNamespace()
    {
        return FolderItem.NAMESPACE;
    }

}
