package nl.knaw.dans.easy.data.audit;

import org.aspectj.lang.JoinPoint;

import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class JumpoffAuditRecord extends AbstractAuditRecord<JumpoffDmo>
{
    
    private final String tracedObjectId;

    public JumpoffAuditRecord(EasyUser sessionUser, JumpoffDmo tracedObject, JoinPoint joinPoint)
    {
        super(sessionUser, tracedObject, joinPoint);
        tracedObjectId = tracedObject.getStoreId();
    }
    
    public JumpoffAuditRecord(EasyUser sessionUser, String tracedObjectId, JoinPoint joinPoint)
    {
        super(sessionUser, null, joinPoint);
        this.tracedObjectId = tracedObjectId;
    }

    @Override
    public String getTracedObjectId()
    {
        return tracedObjectId;
    }
    
    public String getTracedType()
    {
        return JumpoffDmo.class.getSimpleName();
    }

}
