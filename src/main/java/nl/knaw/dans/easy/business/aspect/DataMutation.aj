package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.data.audit.Audit;
import nl.knaw.dans.easy.data.audit.DatasetAuditRecord;
import nl.knaw.dans.easy.data.audit.JumpoffAuditRecord;
import nl.knaw.dans.easy.data.audit.UserAuditRecord;
import nl.knaw.dans.easy.domain.annotations.MutatesDataset;
import nl.knaw.dans.easy.domain.annotations.MutatesJumpoffDmo;
import nl.knaw.dans.easy.domain.annotations.MutatesUser;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public aspect DataMutation
{
    
    pointcut datasetMutation(EasyUser sessionUser, Dataset dataset) : 
        (execution(@MutatesDataset * *(..)) ||
        initialization(@MutatesDataset *.new(..)))
        && args(sessionUser, dataset, ..);
    
    pointcut userMutation(EasyUser sessionUser, EasyUser userUnderEdit) :
        execution(@MutatesUser * *(..))
        && args(sessionUser, userUnderEdit, ..);
    
    pointcut jumpoffDmoMutation(EasyUser sessionUser, JumpoffDmo jumpoffDmo) :
        execution(@MutatesJumpoffDmo * *(..))
        && args(sessionUser, jumpoffDmo, ..);
    
    pointcut jumpoffDmoMutation2(EasyUser sessionUser, DmoStoreId storeId) :
        execution(@MutatesJumpoffDmo * *(..))
        && args(sessionUser, storeId, ..);
    
    pointcut invalidMutatesDataset() : execution(@MutatesDataset * *(!EasyUser, !Dataset, ..));
    
    pointcut invalidMutatesUser() : execution(@MutatesUser * *(!EasyUser, ..));
    
    pointcut invalidMutatesJumpoffDmo() : execution(@MutatesJumpoffDmo * *(..)) 
        && !(args(EasyUser, JumpoffDmo, ..) || args(EasyUser, DmoStoreId, ..));
    
    declare error : invalidMutatesDataset() : "Invalid use of annotation @MutatesDataset. "
        + "Expected parameters: 0. EasyUser, 1. Dataset, ..";
    
    declare error : invalidMutatesUser() : "Invalid use of annotation @MutatesUser. "
        + "Expected parameters: 0. EasyUser, ..";
     
    before(EasyUser sessionUser, Dataset dataset) : datasetMutation(sessionUser, dataset)
    {
        Audit.storeAuditRecord(new DatasetAuditRecord(sessionUser, dataset, thisJoinPoint));
    }

    before(EasyUser sessionUser, EasyUser userUnderEdit) : userMutation(sessionUser, userUnderEdit)
    {
        Audit.storeAuditRecord(new UserAuditRecord(sessionUser, userUnderEdit, thisJoinPoint));
    }
    
    after(EasyUser sessionUser, JumpoffDmo jumpoffDmo) : jumpoffDmoMutation(sessionUser, jumpoffDmo)
    {
        Audit.storeAuditRecord(new JumpoffAuditRecord(sessionUser, jumpoffDmo, thisJoinPoint));
    }
    
    before(EasyUser sessionUser, DmoStoreId storeId) : jumpoffDmoMutation2(sessionUser, storeId)
    {
        Audit.storeAuditRecord(new JumpoffAuditRecord(sessionUser, storeId, thisJoinPoint));
    }
    
    before() : invalidMutatesJumpoffDmo()
    {
        throw new RuntimeException("Invalid use of annotation @MutatesJumpoffDmo");
    }
}
