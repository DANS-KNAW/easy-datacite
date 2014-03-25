package nl.knaw.dans.fcrepo.oai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.trippi.RDFFormat;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import proai.error.RepositoryException;
import fedora.client.FedoraClient;

public class RiConnector
{
    public static final String QL_SPARQL = "sparql";
    public static final String QL_ITQL = "itql";

    private static final Logger logger = Logger.getLogger(RiConnector.class);

    private String queryLanguage;

    private final FedoraClient m_queryClient;

    public RiConnector(FedoraClient queryClient)
    {
        this.m_queryClient = queryClient;
    }

    public String getQueryLanguage()
    {
        if (queryLanguage == null)
        {
            queryLanguage = QL_SPARQL;
        }
        return queryLanguage;
    }

    public void setQueryLanguage(String queryLanguage)
    {
        this.queryLanguage = queryLanguage;
    }

    public TupleIterator getTuples(String query) throws RepositoryException
    {
        TupleIterator tupleIterator = null;
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("lang", getQueryLanguage());
        parameters.put("query", query);
        parameters.put("stream", "true"); // stream immediately from server
        logger.debug("Start performing query ...");
        long start = System.currentTimeMillis();
        try
        {
            tupleIterator = m_queryClient.getTuples(parameters);
        }
        catch (IOException e)
        {
            throw new RepositoryException("Error getting tuples from Fedora: " + e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        logger.debug("Query took " + (end - start) + " msec.");
        return tupleIterator;
    }

    /**
     * Get the results of the given query as a temporary CSV file.
     * 
     * @param query
     * @return file with result set
     * @throws RepositoryException
     * @throws IOException
     */
    public File getCSVResults(String query) throws RepositoryException, IOException
    {
        File tempFile = null;
        OutputStream out = null;
        try
        {
            tempFile = File.createTempFile("dans-oai-riconnector", ".csv");
            tempFile.deleteOnExit(); // just in case
            out = new FileOutputStream(tempFile);
        }
        catch (IOException e)
        {
            throw new RepositoryException("Error creating tempFile for query result.", e);
        }

        try
        {
            logger.debug("Calling RiConnector.getTuples() with query:\n" + query);
            TupleIterator tuples = getTuples(query);
            logger.debug("Saving query results to disk...");
            tuples.toStream(out, RDFFormat.CSV);
        }
        catch (RepositoryException e)
        {
            tempFile.delete();
            throw new RepositoryException("Error getting tuples from Fedora: ", e);
        }
        catch (TrippiException e)
        {
            tempFile.delete();
            throw new RepositoryException("Error streaming result to tempFile: ", e);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
        logger.info("Saved query result to tempFile (" + tempFile.length() + " bytes).");

        return tempFile;
    }

}
