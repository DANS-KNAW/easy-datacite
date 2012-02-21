package nl.knaw.dans.easy.servicelayer.services;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public interface JumpoffService extends EasyService
{

    JumpoffDmo getJumpoffDmoFor(EasyUser sessionUser,DmoStoreId storeId) throws ServiceException;
    
    URL getURL(DmoStoreId storeId, DsUnitId unitId) throws ServiceException;

    void saveJumpoffDmo(EasyUser sessionUser,DataModelObject containerDmo, JumpoffDmo jumpoffDmo) throws ServiceException;

    void deleteMetadataUnit(EasyUser sessionUser, DmoStoreId storeId, DsUnitId unitId) throws ServiceException;
    
    void deleteJumpoff(EasyUser sessionUser,DataModelObject containerDmo, JumpoffDmo jumpoffDmo) throws ServiceException;
    
    List<UnitMetadata> getUnitMetadata(EasyUser sessionUser,JumpoffDmo jumpoffDmo) throws ServiceException;
    
    List<UnitMetadata> getUnitMetadata(EasyUser sessionUser,DmoStoreId storeId, DsUnitId unitId) throws ServiceException;

    void toggleEditorMode(EasyUser sessionUser, JumpoffDmo jumpoffDmo) throws ServiceException;

}
