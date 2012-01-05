package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.easy.domain.model.FileItem;

public class FileItemFactory extends AbstractDmoFactory<FileItem>
{

    @Override
    public FileItem newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }
    
    @Override
    public FileItem createDmo(String storeId)
    {
        return new FileItemImpl(storeId);
    }

    @Override
    public String getNamespace()
    {
        return FileItem.NAMESPACE;
    }

}
