package nl.knaw.dans.easy.business.dataset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.net.ConnectException;
import java.net.URL;

import nl.knaw.dans.easy.business.dataset.PidClient;
import nl.knaw.dans.easy.business.dataset.PidClient.Type;
import nl.knaw.dans.easy.util.ApacheHttpClientFacade;

import org.junit.BeforeClass;
import org.junit.Test;

public class PidClientOnlineTest {

    private static URL service;

    @BeforeClass
    public static void running() throws Exception {
        int port = 8084;
        service = new URL("http://localhost:" + port);
        try {
            new URL(service, "/pids").openConnection();
        }
        catch (ConnectException e) {
            // ignore tests (nice to have: start the service if not jet running on some port)
            assumeTrue("please launch mvn goal 'jetty:run' on pid-generator project with jvm: -Djetty.port=" + port, false);
        }
    }

    @Test
    public void doi() throws Exception {
        String pid = new PidClient(service, new ApacheHttpClientFacade()).getPid(Type.doi);
        assertThat(pid, startsWith("10.5072/dans-"));
        assertThat(pid.length(), equalTo(21));
    }

    @Test
    public void urn() throws Exception {
        String pid = new PidClient(service, new ApacheHttpClientFacade()).getPid(Type.urn);
        assertThat(pid, startsWith("urn:nbn:nl:ui:13-"));
        assertThat(pid.length(), equalTo(24));
    }
}
