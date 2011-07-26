package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.migration.IdMap;

public interface MigrationService extends EasyService
{
    
    IdMap getMostRecentByAipId(String aipId) throws ServiceException;

}
