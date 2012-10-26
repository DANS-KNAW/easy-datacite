package nl.knaw.dans.commons.pid;

import static nl.knaw.dans.commons.pid.PidTableProperties.DANS_PREFIX;
import static nl.knaw.dans.commons.pid.PidTableProperties.URN_PREFIX;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;

import nl.knaw.dans.common.lang.test.Tester;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidGeneratorOnlineTest
{
    private static final Logger logger = LoggerFactory.getLogger(PidGenerator.class);

    private static final String PREFIX = URN_PREFIX + DANS_PREFIX;

    private static Connection connection;
    private static PidGenerator defaultGenerator;

    private static long originalValue;

    @BeforeClass
    public static void getConnection() throws Exception
    {
        if (connection == null)
        {
            // note that Maven needs dependencies for supported databases
            Class.forName(Tester.getString("pid.database.driver"));
            final String url = Tester.getString("pid.database.url");

            final String user = Tester.getString("pid.database.username");
            final String password = Tester.getString("pid.database.userpass");

            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);

            defaultGenerator = new PidGenerator(connection);
        }
        originalValue = defaultGenerator.fetchId();
        logger.debug("original value was " + originalValue);
    }

    @AfterClass
    public static void cleanup() throws Exception
    {
        if (connection != null)
        {
            defaultGenerator.updateId(originalValue);
            logger.debug("database state restored. original value is " + originalValue);
            connection.close();
            logger.debug("Closed connection");
        }
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

    @Test(expected = PidGenerator.PidException.class)
    public void readError() throws Exception
    {
        final String invalidPrefix = "blabla";
        final PidGenerator linmpingGenerator = new PidGenerator(connection, invalidPrefix);
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
        final String pidStr = pidStrWithoutSeparator.substring(0, PidConverter.SEPARATOR_POSITION) + PidConverter.SEPARATOR
                + pidStrWithoutSeparator.substring(PidConverter.SEPARATOR_POSITION);
        assertThat(actualUrn, equalTo(PREFIX + pidStr));
    }

}
