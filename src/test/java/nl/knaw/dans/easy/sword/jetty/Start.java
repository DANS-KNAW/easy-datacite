package nl.knaw.dans.easy.sword.jetty;

import nl.knaw.dans.common.lang.test.ClassPathHacker;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Start
{
    public static final int PORT = 8083;
    public static final int SSL_PORT = 8449;
    private static final int SLEEPTIME = 5000;
    private static final int EXIT_CODE = 100;

    private Start()
    {
        // Make it impossible to instantiate
    }

    /**
     * Start Jetty. Allows commands like:<br>
     * <br>
     * </code> http://EASYUSER:PASSWORD@localhost:8083/servicedocument </code> <br>
     * <br>
     * <code>
     * curl -i --data-binary @src/test/resources/input/data-plus-meta.zip http://EASYUSER:PASSWORD@localhost:8083/deposit
     * </code>
     * <br>
     * <ul>
     * <li>To prevent changes of the repository, add: <code>-H "X-No-Op: true"</code></li>
     * <li>To return also a license document, add: <code>-H "X-Verbose: true"</code></li>
     * </ul>
     * 
     * @param args
     *        alternative port numbers, 1st: http, 2nd: https
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception // NOPMD
    {
        int port = args.length > 0 ? Integer.valueOf(args[0]) : PORT;
        int sslPort = args.length > 1 ? Integer.valueOf(args[1]) : SSL_PORT;
        final Server server = createServer(port, sslPort);

        try
        {
            server.start();
            System.out.println(">>> STARTED EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP"); // NOPMD
            while (System.in.available() == 0)
                Thread.sleep(SLEEPTIME); // NOPMD
            server.stop();
            server.join();
        }
        catch (final Exception e)
        {
            e.printStackTrace(); // NOPMD
            System.exit(EXIT_CODE); // NOPMD
        }
    }

    public static Server createServer(int port, int sslPort) throws Exception
    {
        ClassPathHacker.addFile("src/main/resources/");

        final Server server = new Server(); // NOPMD

        System.out.println(">>> Creating connector on port " + port);
        final SocketConnector connector = new SocketConnector(); // NOPMD
        connector.setPort(port); // NOPMD

        Connector[] connectors;
        if (!"true".equalsIgnoreCase(System.getProperty("nl.knaw.dans.easy.web.ssl")))
        {
            connectors = new Connector[] {connector};
        }
        else
        {
            System.out.println(">>> " + "Creating sslConnector on port " + sslPort);
            connector.setConfidentialPort(sslPort);

            // ssl connector
            final SslSocketConnector sslConnector = new SslSocketConnector();
            sslConnector.setPort(sslPort);
            sslConnector.setKeystore("/etc/keystore");
            sslConnector.setPassword("jetty01");
            sslConnector.setKeyPassword("jetty01");
            sslConnector.setTruststore("/etc/keystore");
            sslConnector.setTrustPassword("jetty01");
            connectors = new Connector[] {connector, sslConnector};
        }

        server.setConnectors(connectors);

        final WebAppContext webAppContext = new WebAppContext(); // NOPMD
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        webAppContext.setWar("src/main/webapp");
        server.addHandler(webAppContext);
        return server;
    }
}
