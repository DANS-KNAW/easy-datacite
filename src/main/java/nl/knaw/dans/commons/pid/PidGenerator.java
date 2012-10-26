package nl.knaw.dans.commons.pid;

import static nl.knaw.dans.commons.pid.PidTableProperties.DANS_PREFIX;
import static nl.knaw.dans.commons.pid.PidTableProperties.TABLE_NAME;
import static nl.knaw.dans.commons.pid.PidTableProperties.URN_PREFIX;
import static nl.knaw.dans.commons.pid.PidTableProperties.VALUE_COLUMN_NAME;
import static nl.knaw.dans.commons.pid.PidTableProperties.WHERE_CLAUSE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidGenerator
{
    private static Logger logger = LoggerFactory.getLogger(PidGenerator.class);

    private final Connection connection;
    private final String prefix;
    private final String whereClause;

    public static class PidException extends RuntimeException
    {
        // private constructors so anyone can catch, only owner can throw

        private static final long serialVersionUID = 1L;

        private PidException(final String explanation, final Throwable cause)
        {
            super(explanation, cause);
            logger.error(explanation, cause);
        }

        private PidException(String explanation)
        {
            super(explanation);
            logger.error(explanation);
        }
    };

    /**
     * Creates a generator for persistent identifiers. A database with a row per prefix remembers
     * the last generated id.
     *
     * @param connection connection with the database.
     * @param prefix the first characters after "urn:nbn:nl:ui:"
     * @throws PidException in case of problems with a connection property
     */
    public PidGenerator(Connection connection, String prefix) throws PidException
    {
        this.connection = connection;
        try
        {
            this.connection.setAutoCommit(false);
        }
        catch (SQLException exception)
        {
            throw new PidException("could not switch off autocommit of pid generator", exception);
        }
        this.prefix = URN_PREFIX + prefix;
        whereClause = String.format(WHERE_CLAUSE, this.prefix);
    }

    public PidGenerator(PidConnectionConfiguration connection, String prefix) throws PidException
    {
        this.prefix = URN_PREFIX + prefix;
        whereClause = String.format(WHERE_CLAUSE, this.prefix);

        try
        {
            Class.forName(connection.getDriverClass());
        }
        catch (ClassNotFoundException exception)
        {
            throw new PidException("pom file of pid generator might need a dependency to support database driver " + connection.getDriverClass(), exception);
        }

        try
        {
            this.connection = DriverManager.getConnection(connection.getUrl(), connection.getUsername(), connection.getPassword());
        }
        catch (SQLException exception)
        {
            throw new PidException("could not connect to database of pid generator", exception);
        }
    }

    public PidGenerator(PidConnectionConfiguration connection) throws PidException
    {
        this(connection, DANS_PREFIX);
    }

    public PidGenerator(Connection connection) throws PidException
    {
        this(connection, DANS_PREFIX);
    }

    private synchronized Long getNextPersistentIdentifier()
    {
        // old returns what is in the database, logs the next with the fileWrite, logs next.next
        // with the logger

        final Long lastId;
        try
        {
            lastId = fetchId();
        }
        catch (final SQLException exception)
        {
            throw new PidException("Unable to read last generated.", exception);
        }

        final long nextId = PidCaculator.getNext(lastId);
        try
        {
            updateId(nextId);
        }
        catch (final SQLException exception)
        {
            rollBack();
            throw new PidException("Unable to read last generated.", exception);
        }
        return nextId;
    }

    private void rollBack()
    {
        // we want to log the roll back problem, but return the original problem
        try
        {
            connection.rollback();
        }
        catch (final SQLException exception)
        {
            logger.error("Unable to rollback database.", exception);
        }
    }

    void updateId(final long id) throws SQLException
    {
        logger.info(String.format("PidGenerator - next (%s , %s)", id, PidConverter.encode(id)));
        if (connection == null)
            throw new NullPointerException("no connection with the persisten identifier database");
        final Statement stat = connection.createStatement();
        final String statement = "UPDATE " + TABLE_NAME + " SET " + VALUE_COLUMN_NAME + " = " + id;
        stat.execute(statement + whereClause);
        stat.close();
        connection.commit();
    }

    long fetchId() throws SQLException
    {
        if (connection == null)
            throw new NullPointerException("no connection with the persisten identifier database");
        Statement stat = null;
        ResultSet resultSet = null;
        long id;
        try
        {
            stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            final String statement = "SELECT " + VALUE_COLUMN_NAME + " FROM " + TABLE_NAME + whereClause;
            resultSet = stat.executeQuery(statement);
            boolean canMove = resultSet.next();
            if (!canMove)
            {
                throw new PidException("No id in result set: " + statement);
            }
            id = resultSet.getLong(1);
        }
        finally
        {
            if (stat != null)
                stat.close();
            if (resultSet != null)
                resultSet.close();
        }

        logger.info(String.format("PidGenerator - last (%s = %s)", id, PidConverter.encode(id)));
        return id;
    }

    public String getNextPersistentIdentifierUrn()
    {
        Long identifier = getNextPersistentIdentifier();
        String encoded = PidConverter.toUrn(prefix, identifier);
        logger.info("PidGenerator - next (" + identifier + " = " + encoded + ")");
        return encoded;
    }

    public void close()
    {
        logger.info("Closing " + this.getClass().getSimpleName());
        closeConnection();
    }

    private void closeConnection()
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
                logger.info("Closed connection for " + this.getClass().getName());
            }
        }
        catch (SQLException e)
        {
            logger.error("Unable to close connection: ", e);
        }

    }
}
