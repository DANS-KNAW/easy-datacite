package nl.knaw.dans.easy.security.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.i.security.SecurityAgent;

public class SecurityOficerAdapter implements SecurityAgent
{

    private static final Logger logger = LoggerFactory.getLogger(SecurityOficerAdapter.class);

    private final String securityId;
    private final SecurityOfficer officer;

    public SecurityOficerAdapter(String securityId, SecurityOfficer officer)
    {
        this.securityId = securityId;
        this.officer = officer;
    }

    @Override
    public String getSecurityId()
    {
        return securityId;
    }

    @Override
    public boolean isAllowed(String ownerId, Object... args)
    {
        EasyUser sessionUser = null;
        if (ownerId != null)
        {
            try
            {
                sessionUser = Data.getUserRepo().findById(ownerId);
            }
            catch (ObjectNotInStoreException e)
            {
                logger.warn("SessionUser with id " + ownerId + " not found in repository. " + "Returning not allowed for secured operation " + securityId, e);
                return false;
            }
            catch (RepositoryException e)
            {
                logger.warn("Could not get sessionUser with id " + ownerId + " from repository.");
                throw new ApplicationException(e);
            }
        }
        ContextParameters ctxParameters = new ContextParameters(sessionUser, args);
        boolean allowed = officer.isEnableAllowed(ctxParameters);
        if (!allowed)
        {
            logger.info(officer.explainEnableAllowed(ctxParameters));
        }
        return allowed;
    }

}
