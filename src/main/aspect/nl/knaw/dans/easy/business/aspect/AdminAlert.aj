package nl.knaw.dans.easy.business.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDatasetService;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.business.services.EasyDisciplineCollectionService;
import nl.knaw.dans.easy.business.services.EasyItemService;
import nl.knaw.dans.easy.business.services.EasyJumpoffService;
import nl.knaw.dans.easy.business.services.EasySearchService;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.servicelayer.services.EasyService;

public aspect AdminAlert
{
    
    private static final Logger logger = LoggerFactory.getLogger(AdminAlert.class);
    
    
    pointcut allPublicMethods() :
        execution(public * EasyService.*(..)) ||
        execution(public * EasyDatasetService.*(..)) ||
        execution(public * EasyDepositService.*(..)) ||
        execution(public * EasyDisciplineCollectionService.*(..)) ||
        execution(public * EasyItemService.*(..)) ||
        execution(public * EasyJumpoffService.*(..)) ||
        execution(public * EasySearchService.*(..))  ||
        execution(public * EasyUserService.*(..));
    
    after() throwing(ServiceException e) throws ServiceException : allPublicMethods()
    {
        if ((e instanceof ObjectNotAvailableException) || (e instanceof CommonSecurityException))
        {
            logger.info("Not sending admin mail because exception has external cause: " + e.getMessage());
        }
        else
        {
            String msg = "ServiceException thrown by \n" + AspectUtil.printJoinPoint(thisJoinPoint);
            try
            {
                ExternalServices.getAdminMailer().sendExceptionMail(msg, e);
            }
            catch (Exception mailException)
            {
                // shit happens
                logger.warn("Could not send admin mail: ", mailException);
            }
        }
        throw e;
    }

}
