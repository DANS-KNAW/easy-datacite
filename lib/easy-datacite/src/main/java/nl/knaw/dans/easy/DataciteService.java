package nl.knaw.dans.easy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

public class DataciteService {

    private static final String CONTENT_TYPE = "application/xml;charset=UTF-8";

    private static final Pattern createdResultPattern = Pattern.compile("dois created:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern updatedResultPattern = Pattern.compile("dois updated:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private static Logger logger = LoggerFactory.getLogger(DataciteService.class);

    private final DataciteServiceConfiguration configuration;

    private DataciteResourcesBuilder resourcesBuilder;

    public DataciteService(DataciteServiceConfiguration configuration) {
        this.configuration = configuration;
        resourcesBuilder = new DataciteResourcesBuilder(configuration.getXslEmd2datacite(), configuration.getDatasetResolver());
    }

    public void create(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        logResult("Creating", getCreatedCount(result) != emds.length, result, emds);
    }

    public void update(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        logResult("Updating", getUpdatedCount(result) != emds.length, result, emds);
    }

    public void createOrUpdate(EasyMetadata... emds) throws DataciteServiceException {
        String result = post(resourcesBuilder.create(emds));
        logResult("Creating/updating", false, result, emds);
    }

    private void logResult(String crudType, boolean warn, String result, EasyMetadata... emds) throws DataciteServiceException {
        String firstDOI = emds[0].getEmdIdentifier().getDansManagedDoi();
        String format = crudType + " %s DOIs resulted in %s. First DOI %s";
        String message = String.format(format, emds.length, result, firstDOI);
        if (getCreatedCount(result) + getUpdatedCount(result) != emds.length) {
            logger.error(message);
            if (getCreatedCount(result) >= 0 && getUpdatedCount(result) >= 0)
                throw new DataciteServiceException(message); // we could interpret both numbers but they don't add up correctly
        } else if (warn)
            logger.warn(message);
        else
            logger.info(message);
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

    private int getCreatedCount(String result) {
        return getCountFromPatternAndMessage(createdResultPattern, result);
    }

    private int getUpdatedCount(String result) {
        return getCountFromPatternAndMessage(updatedResultPattern, result);
    }

    private int getCountFromPatternAndMessage(Pattern p, String msg) {
        Matcher m = p.matcher(msg);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException e) {
                countNoutFoundError(p.toString());
            }
        }
        countNoutFoundError(p.toString());
        return -1; // default, not only satisfies compiler, but also allows further checks
    }

    private void countNoutFoundError(String patternString) {
        logger.error("DOI post succeeded but could not extract result count with pattern: " + patternString);
    }
}
