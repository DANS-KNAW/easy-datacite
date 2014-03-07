package nl.knaw.dans.c.dmo.collections.aspects;

import nl.knaw.dans.c.dmo.collections.CollectionManagerImpl;
import nl.knaw.dans.c.dmo.collections.core.Settings;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.exceptions.SecurityViolationException;
import nl.knaw.dans.i.security.SecurityAgent;
import nl.knaw.dans.i.security.annotations.SecuredOperation;

import org.aspectj.lang.reflect.MethodSignature;

public aspect SecurityAspect
{    
    
    pointcut securedManagerOperation(CollectionManagerImpl manager) : 
        execution(@SecuredOperation public * CollectionManagerImpl.*(..))
        && target(manager);
    
    before(CollectionManagerImpl manager) throws SecurityViolationException : securedManagerOperation(manager)
    {
        MethodSignature methodSignature = (MethodSignature) thisJoinPointStaticPart.getSignature();
        String operationId = methodSignature.getMethod().getAnnotation(SecuredOperation.class).id();
        SecurityAgent agent = Settings.instance().getAgentFor(operationId);
        if (agent == null)
        {
            throw new SecurityViolationException("No SecurityAgent for operation " + operationId);
        }
        String ownerId = manager.getOwnerId();
        Object[] args = thisJoinPoint.getArgs();
        if (!agent.isAllowed(ownerId, args))
        {
            throw new SecurityViolationException(agent + " disallowes " + operationId);
        }
    }


}
