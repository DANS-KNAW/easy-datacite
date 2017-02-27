package nl.knaw.dans.easy;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import nl.knaw.dans.easy.DataciteResourcesBuilder.Resources;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

public class DataciteService {

    private static Logger logger = LoggerFactory.getLogger(DataciteService.class);

    private final DataciteServiceConfiguration configuration;

    private DataciteResourcesBuilder resourcesBuilder;

    public DataciteService(DataciteServiceConfiguration configuration) {
        this.configuration = configuration;
        resourcesBuilder = new DataciteResourcesBuilder(configuration.getXslEmd2datacite(), configuration.getDatasetResolver());
    }

    public void create(EasyMetadata emd) throws DataciteServiceException {
        Resources resources = resourcesBuilder.create(emd);

        // metadata must be uploaded first
        postMetadata(resources.metadataResource);
        String doiResult = postDoi(resources.doiResource);

        logResult("Creating", doiResult, emd.getEmdIdentifier().getDansManagedDoi());
    }

    // NOTE: create and update do exactly the same thing, apart from the logging.
    // We must keep both, however, because this is a public API that is used by other modules
    public void update(EasyMetadata emd) throws DataciteServiceException {
        Resources resources = resourcesBuilder.create(emd);

        // metadata must be uploaded first
        postMetadata(resources.metadataResource);
        String doiResult = postDoi(resources.doiResource);

        logResult("Updating", doiResult, emd.getEmdIdentifier().getDansManagedDoi());
    }

    private void logResult(String crudType, String result, String doi) {
        String format = "%s of DOI %s resulted in %s";
        String message = String.format(format, crudType, doi, result);
        logger.info(message);
    }

    private String postDoi(String content) throws DataciteServiceException {
        try {
            logger.debug("THIS IS SENT TO DATACITE: {}", content);
            ClientResponse response = createDoiWebResource()
                .type(configuration.getDoiRegistrationContentType())
                .post(ClientResponse.class, content);
            String entity = response.getEntity(String.class);
            if (response.getStatus() != Response.Status.CREATED.getStatusCode())
                throw createDoiPostFailedException(response.getStatus(), entity);
            return entity;
        }
        catch (UniformInterfaceException e) {
            throw createDoiPostFailedException(e);
        }
        catch (ClientHandlerException e) {
            throw createDoiPostFailedException(e);
        }
    }

    private String postMetadata(String content) throws DataciteServiceException {
        try {
            logger.debug("THIS IS SENT TO DATACITE: {}", content);
            ClientResponse response = createMetadataWebResource()
                .type(configuration.getMetadataRegistrationContentType())
                .post(ClientResponse.class, content);
            String entity = response.getEntity(String.class);
            if (response.getStatus() != Response.Status.CREATED.getStatusCode())
                throw createMetadataPostFailedException(response.getStatus(), entity);
            return entity;
        }
        catch (UniformInterfaceException e) {
            throw createMetadataPostFailedException(e);
        }
        catch (ClientHandlerException e) {
            throw createMetadataPostFailedException(e);
        }
    }

    private WebResource createDoiWebResource() {
        return createWebResource(configuration.getDoiRegistrationUri());
    }

    private WebResource createMetadataWebResource() {
        return createWebResource(configuration.getMetadataRegistrationUri());
    }

    private WebResource createWebResource(String uri) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(configuration.getUsername(), configuration.getPassword()));
        return client.resource(uri);
    }

    private DataciteServiceException createDoiPostFailedException(int status, String cause) {
        return createPostFailedException("DOI", status, cause);
    }

    private DataciteServiceException createDoiPostFailedException(Exception cause) {
        return createPostFailedException("DOI", cause);
    }

    private DataciteServiceException createMetadataPostFailedException(int status, String cause) {
        return createPostFailedException("metadata", status, cause);
    }

    private DataciteServiceException createMetadataPostFailedException(Exception cause) {
        return createPostFailedException("metadata", cause);
    }

    private DataciteServiceException createPostFailedException(String kind, int status, String cause) {
        return new DataciteServiceException(kind + " post failed : HTTP error code : " + status + "\n" + cause);
    }

    private DataciteServiceException createPostFailedException(String kind, Exception cause) {
        return new DataciteServiceException(kind + " post failed: " + cause.getMessage(), cause);
    }
}
