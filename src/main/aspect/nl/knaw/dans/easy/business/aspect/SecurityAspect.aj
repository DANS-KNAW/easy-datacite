package nl.knaw.dans.easy.business.aspect;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.TemporaryUnAvailableException;
import nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher;
import nl.knaw.dans.easy.business.item.DownloadWorkDispatcher;
import nl.knaw.dans.easy.business.item.ItemWorkDispatcher;
import nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.i.security.annotations.SecuredOperation;

import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect SecurityAspect
{

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    pointcut securityBeforeMethod() : 
        (
        execution(* ItemWorkDispatcher.*(..)) ||
        execution(* DownloadWorkDispatcher.*(..)) ||
        execution(* DatasetWorkDispatcher.*(..)) ||
        execution(* JumpoffWorkDispatcher.*(..))
        ) && !execution(* *.get*(..)) ||
        execution(* EasyUserService.getUserById(..));

    pointcut securityAroundMethod() :
        execution(public * ItemWorkDispatcher.get*(..)) ||
        execution(* DatasetWorkDispatcher.get*(..));

    pointcut allPublicMethods() :
        execution(public * ItemWorkDispatcher.*(..)) ||
        execution(public * DatasetWorkDispatcher.*(..));
    
    /**
     * Defines a pointcut on all operations that are annotated with {@link SecuredOperation}.
     * Using an annotation is more flexible than declaring secured operations in this aspect.
     */
    pointcut securedOperation() : 
        execution(@SecuredOperation * *.*(..));

    /**
     * Guards access to the methods listed in pointcut securityBeforeMethod. If the SecurityOfficer assigned to the
     * signature of the invoked method says that enabling of the method, given the context, is not allowed, an
     * EasySecurityException will be thrown.
     * <p/>
     * Besides that, an extensive explanation will be printed to the log. Because the explanation contains id's of
     * objects and gives insight to the precise working of the security mechanism, it would be a security risk in itself
     * if such detailed information would become known to the outside world. Therefore the thrown exception only carries
     * a reference to this detailed explanation in it's message.
     * 
     * @throws CommonSecurityException
     *         if the SecurityOfficer assigned to the signature of the invoked method says that enabling of the method,
     *         given the context, is not allowed
     */
    before() throws CommonSecurityException : securityBeforeMethod()
    {
        ContextParameters ctxParameters = new ContextParameters(thisJoinPoint.getArgs());
        String signature = thisJoinPointStaticPart.getSignature().toString();
        SecurityOfficer officer = Security.getAuthz().getSecurityOfficer(signature);
        if (!officer.isEnableAllowed(ctxParameters))
        {
            String msg = ("\nForbidden! \n" + AspectUtil.printJoinPoint(thisJoinPoint)
                    + "\nConditions that must be met before entering this method:\n\t"
                    + officer.getProposition() + "\nThe SecurityOfficer explains why it is not allowed: "
                    + officer.explainEnableAllowed(ctxParameters)
                    + printStackTrace() + "\n");
            throwError(ctxParameters, officer, msg);
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Entering allowed by security: " + signature);
        }
    }
    
    /**
     * Guards access to the methods annotated with {@link SecuredOperation}. If the SecurityOfficer assigned to the
     * operationId of the invoked method says that enabling of the method, given the context, is not allowed, an
     * EasySecurityException will be thrown.
     * <p/>
     * Besides that, an extensive explanation will be printed to the log. Because the explanation contains id's of
     * objects and gives insight to the precise working of the security mechanism, it would be a security risk in itself
     * if such detailed information would become known to the outside world. Therefore the thrown exception only carries
     * a reference to this detailed explanation in it's message.
     * 
     * @throws CommonSecurityException
     *         if the SecurityOfficer assigned to the operationId says that enabling of the method,
     *         given the context, is not allowed
     */
    before() throws CommonSecurityException : securedOperation()
    {
        ContextParameters ctxParameters = new ContextParameters(thisJoinPoint.getArgs());
        MethodSignature methodSignature = (MethodSignature) thisJoinPointStaticPart.getSignature();
        String operationId = methodSignature.getMethod().getAnnotation(SecuredOperation.class).id();
        SecurityOfficer officer = Security.getAuthz().getSecurityOfficer(operationId);
        if (!officer.isEnableAllowed(ctxParameters))
        {
            String msg = ("\nForbidden! \n" + AspectUtil.printJoinPoint(thisJoinPoint)
                    + "\nConditions that must be met before entering this method:\n\t"
                    + officer.getProposition() + "\nThe SecurityOfficer explains why it is not allowed: "
                    + officer.explainEnableAllowed(ctxParameters)
                    + printStackTrace() + "\n");
            throwError(ctxParameters, officer, msg);
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Entering allowed by security: " + operationId);
        }
    }

    private void throwError(ContextParameters ctxParameters, SecurityOfficer officer, String msg)
            throws CommonSecurityException
    {
        logger.error(msg);
        List<Object> hints = new ArrayList<Object>();
        officer.getHints(ctxParameters, hints);
        if (hints.contains(DatasetState.MAINTENANCE))
        {
            throw new TemporaryUnAvailableException("Dataset in maintanance.", hints);
        }
        throw new CommonSecurityException("Violation of access rules. See log file for details.", hints);
    }

    /**
     * Guards returning from methods listed in pointcut securityAroundMethod. If the SecurityOfficer assigned to the
     * signature of the invoked method says that returning from the method, given the context, is not allowed, an
     * EasySecurityException will be thrown.
     * 
     * @return the result of the method
     * @throws CommonSecurityException
     *         if the SecurityOfficer assigned to the signature of the invoked method says that returning from the
     *         method, given the context, is not allowed
     */
    Object around() throws CommonSecurityException : securityAroundMethod()
    {
        String signature = thisJoinPointStaticPart.getSignature().toString();
        SecurityOfficer officer = Security.getAuthz().getSecurityOfficer(signature);
        ContextParameters ctxParameters = new ContextParameters(thisJoinPoint.getArgs());
        Object result = proceed();       
        ctxParameters.setResult(result);
        
        if (!officer.isEnableAllowed(ctxParameters))
        {
            String msg = ("\nForbidden! " + signature + " ("
                    + thisJoinPointStaticPart.getSourceLocation().getFileName() + ":"
                    + thisJoinPointStaticPart.getSourceLocation().getLine() + ")" + "\n"
                    + AspectUtil.printParameters(thisJoinPoint) + "\nResult:\n\t" + result
                    + "\nConditions that must be met before returning from this method:\n\t" + officer.getProposition()
                    + "\nThe SecurityOfficer explains why it is not allowed: "
                    + officer.explainEnableAllowed(ctxParameters)
                    + printStackTrace() + "\n");
            throwError(ctxParameters, officer, msg);
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Returning result is allowed by security: " + signature);
        }
        return result;
    }

    /**
     * Makes sure that any RuntimeException a WorkDispatcher may stumble upon is wrapped in a checked ServiceException.
     * This advice also implies that any public method in a WorkDispatcher has a throws clause for ServiceException in
     * it's signature.
     * 
     * @param e
     *        the thrown RuntimeException
     * @throws ServiceException
     *         e wrapped as a ServiceException
     */
    after() throwing(RuntimeException e) throws ServiceException : allPublicMethods()
    {
        throw new ServiceException("The method " + thisJoinPointStaticPart.getSignature().toString() + " ran into a RuntimeException: ", e);
    }
    
    private String printStackTrace()
    {
        StringBuilder sb = new StringBuilder("\nStacktrace:");
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            if (ste.getClassName().startsWith("nl.knaw"))
            {
                sb.append("\n\t")
                .append("at ")
                .append(ste.getClassName())
                .append(" (")
                .append(ste.getFileName())
                .append(":")
                .append(ste.getLineNumber())
                .append(")");
            }
        }
        return sb.toString();
    }


}
