package nl.knaw.dans.easy.data.audit;

import org.aspectj.lang.JoinPoint;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class UserAuditRecord extends AbstractAuditRecord<EasyUser> {

    public UserAuditRecord(EasyUser sessionUser, EasyUser tracedObject, JoinPoint joinPoint) {
        super(sessionUser, tracedObject, joinPoint);
    }

    @Override
    public String getTracedObjectId() {
        return getTracedObject().getId();
    }

}
