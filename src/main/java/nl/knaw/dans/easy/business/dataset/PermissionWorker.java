package nl.knaw.dans.easy.business.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceImpl;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;
import nl.knaw.dans.easy.servicelayer.ReplyNotification;
import nl.knaw.dans.easy.servicelayer.RequestNotification;

import org.apache.commons.lang.StringUtils;

public class PermissionWorker extends AbstractWorker
{

    protected PermissionWorker(EasyUser sessionUser)
    {
        super(sessionUser);
    }

    protected PermissionWorker(UnitOfWork uow)
    {
        super(uow);
    }
    
    
    protected void saveRequest(Dataset dataset, EasyUser requester, PermissionRequestModel requestModel) throws ServiceException
    {
        validateRequest(requestModel);
        
        PermissionSequenceListImpl sequenceList = (PermissionSequenceListImpl) dataset.getPermissionSequenceList();
        PermissionSequenceImpl sequence = (PermissionSequenceImpl) sequenceList.getSequenceFor(requester);
        if (sequence == null)
        {
            sequence = new PermissionSequenceImpl(requester);
            sequenceList.addSequence(sequence);
        }
        sequence.updateRequest(requestModel);

        saveSequenceList(dataset);
        
        new RequestNotification(dataset, requester, requestModel).send();
    }
    
    protected void saveReply(Dataset dataset, EasyUser depositor, PermissionReplyModel replyModel) throws ServiceException
    {
        validateReply(replyModel);
        
        PermissionSequenceList sequenceList = dataset.getPermissionSequenceList();
        PermissionSequenceImpl sequence = (PermissionSequenceImpl) sequenceList.getSequenceFor(replyModel.getRequesterId());
        sequence.updateReply(replyModel);
        
        saveSequenceList(dataset);
        
        new ReplyNotification(dataset, sequence, replyModel).send();
    }

    private void validateReply(PermissionReplyModel replyModel) throws ServiceException
    {
        boolean valid = !StringUtils.isBlank(replyModel.getExplanation());
        State state = replyModel.getState();
        valid &= state != null && !State.Submitted.equals(state);
        if (!valid)
        {
            throw new ServiceException("Insufficient data for PermissionReply");
        }
    }

    private void validateRequest(PermissionRequestModel requestModel) throws ServiceException
    {
        boolean valid = !StringUtils.isBlank(requestModel.getRequestTheme());
        valid &= !StringUtils.isBlank(requestModel.getRequestTitle());
        valid &= requestModel.isAcceptingConditionsOfUse();
        
        if (!valid)
        {
            throw new ServiceException("Insufficient data for PermissionRequest");
        }
    }
    
    private void saveSequenceList(Dataset dataset) throws ServiceException
    {
        try
        {
            getUnitOfWork().attach(dataset);
            getUnitOfWork().commit();
        }
        catch (UnitOfWorkInterruptException e)
        {
            rollBack(e.getMessage());
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        finally
        {
            getUnitOfWork().close();
        }
        
    }
}
