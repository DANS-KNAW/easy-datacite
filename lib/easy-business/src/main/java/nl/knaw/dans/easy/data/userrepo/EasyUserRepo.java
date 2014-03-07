package nl.knaw.dans.easy.data.userrepo;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.UserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

/**
 * A Data Access Point providing access to the domain object {@link EasyUser}.
 * 
 * @author ecco
 */
public interface EasyUserRepo extends UserRepo<EasyUser>
{

    List<EasyUser> findByRole(Role role) throws RepositoryException;

}
