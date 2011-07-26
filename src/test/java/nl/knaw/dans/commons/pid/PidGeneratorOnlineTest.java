package nl.knaw.dans.commons.pid;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;

import java.sql.*;

import org.junit.*;
import org.slf4j.*;
import static nl.knaw.dans.commons.pid.PidTableProperties.*;


public class PidGeneratorOnlineTest
{
    private static final Logger logger = LoggerFactory.getLogger(PidGenerator.class);

    private static final String PREFIX = URN_PREFIX+DANS_PREFIX;

    private static Connection connection;
    private static PidGenerator defaultGenerator;

    @BeforeClass
    public static void getConnection() throws Exception
    {
        if (connection == null)
        {
            // note that Maven needs dependencies for supported databases
            Class.forName("org.postgresql.Driver");
            final String url = "jdbc:postgresql://localhost/pid_generator";

            final String user = "postgres";
            final String password = "postgres";

            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);

            defaultGenerator = new PidGenerator(connection);
        }
    }

    @AfterClass
    public static void closeConnection() throws Exception
    {
        connection.close();
    }

    @Before
    public void initDatabase() throws Exception
    {
        logger.debug("");
        final long value = 0L;

        defaultGenerator.updateId(value);
        final Long actualResult = defaultGenerator.fetchId();

        assertThat(actualResult, equalTo(value));
    }

    @Test (expected = PidGenerator.PidException.class)
    public void readError() throws Exception
    {
        final String invalidPrefix = "blabla";
        final PidGenerator linmpingGenerator = new PidGenerator(connection,invalidPrefix);
        final String actual = linmpingGenerator.getNextPersistentIdentifierUrn();
        assertThat(actual, equalTo(""));
    }

    @Test
    public void endToEnd() throws Exception
    {
        // just get the third
        defaultGenerator.getNextPersistentIdentifierUrn();
        defaultGenerator.getNextPersistentIdentifierUrn();
        final String actualUrn = defaultGenerator.getNextPersistentIdentifierUrn();

        final long actualPid = defaultGenerator.fetchId();//.fromUrn(actualUrn);        
        assertThat(actualPid, equalTo(230659027L));
        
        final String pidStrWithoutSeparator = "3tbtn7";
        final String pidStr = pidStrWithoutSeparator.substring(0, PidConverter.SEPARATOR_POSITION) + PidConverter.SEPARATOR + pidStrWithoutSeparator.substring(PidConverter.SEPARATOR_POSITION);
        assertThat(actualUrn, equalTo(PREFIX + pidStr));
    }

}