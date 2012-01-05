package nl.knaw.dans.easy.domain.model.disciplinecollection;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;

public class DisciplineContainerFactory extends AbstractDmoFactory<DisciplineContainer>
{

    @Override
    public DisciplineContainer newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }
    
    @Override
    public DisciplineContainer createDmo(String storeId)
    {
        return new DisciplineContainerImpl(storeId);
    }

    @Override
    public String getNamespace()
    {
        return DisciplineContainer.NAMESPACE;
    }

}
