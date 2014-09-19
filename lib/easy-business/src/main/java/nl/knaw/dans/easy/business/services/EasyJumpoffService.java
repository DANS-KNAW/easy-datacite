package nl.knaw.dans.easy.business.services;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.JumpoffService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyJumpoffService extends AbstractEasyService implements JumpoffService {

    private JumpoffWorkDispatcher workDispatcher;

    private static final Logger logger = LoggerFactory.getLogger(EasyJumpoffService.class);

    @Override
    public void deleteMetadataUnit(EasyUser sessionUser, DmoStoreId storeId, DsUnitId unitId) throws ServiceException {
        getDispatcher().deleteUnit(sessionUser, storeId, unitId, "purged by " + getUserId(sessionUser));
    }

    @Override
    public JumpoffDmo getJumpoffDmoFor(EasyUser sessionUser, DmoStoreId storeId) throws ServiceException {
        return getDispatcher().getJumpoffDmo(sessionUser, storeId);
    }

    @Override
    public URL getURL(DmoStoreId storeId, DsUnitId unitId) throws ServiceException {
        return getDispatcher().retrieveURL(storeId, unitId);
    }

    @Override
    public void saveJumpoffDmo(EasyUser sessionUser, DataModelObject containerDmo, JumpoffDmo jumpoffDmo) throws ServiceException {
        getDispatcher().saveJumpoffDmo(sessionUser, jumpoffDmo, containerDmo);
        if (logger.isDebugEnabled()) {
            logger.debug("User '" + getUserId(sessionUser) + "' saved JumpoffDmo of dataset " + getStoreId(containerDmo) + " as " + jumpoffDmo.getStoreId());
        }
    }

    public List<UnitMetadata> getUnitMetadata(EasyUser sessionUser, JumpoffDmo jumpoffDmo) throws ServiceException {
        return getDispatcher().getUnitMetadata(sessionUser, jumpoffDmo);
    }

    public List<UnitMetadata> getUnitMetadata(EasyUser sessionUser, DmoStoreId storeId, DsUnitId unitId) throws ServiceException {
        return getDispatcher().retrieveUnitMetadata(sessionUser, storeId, unitId);
    }

    @Override
    public void deleteJumpoff(EasyUser sessionUser, DataModelObject containerDmo, JumpoffDmo jumpoffDmo) throws ServiceException {
        getDispatcher().deleteJumpoff(sessionUser, jumpoffDmo, containerDmo, "purged by " + getUserId(sessionUser));
        if (logger.isDebugEnabled()) {
            logger.debug("User '" + getUserId(sessionUser) + "' purged JumpoffDmo of dataset " + getStoreId(containerDmo) + " as " + jumpoffDmo.getStoreId());
        }
    }

    @Override
    public void toggleEditorMode(EasyUser sessionUser, JumpoffDmo jumpoffDmo) throws ServiceException {
        getDispatcher().toggleEditorMode(sessionUser, jumpoffDmo);
        if (logger.isDebugEnabled()) {
            logger.debug("User '" + getUserId(sessionUser) + "' toggled version of " + jumpoffDmo.getStoreId());
        }
    }

    private JumpoffWorkDispatcher getDispatcher() {
        if (workDispatcher == null) {
            workDispatcher = new JumpoffWorkDispatcher();
        }
        return workDispatcher;
    }

}
