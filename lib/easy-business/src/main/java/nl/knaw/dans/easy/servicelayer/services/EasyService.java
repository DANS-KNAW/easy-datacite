package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;

/**
 * Services expose the main API of the business layer.
 * 
 * @author ecco
 */
public interface EasyService
{
    /**
     * Get the fully qualified class name of this service.
     * 
     * @return the fully qualified class name of this service
     */
    String getServiceTypeName();

    /**
     * Get a short description of this service.
     * 
     * @return a short description of this service
     */
    String getServiceDescription();

    /**
     * Do any processing needed after bean instantiation.
     */
    void doBeanPostProcessing() throws ServiceException;
}
