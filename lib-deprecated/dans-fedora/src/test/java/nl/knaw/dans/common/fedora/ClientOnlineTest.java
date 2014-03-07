package nl.knaw.dans.common.fedora;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.test.Tester;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.client.FedoraClient;

public class ClientOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(ClientOnlineTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void getFedoraClient() throws RepositoryException, IOException
    {
        FedoraClient fedoraClient = getRepository().getFedoraClient();

        String apiAEndpointUrl = fedoraClient.getAPIAEndpointURL().toString();
        if (verbose)
            logger.debug("getAPIAEndpointURL()=" + apiAEndpointUrl);
        assertTrue(apiAEndpointUrl.startsWith(getBaseUrl()));

        String apiMEndpointUrl = fedoraClient.getAPIMEndpointURL().toString();
        if (verbose)
            logger.debug("getAPIMEndpointURL()=" + apiMEndpointUrl);
        assertTrue(apiMEndpointUrl.startsWith(getBaseUrl()));
    }

    @Test
    public void getServerDate() throws RepositoryException
    {
        DateTime serverDate = getRepository().getServerDate();
        assertNotNull(serverDate);
        if (verbose)
            logger.debug("Server date=" + serverDate);
    }

}
