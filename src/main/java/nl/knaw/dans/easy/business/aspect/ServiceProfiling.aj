package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.easy.business.services.EasyDatasetService;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.business.services.EasyDisciplineCollectionService;
import nl.knaw.dans.easy.business.services.EasyItemService;
import nl.knaw.dans.easy.business.services.EasyJumpoffService;
import nl.knaw.dans.easy.business.services.EasySearchService;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.domain.worker.WorkReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs profiling on services.
 * 
 * @author ecco Nov 8, 2009
 */
public aspect ServiceProfiling
{
    private static Logger logger = LoggerFactory.getLogger(ServiceProfiling.class);
    

    pointcut profiling() : 
        execution(public * EasyDatasetService.*(..)) ||
        execution(public * EasyDepositService.*(..)) ||
        execution(public * EasySearchService.*(..)) ||
        execution(public * EasyUserService.*(..)) ||
        (execution(public * EasyJumpoffService.*(..)) && 
                !execution(public * EasyJumpoffService.*(String, String))
                )||
        execution(public * EasyItemService.get*(..)) ||
        execution(public * EasyDisciplineCollectionService.*(..)); 
    // ||
        //execution(public boolean EasyItemService.hasChildItems(..));

    pointcut profilingReporter(EasyItemService service, WorkListener[] workListeners) :
        execution(* *(.., WorkListener...)) &&
        target(service) &&
        args(.., workListeners);

    Object around() : profiling()
    {     
        long start = System.currentTimeMillis();
        Object result = proceed();
        long end = System.currentTimeMillis();
        logger.info(thisJoinPointStaticPart.getSignature().getDeclaringType().getSimpleName() + ";"
                + thisJoinPointStaticPart.getSignature().toString() + ";" + (end - start) + ";1");
        return result;
    }

    Object around(EasyItemService service, WorkListener[] workListeners) : profilingReporter(service, workListeners)
    {
        WorkReporter reporter = null;
        WorkListener[] proceedingWorkListeners = null;
        if (workListeners == null)
        {
            reporter = new WorkReporter();
            proceedingWorkListeners = new WorkListener[] {reporter};
        }
        else
        {
            for (WorkListener listener : workListeners)
            {
                if (listener instanceof WorkReporter)
                {
                    reporter = (WorkReporter) listener;
                    proceedingWorkListeners = workListeners;
                    break;
                }
            }
        }
        if (reporter == null)
        {
            reporter = new WorkReporter();
            proceedingWorkListeners = new WorkListener[workListeners.length + 1];
            int i;
            for (i = 0; i < workListeners.length; i++)
            {
                proceedingWorkListeners[i] = workListeners[i];
            }
            proceedingWorkListeners[i] = reporter;
        }
        long start = System.currentTimeMillis();
        Object result = proceed(service, proceedingWorkListeners);
        long end = System.currentTimeMillis();
        logger.info(thisJoinPointStaticPart.getSignature().getDeclaringType().getSimpleName() + ";"
                + thisJoinPointStaticPart.getSignature().toString() + ";" + (end - start) + ";"
                + reporter.getTotalActionCount());
        return result;
    }

    // Object around() : profilingList()
    // {
    // long start = System.currentTimeMillis();
    // List<?> result = (List<?>) proceed();
    // long end = System.currentTimeMillis();
    // logger.info(thisJoinPointStaticPart.getSignature().getDeclaringType().getSimpleName()
    // + ";" + thisJoinPointStaticPart.getSignature().toString()
    // + ";" + (end - start)
    // + ";" + result.size());
    // return result;
    // }
    
    // for testing only
    protected static Logger setLogger(Logger newLogger)
    {
        Logger oldLogger = logger;
        logger = newLogger;
        return oldLogger;
    }

}
