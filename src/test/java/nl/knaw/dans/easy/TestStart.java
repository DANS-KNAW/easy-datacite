package nl.knaw.dans.easy;

import java.io.*;

import org.junit.*;
import org.mortbay.jetty.Server;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class TestStart
{

    @Ignore("runs in error(s)")
    @Test
    public void readyForManualBrowserTests() throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream detour = new PrintStream(out);
        final PrintStream saved = System.err;
        System.setErr(detour);
        final Server server = Start.createServer(Start.PORT, Start.SSL_PORT);
        server.start();
        server.stop();
        System.setErr(saved);
        assertThat(out.toString(), containsString("WARNING: Wicket is running in DEVELOPMENT mode. "));
    }
}
