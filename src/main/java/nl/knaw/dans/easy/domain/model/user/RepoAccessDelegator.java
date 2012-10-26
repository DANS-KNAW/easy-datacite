package nl.knaw.dans.easy.domain.model.user;

import java.util.Collection;
import java.util.List;

/**
 * DelegatorPattern for RepoAccess.
 * <p/>
 * MARK THAT: <br/>
 * The overall logic of business processes and access to stores and repositories remains situated in the business layer
 * so the RepoAccessDelegator should be confined to simple getter-methods like 'getUser', 'getGroups', etc.
 * 
 * @author ecco Nov 19, 2009
 */
public interface RepoAccessDelegator
{

    EasyUser getUser(String userId);

    List<Group> getGroups(Collection<String> groupIds);

    List<Group> getGroups(EasyUser user);

}
