package nl.knaw.dans.easy.data.audit;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.aspectj.lang.JoinPoint;
import org.joda.time.DateTime;

public abstract class AbstractAuditRecord<T> implements AuditRecord<T>
{

    public static final String SEPARATOR = ";";

    private final DateTime date;
    private final EasyUser sessionUser;
    private final T tracedObject;
    private final String methodSignature;
    private final Object[] arguments;

    protected AbstractAuditRecord(EasyUser sessionUser, T tracedObject, JoinPoint joinPoint)
    {
        date = new DateTime();
        this.sessionUser = sessionUser;
        this.tracedObject = tracedObject;
        this.methodSignature = joinPoint.getSignature().toString();
        this.arguments = joinPoint.getArgs();
    }

    public DateTime getDate()
    {
        return date;
    }

    public EasyUser getSessionUser()
    {
        return sessionUser;
    }

    public T getTracedObject()
    {
        return tracedObject;
    }

    public String getMethodSignature()
    {
        return methodSignature;
    }

    public Object[] getArguments()
    {
        return arguments;
    }

    public abstract String getTracedObjectId();

    public String getTracedType()
    {
        return tracedObject == null ? "unknown" : tracedObject.getClass().getSimpleName();
    }

    public String getRecord()
    {
        StringBuilder sb = new StringBuilder(date.toString()).append(SEPARATOR).append(getTracedType()).append(SEPARATOR).append(
                sessionUser.isAnonymous() ? "anonymous" : sessionUser.getId()).append(SEPARATOR).append(
                sessionUser.isAnonymous() ? "anonymous" : sessionUser.getEmail()).append(SEPARATOR).append(getTracedObjectId()).append(SEPARATOR).append(
                methodSignature).append(SEPARATOR);
        return sb.toString();
    }

}
