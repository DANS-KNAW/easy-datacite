package nl.knaw.dans.easy.business.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.SecuredStreamingService;
import nl.knaw.dans.easy.util.HttpClientFacade;

public class WowzaSecuredStreamingService implements SecuredStreamingService {
    private static Logger log = LoggerFactory.getLogger(WowzaSecuredStreamingService.class);

    private String baseUrl;
    private long accessDurationInMilliseconds;
    private HttpClientFacade http;

    @Override
    public void addSecurityTicketToResource(String ticket, String resource) throws ServiceException {
        String xmlMessage = createXmlMessage(ticket, resource);
        String url = baseUrl + "/acl/ticket";
        log.debug("Sending following message to url: {}\n {}", url, xmlMessage);
        int status = http.post(url, xmlMessage);
        if (!acceptableResponseToPost(status)) {
            throw new ServiceException(String.format("Failed to add security ticket %s to resource %s, status code: %d", ticket, resource, status));
        }
    }

    private boolean acceptableResponseToPost(int status) {
        return status == 201 || status == 200;
    }

    private String createXmlMessage(String ticket, String resource) {
        long expiring = javaToUnixTimestamp(System.currentTimeMillis() + accessDurationInMilliseconds);
        // @formatter:off
        return String.format("<fsxml>\n"
                + "<properties>\n"
                + "<ticket>%s</ticket>\n"
                + "<uri>%s</uri>\n" 
                + "<role>DEFAULT ROLE</role>\n"
                + "<ip>DEFAULT IP</ip>\n"
                + "<expiry>%d</expiry>\n"
                + "</properties>\n"
                + "</fsxml>\n", ticket, resource, expiring);
        // @formatter:on
    }

    private long javaToUnixTimestamp(long javaTime) {
        return javaTime / 1000;
    }

    @Override
    public void removeSecurityTicket(String ticket) throws ServiceException {
        int status = http.delete(baseUrl + "/acl/ticket");
        if (!acceptableResponseToDelete(status)) {
            throw new ServiceException(String.format("Failed to remoe security ticket %s, status code: %d", ticket, status));
        }
    }

    private boolean acceptableResponseToDelete(int status) {
        return status == 204 || status == 200;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void setAccessDurationInMilliseconds(long ms) {
        this.accessDurationInMilliseconds = ms;
    }

    public void setHttpClientFacade(HttpClientFacade http) {
        this.http = http;
    }

}
