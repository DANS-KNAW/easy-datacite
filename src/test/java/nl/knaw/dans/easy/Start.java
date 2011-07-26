package nl.knaw.dans.easy;

import nl.knaw.dans.easy.util.EasyHome;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

// CHECKSTYLE:OFF
/**
 * Start tester.
 */
public final class Start // NOPMD
{
    /**
     * MAX_IDLE_TIME.
     */
    private static final int MAX_IDLE_TIME = 1000 * 60 * 60;
    /**
     * PORT.
     */
    static final int PORT = 8081;
    
    static final int SSL_PORT = 8444;
    /**
     * SLEEPTIME.
     */
    private static final int SLEEPTIME = 5000;
    /**
     * EXIT_CODE.
     */
    private static final int EXIT_CODE = 100;

    /**
     * Default constructor.
     */
    private Start()
    {
        // Make it impossible to instantiate
    }

    /**
     * Start Jetty.
     *
     * @param args arguments
     * @throws Exception Exception
     */
    public static void main(final String[] args) throws Exception // NOPMD
    {
        if(EasyHome.getValue() == null)
        {
            System.err.println("Stopping EASY because of missing system property for home directory!"
                    + "\n\tPlease specify the system property '" + EasyHome.EASY_HOME_KEY + "'");
            System.exit(EXIT_CODE);
        }
        
        int port = args.length > 0 ? Integer.valueOf(args[0]) : PORT;
        int sslPort = args.length > 1 ? Integer.valueOf(args[1]) : SSL_PORT;
        final Server server = createServer(port, sslPort);

        try
        {
            System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP"); // NOPMD
            server.start();
            while (System.in.available() == 0)
            {
                Thread.sleep(SLEEPTIME); // NOPMD
            }
            server.stop();
            server.join();
        }
        catch (final Exception e)
        {
            e.printStackTrace(); // NOPMD
            System.exit(EXIT_CODE); // NOPMD
        }
    }

    static Server createServer(int port, int sslPort)
    {
        System.err.println(">>> Configuration folder = " + ClassLoader.getSystemResource("conf"));
        
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
        System.setProperty("org.mortbay.jetty.Request.maxFormContentSize", "500000");

        final Server server = new Server(); // NOPMD
        
        System.out.println(">>> Creating connector on port " + port);
        final SocketConnector connector = new SocketConnector(); // NOPMD
        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(MAX_IDLE_TIME);
        connector.setSoLingerTime(-1);
        connector.setPort(port); // NOPMD
        
        Connector[] connectors;
        if ("true".equalsIgnoreCase(System.getProperty("nl.knaw.dans.easy.web.ssl")))
        {
            System.out.println(">>> " +
            		"Creating sslConnector on port " + sslPort);
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
            connectors = new Connector[] { connector, sslConnector };
        }
        else
        {
            connectors = new Connector[] { connector };
        }
        
        server.setConnectors(connectors);

        final WebAppContext webAppContext = new WebAppContext(); // NOPMD
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        webAppContext.setWar("src/main/webapp");

        // START JMX SERVER
        // MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        // server.getContainer().addEventListener(mBeanContainer);
        // mBeanContainer.start();

        server.addHandler(webAppContext);
        return server;
    }
}
