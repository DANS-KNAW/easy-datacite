package nl.knaw.dans.common.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchResult;
import nl.knaw.dans.common.solr.converter.SolrDocumentConverter;
import nl.knaw.dans.common.solr.converter.SolrQueryRequestConverter;
import nl.knaw.dans.common.solr.converter.SolrQueryResponseConverter;
import nl.knaw.dans.common.solr.exceptions.SolrSearchEngineException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is meant to be a very simple wrapper around SolrServer.
 * 
 * @author lobo
 */
public class SolrServerWrapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrServerWrapper.class);

    private SolrServer server;

    public SolrServerWrapper(SolrServer server)
    {
        this.server = server;
    }

    public void index(Collection<IndexDocument> indexDocuments) throws SolrSearchEngineException
    {
        try
        {
            server.add(SolrDocumentConverter.convert(indexDocuments));

            LOGGER.info("Solr: Indexed " + indexDocuments.size() + " documents.");
        }
        catch (SolrServerException e)
        {
            throw new SolrSearchEngineException(e);
        }
        catch (IOException e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public SimpleSearchResult search(SearchRequest request) throws SearchEngineException
    {
        try
        {
            SolrQuery query = SolrQueryRequestConverter.convert(request);
            LOGGER.debug("Solr: searching with " + query.toString() + ".");

            QueryResponse solrResponse = server.query(query);

            SimpleSearchResult result = SolrQueryResponseConverter.convert(solrResponse, request.getIndex());

            LOGGER.debug("Solr: returned " + result.getHits().size() + " documents and " + result.getFacets().size() + " facets.");

            return result;
        }
        catch (SolrServerException e)
        {
            throw new SearchEngineException(e);
        }
    }

    public void deleteByQuery(SearchRequest request) throws SearchEngineException
    {
        try
        {
            SolrQuery query = SolrQueryRequestConverter.convert(request);
            LOGGER.info("Solr: deleting by Query " + query.toString());
            server.deleteByQuery(query.getQuery());
        }
        catch (SolrServerException e)
        {
            throw new SearchEngineException(e);
        }
        catch (IOException e)
        {
            throw new SearchEngineException(e);
        }
    }

    public void deleteByPrimaryKey(List<Field<?>> primaryKeys) throws SearchEngineException
    {
        try
        {
            List<String> ids = new ArrayList<String>(primaryKeys.size());
            String debugMessage = "";
            for (Field<?> primaryKey : primaryKeys)
            {
                String keyStr = SolrUtil.toString(primaryKey.getValue());
                ids.add(keyStr);
                debugMessage += keyStr + ", ";
            }

            LOGGER.info("Solr: deleting " + debugMessage);

            server.deleteById(ids);
        }
        catch (SolrServerException e)
        {
            throw new SearchEngineException(e);
        }
        catch (IOException e)
        {
            throw new SearchEngineException(e);
        }
    }

    public void commit() throws SearchEngineException
    {
        try
        {
            LOGGER.info("Solr: commit");
            server.commit();
        }
        catch (SolrServerException e)
        {
            throw new SearchEngineException(e);
        }
        catch (IOException e)
        {
            throw new SearchEngineException(e);
        }
    }

    public void rollback() throws SearchEngineException
    {
        try
        {
            LOGGER.info("Solr: rollback");
            server.rollback();
        }
        catch (SolrServerException e)
        {
            throw new SearchEngineException(e);
        }
        catch (IOException e)
        {
            throw new SearchEngineException(e);
        }
    }
}
