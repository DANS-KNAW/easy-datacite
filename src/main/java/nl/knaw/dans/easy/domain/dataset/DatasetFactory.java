package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.domain.model.Dataset;

public class DatasetFactory extends AbstractDmoFactory<Dataset>
{

    @Override
    public Dataset newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }

    @Override
    public Dataset createDmo(String storeId)
    {
        return new DatasetImpl(storeId);
    }

    @Override
    public DmoNamespace getNamespace()
    {
        return Dataset.NAMESPACE;
    }

}
