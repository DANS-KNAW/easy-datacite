package nl.knaw.dans.easy;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

public class EasyUserTestImpl extends EasyUserImpl
{
    private static final long serialVersionUID = 1L;

    public EasyUserTestImpl(String userId)
    {
        super(userId);
    }

    public Set<Group> getGroups()
    {
        return new HashSet<Group>();
    }
}
