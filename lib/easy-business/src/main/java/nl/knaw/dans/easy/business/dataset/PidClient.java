package nl.knaw.dans.easy.business.dataset;

import static javax.ws.rs.core.Response.Status.OK;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PidClient {

    public static enum Type {
        urn, doi
    };

    private URL service;

    public PidClient(URL service) {
        this.service = service;
        if (service == null)
            throw new IllegalArgumentException("arguments should not null");
    }

    public String getPid(Type type) throws IOException {

        URI uri = composeURL(type);
        ClientResponse response = Client.create().resource(uri).post(ClientResponse.class);
        if (response.getStatus() != OK.getStatusCode()) {
            throw new IOException(uri + "returned HTTP status code " + response.getStatus());
        }
        return response.getEntity(String.class);
    }

    private URI composeURL(Type type) {
        try {
            return new URL(service, "pids?type=" + type).toURI();
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("URL for RESTfull PID service not properly configured: " + e.getMessage(), e);
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("URL for RESTfull PID service not properly configured: " + e.getMessage(), e);
        }
    }
}
