package nl.knaw.dans.easy.business.jumpoff;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.annotations.MutatesJumpoffDmo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class JumpoffWorkDispatcher
{

    public JumpoffDmo getJumpoffDmo(EasyUser sessionUser, String storeId) throws ServiceException
    {
        JumpoffDmo jumpoffDmo = null;
        try
        {
            jumpoffDmo = Data.getEasyStore().findJumpoffDmoFor(storeId);
            //System.err.println(jumpoffDmo.getJumpoffMarkup().getMarkup());
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        return jumpoffDmo;
    }

    @MutatesJumpoffDmo
    public void saveJumpoffDmo(EasyUser sessionUser, JumpoffDmo jumpoffDmo, DataModelObject containerDmo) throws ServiceException
    {
        jumpoffDmo.setObjectId(containerDmo.getStoreId());
        jumpoffDmo.getJumpoffDmoMetadata().getDefaultMarkupMetadata().setLastEditedBy(sessionUser.getId());
        UnitOfWork uow = new EasyUnitOfWork(sessionUser);
        try
        {
            if (jumpoffDmo.getStoreId() == null)
            {
                String storeId = Data.getEasyStore().nextSid(JumpoffDmo.NAMESPACE);
                jumpoffDmo.setStoreId(storeId);
            }
            uow.attach(jumpoffDmo);
            uow.commit();
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        catch (UnitOfWorkInterruptException e)
        {
            throw new ServiceException(e);
        }
        finally
        {
            uow.close();
        }
    }

    public List<UnitMetadata> getUnitMetadata(EasyUser sessionUser, JumpoffDmo jumpoffDmo) throws ServiceException
    {
        try
        {
            return Data.getEasyStore().getUnitMetadata(jumpoffDmo.getStoreId());
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }

    public List<UnitMetadata> retrieveUnitMetadata(EasyUser sessionUser, String storeId, String unitId) throws ServiceException
    {
        try
        {
            return Data.getEasyStore().getUnitMetadata(storeId, unitId);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }

    public URL retrieveURL(String storeId, String unitId) throws ServiceException
    {
        return Data.getEasyStore().getFileURL(storeId, unitId);
    }

    @MutatesJumpoffDmo
    public void deleteUnit(EasyUser sessionUser, String storeId, String unitId, String logMessage) throws ServiceException
    {
        try
        {
            Data.getEasyStore().puregUnit(storeId, unitId, logMessage);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }

    @MutatesJumpoffDmo
    public void deleteJumpoff(EasyUser sessionUser, JumpoffDmo jumpoffDmo, DataModelObject containerDmo, String logMessage) throws ServiceException
    {
        jumpoffDmo.registerDeleted();
        try
        {
            Data.getEasyStore().purge(jumpoffDmo, false, logMessage);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }

    @MutatesJumpoffDmo
    public void toggleEditorMode(EasyUser sessionUser, JumpoffDmo jumpoffDmo) throws ServiceException
    {
        jumpoffDmo.toggleEditorMode();
        if (jumpoffDmo.isLoaded())
        {
            try
            {
                UnitOfWork uow = new EasyUnitOfWork(sessionUser);
                uow.attach(jumpoffDmo);
                uow.commit();
            }
            catch (RepositoryException e)
            {
                throw new ServiceException(e);
            }
            catch (UnitOfWorkInterruptException e)
            {
                throw new ServiceException(e);
            }
        }
    }

}
