package nl.knaw.dans.easy.business.dataset;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nl.knaw.dans.easy.util.HttpClientFacade;
import nl.knaw.dans.easy.util.HttpClientFacade.ClientException;

public class PidClient {

    public static enum Type {
        urn, doi
    };

    private URL service;
    private HttpClientFacade clientFacade;

    public PidClient(URL service, HttpClientFacade clientFacade) {
        this.service = service;
        this.clientFacade = clientFacade;
        if (service == null || clientFacade == null)
            throw new IllegalArgumentException("none of arguments should be null");
    }

    public String getPid(Type type) throws ClientException, IOException {

        byte[] bytes = clientFacade.post(composeURL(type));
        return new String(bytes, "UTF-8");
    }

    private URL composeURL(Type type) {
        try {
            return new URL(service, "pids?type=" + type);
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("URL for RESTfull PID service not properly configured: " + e.getMessage(), e);
        }
    }
}
