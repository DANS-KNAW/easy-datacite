package nl.knaw.dans.easy.business.services;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.servicelayer.services.MigrationService;

public class EasyMigrationService extends AbstractEasyService implements MigrationService
{

    public IdMap getMostRecentByAipId(String aipId) throws ServiceException
    {
        IdMap idMap = null;
        try
        {
            idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        return idMap;
    }

}
