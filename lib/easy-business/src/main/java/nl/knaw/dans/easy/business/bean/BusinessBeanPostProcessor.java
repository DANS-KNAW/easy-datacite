package nl.knaw.dans.easy.business.bean;

import nl.knaw.dans.easy.servicelayer.services.EasyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BusinessBeanPostProcessor implements BeanPostProcessor
{

    private static Logger logger = LoggerFactory.getLogger(BusinessBeanPostProcessor.class);

    public Object postProcessBeforeInitialization(Object bean, String beanname) throws BeansException
    {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanname) throws BeansException
    {
        if (bean instanceof EasyService)
        {
            EasyService service = (EasyService) bean;
            try
            {
                service.doBeanPostProcessing();
                logger.debug("Process after initialization: called on " + service.getServiceTypeName());
            }
            catch (Exception e)
            {
                throw new FatalBeanException("Cannot properly instantiate " + service.getServiceTypeName() + ": ", e);
            }
        }
        return bean;
    }

}
