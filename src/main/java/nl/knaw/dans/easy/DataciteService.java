/*
 * Copyright (C) 2014 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import nl.knaw.dans.easy.DataciteResourcesBuilder.Resources;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

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

    public boolean doiExists(String doi) throws DataciteServiceException {
        try {
            String uri = configuration.getDoiRegistrationUri() + "/" + doi;
            logger.debug("Checking if doi: {} is registered in Datacite", doi);
            ClientResponse response = createWebResource(uri).type(configuration.getMetadataRegistrationContentType()).get(ClientResponse.class);
            int status = response.getStatus();
            if (status == NO_CONTENT.getStatusCode() || status == OK.getStatusCode()) {
                return true;
            } else if (status == NOT_FOUND.getStatusCode()) {
                return false;
            } else
                throw createDoiGetFailedException(status, response.getEntity(String.class));
        }
        catch (ClientHandlerException e) {
            throw createDoiGetFailedException(e);
        }
        catch (UniformInterfaceException e) {
            throw createDoiGetFailedException(e);
        }
        catch (DataciteServiceException e) {
            throw createDoiGetFailedException(e);
        }
    }

    private String postDoi(String content) throws DataciteServiceException {
        try {
            logger.debug("THIS IS SENT TO DATACITE: {}", content);
            ClientResponse response = createDoiWebResource().type(configuration.getDoiRegistrationContentType()).post(ClientResponse.class, content);
            String entity = response.getEntity(String.class);
            if (response.getStatus() != CREATED.getStatusCode())
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

    private boolean isUserError(int statusCode) {
        return (statusCode >= 400) && (statusCode < 500);
    }

    private String postMetadata(String content) throws DataciteServiceException {
        try {
            logger.debug("THIS IS SENT TO DATACITE: {}", content);
            ClientResponse response = createMetadataWebResource().type(configuration.getMetadataRegistrationContentType()).post(ClientResponse.class, content);
            String entity = response.getEntity(String.class);
            if (response.getStatus() != CREATED.getStatusCode())
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
        client.setConnectTimeout(configuration.getConnectionTimeout());
        client.setReadTimeout(configuration.getReadTimeout());
        client.addFilter(new HTTPBasicAuthFilter(configuration.getUsername(), configuration.getPassword()));
        return client.resource(uri);
    }

    private DataciteServiceException createDoiGetFailedException(int status, String cause) {
        String message = "GET doi failed: HTTP error code: " + status + "\\n + cause" + cause;
        if (isUserError(status)) {
            return new DataciteUserErrorException(message, status);
        }
        return new DataciteServiceException(message, status);
    }

    private DataciteServiceException createDoiGetFailedException(Exception cause) {
        return new DataciteServiceException("DOI get failed: " + cause.getMessage(), cause);
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
        String message = kind + " post failed : HTTP error code : " + status + "\n" + cause;
        if (isUserError(status)) {
            return new DataciteUserErrorException(message, status);
        }
        return new DataciteServiceException(message, status);
    }

    private DataciteServiceException createPostFailedException(String kind, Exception cause) {
        return new DataciteServiceException(kind + " post failed: " + cause.getMessage(), cause);
    }
}
