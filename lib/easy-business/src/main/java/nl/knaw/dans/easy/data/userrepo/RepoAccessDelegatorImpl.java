package nl.knaw.dans.easy.data.userrepo;

import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.RepoAccessDelegator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepoAccessDelegatorImpl implements RepoAccessDelegator {

    private static final Logger logger = LoggerFactory.getLogger(RepoAccessDelegatorImpl.class);

    public RepoAccessDelegatorImpl() {
        logger.debug("Constructed " + this.getClass().getName());
    }

    public List<Group> getGroups(EasyUser user) {
        return getGroups(user.getGroupIds());
    }

    public List<Group> getGroups(Collection<String> groupIds) {
        try {
            return Data.getGroupRepo().findById(groupIds);
        }
        catch (ObjectNotInStoreException e) {
            logger.error("Could not instaniate (all of) the groups in the collection: ", e);
            throw new ApplicationException(e);
        }
        catch (RepositoryException e) {
            logger.error("Could not instaniate (all of) the groups in the collection: ", e);
            throw new ApplicationException(e);
        }
    }

    public EasyUser getUser(String userId) {
        try {
            return Data.getUserRepo().findById(userId);
        }
        catch (RepositoryException e) {
            logger.error("Could not instantiate the User with userId '" + userId + "': ", e);
            throw new ApplicationException(e);
        }
    }

}
