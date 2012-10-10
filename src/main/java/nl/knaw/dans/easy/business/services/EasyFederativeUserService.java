package nl.knaw.dans.easy.business.services;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;

public class EasyFederativeUserService extends AbstractEasyService implements FederativeUserService
{
    private static Logger       logger              = LoggerFactory.getLogger(EasyFederativeUserService.class);
    private URL federationUrl;
    private boolean federationLoginEnabled;

    @Override
    public EasyUser getUserById(EasyUser sessionUser, String fedUserId) throws ObjectNotAvailableException, ServiceException
    {
        EasyUser user = null;
        String uid = "";
        
        // Find uid from mapping for federated user
        try
        {
            FederativeUserIdMap userIdMap = Data.getFederativeUserRepo().findById(fedUserId);
            uid = userIdMap.getDansUserId();
            logger.debug("Found easy user for federative user: fedUserId='" + fedUserId + "', userId='" + uid + "'");
        }
        catch (ObjectNotInStoreException e)
        {
            logger.debug("Object not found. fedUserId='" + fedUserId + "'");
            throw new ObjectNotAvailableException("Object not found. fedUserId='" + fedUserId + "' :", e);
        }
        catch (RepositoryException e)
        {
            logger.debug("Could not get user with fedUserId '" + fedUserId + "' :", e);
            throw new ServiceException("Could not get user with fedUserId '" + fedUserId + "' :", e);
        }
        
        // get associated EasyUser with that uid
        // NOTE maybe use EasyUserService for that
        // But if we want to handle thing a bit different...
        try
        {
            user = Data.getUserRepo().findById(uid);
            logger.debug("Found user: " + user.toString());
        }
        catch (final ObjectNotInStoreException e)
        {
            // Maybe the mapping is wrong, because if it refers to a easy user, the uid should be OK
            logger.debug("Easy user Object not found. userId='" + uid + "'");
            //throw new ObjectNotAvailableException("Object not found. userId='" + uid + "' :", e);
            throw new ServiceException("Easy user Object not found. userId='" + uid + "' :", e);
        }
        catch (final RepositoryException e)
        {
            logger.debug("Could not get user with id '" + uid + "' :", e);
            throw new ServiceException("Could not get user with id '" + uid + "' :", e);
        }
        
        return user;
    }

    @Override
    public void addFedUserToEasyUserIdCoupling(String fedUserId, String easyUserId) throws ServiceException
    {
        // Overwrite any existing coupling (idMap) for that federation Id
        try
        {
            if (Data.getFederativeUserRepo().exists(fedUserId))
            {
                    Data.getFederativeUserRepo().delete(fedUserId);
                    logger.debug("Removed coupling for federated user Id: " + fedUserId + ", but should replace it with easy Id: " + easyUserId);
            }
        }
        catch (RepositoryException e1)
        {
            logger.debug("Could not add coupling for federated user Id '" + fedUserId + "' :", e1);
            throw new ServiceException("Could not add coupling for federated user Id '" + fedUserId + "' :", e1);
        }
        
        FederativeUserIdMap idMap = new FederativeUserIdMap(fedUserId, easyUserId);
        try
        {
            Data.getFederativeUserRepo().add(idMap);
            logger.debug("Added coupling for federated user Id: " + fedUserId + " with easy Id: " + easyUserId);
        }
        catch (ObjectExistsException e)
        {
            logger.debug("Could not add coupling for federated user Id '" + fedUserId + "' :", e);
            throw new ServiceException("Could not add coupling for federated user Id '" + fedUserId + "' :", e);
        }
        catch (RepositoryException e)
        {
            logger.debug("Could not add coupling for federated user Id '" + fedUserId + "' :", e);
            throw new ServiceException("Could not add coupling for federated user Id '" + fedUserId + "' :", e);
        }
    }

	@Override
	public URL getFederationUrl() {
		return federationUrl;
	}

	public void setFederationUrl(URL federationUrl) {
		this.federationUrl = federationUrl;
	}
	
	public void setFederationLoginEnabled(boolean enabled) {
		federationLoginEnabled = enabled;
	}

	@Override
	public boolean isFederationLoginEnabled() {
		return federationLoginEnabled;
	}
}
