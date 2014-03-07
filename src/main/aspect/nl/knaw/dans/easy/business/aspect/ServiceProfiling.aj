package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.easy.business.services.EasyDatasetService;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.business.services.EasyDisciplineCollectionService;
import nl.knaw.dans.easy.business.services.EasyItemService;
import nl.knaw.dans.easy.business.services.EasyJumpoffService;
import nl.knaw.dans.easy.business.services.EasySearchService;
import nl.knaw.dans.easy.business.services.EasyUserService;

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
                !execution(public * EasyJumpoffService.*(DmoStoreId, DsUnitId))
                )||
        execution(public * EasyItemService.*(..)) ||
        execution(public * EasyDisciplineCollectionService.*(..));

    Object around() : profiling()
    {
        long start = System.currentTimeMillis();
        Object result = proceed();
        long end = System.currentTimeMillis();
        if (logger.isInfoEnabled())
        {
            logger.info(thisJoinPointStaticPart.getSignature().getDeclaringType().getSimpleName() + ";" + thisJoinPointStaticPart.getSignature().toString()
                    + ";" + (end - start) + ";1");
        }
        return result;
    }
}
