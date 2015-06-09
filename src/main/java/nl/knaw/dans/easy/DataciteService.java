package nl.knaw.dans.easy;

import javax.ws.rs.core.Response;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class DataciteService {

    private static final String CONTENT_TYPE = "application/xml;charset=UTF-8";

    private static Logger logger = LoggerFactory.getLogger(DataciteService.class);

    private final DataciteServiceConfiguration configuration;

    private DataciteResourcesBuilder resourcesBuilder;

    public DataciteService(DataciteServiceConfiguration configuration) {
        this.configuration = configuration;
        resourcesBuilder = new DataciteResourcesBuilder(configuration.getXslEmd2datacite(), configuration.getDatasetResolver());
    }

    public void create(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        String message = createMessage(result, "Creating", emds);
        if (getCount(0, result) == emds.length)
            logger.info(message);
        else {
            logger.error(message);
            throw new DataciteServiceException(message);
        }
    }

    public void update(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        String message = createMessage(result, "Updating", emds);
        if (getCount(1, result) == emds.length)
            logger.info(message);
        else {
            if (getCount(0, result) + getCount(1, result) == emds.length) {
                logger.warn(message);
            } else {
                logger.error(message);
                throw new DataciteServiceException(message);
            }
        }
    }

    public void createOrUpdate(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        String message = createMessage(result, "Creating/updating", emds);
        if (getCount(0, result) + getCount(1, result) == emds.length)
            logger.info(message);
        else {
            logger.error(message);
            throw new DataciteServiceException(message);
        }
    }

    private String createMessage(String result, String crudType, EasyMetadata... emds) {
        String firstDOI = emds[0].getEmdIdentifier().getDansManagedDoi();
        String format = crudType + " %s DOIs resulted in %s. First DOI %s";
        return String.format(format, emds.length, result, firstDOI);
    }

    /** @return something like "dois created: 0, dois updated: 0 (TEST OK)" */
    private String post(String content) throws DataciteServiceException {
        try {
            logger.debug("THIS IS SENT TO DATACITE: {}", content);
            ClientResponse response = createWebResource().type(CONTENT_TYPE).post(ClientResponse.class, content);
            String entity = response.getEntity(String.class);
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw createPostFailedException(response.getStatus(), entity);
            }
            return entity;
        }
        catch (UniformInterfaceException e) {
            throw createPostFailedException(e);
        }
        catch (ClientHandlerException e) {
            throw createPostFailedException(e);
        }
    }

    private WebResource createWebResource() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(configuration.getUsername(), configuration.getPassword()));
        return client.resource(configuration.getDoiRegistrationUri());
    }

    private DataciteServiceException createPostFailedException(int status, String cause) {
        return new DataciteServiceException("DOI post failed : HTTP error code : " + status + "\n" + cause);
    }

    private DataciteServiceException createPostFailedException(Exception cause) {
        return new DataciteServiceException("DOI post failed: " + cause.getMessage(), cause);
    }

    /** @return the i-th number from something like "dois created: 0, dois updated: 0 (TEST OK)" */
    private int getCount(int i, String postResult) {
        return Integer.parseInt(postResult.split(",")[i].replaceAll("\\D+", ""));
    }
}
