package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;

/**
 * Service provides secured access to media streams. The security is based on tickets that grant access to a specific resource for a standard time.
 */
public interface SecuredStreamingService {
    /**
     * Sets the base URL under which the resources are accessed.
     * 
     * @param baseUrl
     *        the base URL as a string
     */
    void setBaseUrl(String baseUrl);

    /**
     * Sets the duration in milliseconds that tickets are valid.
     * 
     * @param ms
     *        a duration in milliseconds
     */
    void setAccessDurationInMilliseconds(long ms);

    /**
     * Instructs the {@link SecuredStreamingService} to grant access to <code>resource</code> to clients that send <code>ticket</code> along (as long as
     * <code>ticket</code> is valid). Other tickets stay valid until they expire or are canceled with a call to {@link #removeSecurityTicket(String)}.
     * 
     * @param ticket
     *        the new ticket
     * @param resource
     *        the resouce to grant access to
     * @throws ServiceException
     */
    void addSecurityTicketToResource(String ticket, String resource) throws ServiceException;

    /**
     * Instructs the {@link SecuredStreamingService} to invalidate <code>ticket</code>. Subsequent requests based on the ticket must be denied by the streaming
     * server.
     * 
     * @param ticket
     * @throws ServiceException
     */
    void removeSecurityTicket(String ticket) throws ServiceException;
}
