package nl.knaw.dans.easy.business.services;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.TicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import javax.ws.rs.core.MediaType;

public class LennyTicketService implements TicketService {
    private static Logger log = LoggerFactory.getLogger(LennyTicketService.class);

    private URI ticketServiceURI;
    private long accessDurationInMilliseconds;

    @Override
    public void addSecurityTicketToResource(String ticket, String resource) throws ServiceException {
        String xmlMessage = createXmlMessage(ticket, resource);
        log.debug("Sending following message to url: {}\n {}", ticketServiceURI, xmlMessage);
        try {
            int status = Client.create().resource(ticketServiceURI).type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class, xmlMessage).getStatus();
            if (!acceptableResponseToPost(status)) {
                throw new ServiceException(String.format("Failed to add security ticket %s to resource %s, status code: %d", ticket, resource, status));
            }
        }
        catch (UniformInterfaceException e) {
            throw new ServiceException(String.format("Failed to add security ticket %s to resource %s: %s", ticket, resource, e.getMessage()), e);
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
        URI uri = composeDeleteURI(ticket);
        int status = Client.create().resource(uri).post(ClientResponse.class).getStatus();
        if (!acceptableResponseToDelete(status)) {
            throw new ServiceException(String.format("Failed to remove security ticket %s, status code: %d", ticket, status));
        }
    }

    private URI composeDeleteURI(String ticket) throws ServiceException {
        try {
            return new URL(ticketServiceURI.toURL(), ticket).toURI();
        }
        catch (MalformedURLException e) {
            throw createUriException(ticket, e);
        }
        catch (URISyntaxException e) {
            throw createUriException(ticket, e);
        }
    }

    private ServiceException createUriException(String ticket, Exception e) {
        return new ServiceException(String.format("Failed to remove security ticket %s: %s", ticket, e.getMessage()), e);
    }

    private boolean acceptableResponseToDelete(int status) {
        return status == 204 || status == 200;
    }

    @Override
    public void setTicketServiceUrl(String ticketServiceUrl) {
        try {
            this.ticketServiceURI = new URI(ticketServiceUrl);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public void setAccessDurationInMilliseconds(long ms) {
        this.accessDurationInMilliseconds = ms;
    }
}
