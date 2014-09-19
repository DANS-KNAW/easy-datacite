package nl.knaw.dans.easy.rest;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

// CHECKSTYLE:OFF
/**
 * Start tester.
 */
public final class Start // NOPMD
{
    private static final Logger log = LoggerFactory.getLogger(Start.class);
    private static final int MAX_IDLE_TIME = 1000 * 60 * 60;
    private static final int PORT = 8081;
    private static final int SSL_PORT = 8444;
    private static final int SLEEPTIME = 5000;
    private static final int EXIT_CODE = 100;

    private Start() {
        // Make it impossible to instantiate
    }

    /**
     * Start Jetty.
     * 
     * @param args
     *        arguments
     * @throws Exception
     *         Exception
     */
    public static void main(final String[] args) throws Exception // NOPMD
    {
        printLogbackStatus();
        int port = args.length > 0 ? Integer.valueOf(args[0]) : PORT;
        int sslPort = args.length > 1 ? Integer.valueOf(args[1]) : SSL_PORT;
        final Server server = createServer(port, sslPort);

        try {
            log.info(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP"); // NOPMD
            server.start();
            while (System.in.available() == 0) {
                Thread.sleep(SLEEPTIME); // NOPMD
            }
            server.stop();
            server.join();
        }
        catch (final Exception e) {
            e.printStackTrace(); // NOPMD
            System.exit(EXIT_CODE); // NOPMD
        }
    }

    static Server createServer(int port, int sslPort) {
        log.info(">>> Configuration folder = {}", ClassLoader.getSystemResource("conf"));

        // @formatter:off
        /*
        <configuration>
            <systemProperties>
            <systemProperty>
            <name>org.mortbay.jetty.Request.maxFormContentSize</name>
            <value>500000</value>
            </systemProperty>
            </systemProperties>
        </configuration>
        
        In order to prevent java.lang.IllegalStateException: Form too large214892>200000
         */
        // @formatter:on
        System.setProperty("org.mortbay.jetty.Request.maxFormContentSize", "500000");
        System.setProperty("java.naming.factory.url.pkgs", "org.mortbay.naming");
        System.setProperty("java.naming.factory.initial", "org.mortbay.naming.InitialContextFactory");

        final Server server = new Server(); // NOPMD

        log.info(">>> Creating connector on port {}", port);
        final SocketConnector connector = new SocketConnector(); // NOPMD
        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(MAX_IDLE_TIME);
        connector.setSoLingerTime(-1);
        connector.setPort(port); // NOPMD

        Connector[] connectors;
        if ("true".equalsIgnoreCase(System.getProperty("nl.knaw.dans.easy.web.ssl"))) {
            log.info(">>> " + "Creating sslConnector on port {}", sslPort);
            connector.setConfidentialPort(sslPort);

            // ssl connector
            final SslSocketConnector sslConnector = new SslSocketConnector();
            sslConnector.setMaxIdleTime(MAX_IDLE_TIME);
            sslConnector.setSoLingerTime(-1);

            sslConnector.setPort(sslPort);
            sslConnector.setKeystore("/etc/keystore");
            sslConnector.setPassword("jetty01");
            sslConnector.setKeyPassword("jetty01");
            sslConnector.setTruststore("/etc/keystore");
            sslConnector.setTrustPassword("jetty01");
            connectors = new Connector[] {connector, sslConnector};
        } else {
            connectors = new Connector[] {connector};
        }

        server.setConnectors(connectors);

        final WebAppContext webAppContext = new WebAppContext(); // NOPMD
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        webAppContext.setWar("src/main/webapp");
        server.addHandler(webAppContext);
        return server;
    }

    private static void printLogbackStatus() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
    }
}
