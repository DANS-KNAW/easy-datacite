package nl.knaw.dans.common.lang.repo.jumpoff;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;

public class JumpoffDmoFactory extends AbstractDmoFactory<JumpoffDmo>
{

    @Override
    public DmoNamespace getNamespace()
    {
        return JumpoffDmo.NAMESPACE;
    }

    @Override
    public JumpoffDmo newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }

    @Override
    public JumpoffDmo createDmo(String storeId)
    {
        return new JumpoffDmo(storeId);
    }

}
