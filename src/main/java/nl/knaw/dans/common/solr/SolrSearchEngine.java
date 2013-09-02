package nl.knaw.dans.common.solr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.dans.common.lang.search.Document;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.bean.SearchBeanConverter;
import nl.knaw.dans.common.lang.search.bean.SearchBeanFactory;
import nl.knaw.dans.common.lang.search.bean.SearchBeanUtil;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;
import nl.knaw.dans.common.lang.search.simple.SimpleIndexDocument;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchResult;
import nl.knaw.dans.common.solr.exceptions.MissingSearchBeanFactoryException;
import nl.knaw.dans.common.solr.exceptions.MissingTypeFieldException;
import nl.knaw.dans.common.solr.exceptions.ReserverdFieldNameException;
import nl.knaw.dans.common.solr.exceptions.SolrSearchEngineException;
import nl.knaw.dans.common.solr.exceptions.TypeFieldEmptyException;
import nl.knaw.dans.common.solr.exceptions.TypeFieldInvalidTypeException;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrSearchEngine implements SearchEngine
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrServerWrapper.class);

    private URL baseUrl;

    private SearchBeanFactory searchBeanFactory;

    /**
     * The name of the field that hold the type value of search entities. It is up to the searchEngine
     * how the type of the bean is stored. In the case of Solr we add a type field which holds this
     * information.
     */
    final static String SE_TYPE_FIELD_NAME = "type";

    // no need to pool more than one server per index, because
    // CommonsHttpSolrServer handles
    // multithreading and connection pooling
    private Map<String, SolrServerWrapper> serverPool = new HashMap<String, SolrServerWrapper>();

    private boolean multiCore;

    ThreadLocal<TransactionData> transactions = new ThreadLocal<TransactionData>();

    class TransactionData
    {
        private Set<SolrServerWrapper> server = new HashSet<SolrServerWrapper>();

        public void addServer(SolrServerWrapper executor)
        {
            server.add(executor);
        }

        public void commitAll() throws SearchEngineException
        {
            for (SolrServerWrapper executor : server)
                executor.commit();
        }

        public void rollbackAll() throws SearchEngineException
        {
            for (SolrServerWrapper executor : server)
                executor.rollback();
        }
    }

    public SolrSearchEngine(String baseUrl, SearchBeanFactory searchBeanFactory) throws MalformedURLException
    {
        this(baseUrl, searchBeanFactory, true);
    }

    public SolrSearchEngine(String baseUrl, SearchBeanFactory searchBeanFactory, boolean multiCore) throws MalformedURLException
    {
        this.baseUrl = new URL(baseUrl);
        setSearchBeanFactory(searchBeanFactory);
        this.multiCore = multiCore;
    }

    public SearchBeanFactory getSearchBeanFactory()
    {
        return searchBeanFactory;
    }

    public void setSearchBeanFactory(SearchBeanFactory seFactory)
    {
        this.searchBeanFactory = seFactory;
    }

    protected synchronized SolrServerWrapper getServerByName(String indexName) throws SolrSearchEngineException
    {
        if (!multiCore)
            indexName = null;

        SolrServerWrapper result = serverPool.get(indexName);
        if (result == null)
        {
            String coreUrl = baseUrl.getProtocol() + "://" + baseUrl.getHost() + ":" + baseUrl.getPort();
            if (indexName != null && !indexName.equals("") && multiCore)
            {
                boolean pathHasTrailingSlash = baseUrl.getPath().charAt(baseUrl.getPath().length() - 1) == '/';
                boolean pathHasStartingSlash = baseUrl.getPath().charAt(0) == '/';
                coreUrl += (pathHasStartingSlash ? "" : "/") + baseUrl.getPath() + (pathHasTrailingSlash ? "" : "/") + indexName;
            }

            CommonsHttpSolrServer httpServer;
            try
            {
                // for one core there should exist only one CommonsHttpServer
                // object that is created like this, because it holds internally
                // a multi-threaded http connection manager which does
                // connection
                // pooling, but also makes sure threads do not commit each
                // others
                // work.
                httpServer = new CommonsHttpSolrServer(new URL(coreUrl));
            }
            catch (MalformedURLException e)
            {
                throw new SolrSearchEngineException("invalid index name " + indexName, e);
            }

            LOGGER.debug("Solr: Opening new connection to " + coreUrl.toString());

            // config
            httpServer.setConnectionManagerTimeout(3000L);
            httpServer.setConnectionTimeout(3000);
            httpServer.setDefaultMaxConnectionsPerHost(8);
            httpServer.setMaxTotalConnections(32);
            httpServer.setMaxRetries(3);

            result = new SolrServerWrapper(httpServer);
            serverPool.put(indexName, result);
        }

        return result;
    }

    protected synchronized SolrServerWrapper getServerByIndex(Index index) throws SolrSearchEngineException
    {
        return (index == null) ? getServerByName((String) null) : getServerByName(index.getName());
    }

    @SuppressWarnings("unchecked")
    protected void doIndex(Collection<?> objects) throws SearchEngineException, SearchBeanException, SearchBeanConverterException, SearchBeanFactoryException
    {
        // weed out index document based on index name
        Map<Index, Collection<IndexDocument>> indexMap = new HashMap<Index, Collection<IndexDocument>>();
        for (Object object : objects)
        {
            Index index = null;
            if (object instanceof IndexDocument)
                index = ((IndexDocument) object).getIndex();
            else
                index = SearchBeanUtil.getDefaultIndex(object.getClass());

            Collection<IndexDocument> indexDocs = indexMap.get(index);
            if (indexDocs == null)
            {
                indexDocs = new ArrayList<IndexDocument>();
                indexMap.put(index, indexDocs);
            }

            if (object instanceof IndexDocument)
                indexDocs.add((IndexDocument) object);
            else
            {
                SearchBeanConverter converter = getSearchBeanFactory().getSearchBeanConverter(object.getClass());
                IndexDocument doc = converter.toIndexDocument(object);

                // if the document is also a search bean and does not already
                // have a type defined
                // then add the type field to a new index document
                if (doc.getFieldByName(SE_TYPE_FIELD_NAME) != null)
                    throw new ReserverdFieldNameException(SE_TYPE_FIELD_NAME + " is a reserved field for the SolrSearchEngine");

                SimpleIndexDocument copyDoc = new SimpleIndexDocument(doc);
                SimpleField<List<String>> typeField = new SimpleField<List<String>>(SE_TYPE_FIELD_NAME, SearchBeanUtil.getTypeHierarchy(object.getClass()));
                copyDoc.addField(typeField);

                indexDocs.add(copyDoc);
            }
        }

        // send to server
        List<SolrServerWrapper> servers = new ArrayList<SolrServerWrapper>();
        for (Entry<Index, Collection<IndexDocument>> indexEntry : indexMap.entrySet())
        {
            SolrServerWrapper server = getServerByIndex(indexEntry.getKey());
            servers.add(server);
            server.index(indexEntry.getValue());
        }

        doCommit(servers);
    }

    public void doDelete(Collection<?> objects) throws SearchEngineException, SearchBeanException
    {
        Map<Index, List<Field<?>>> delMap = new HashMap<Index, List<Field<?>>>(objects.size());
        for (Object object : objects)
        {
            Field<?> primaryKey = null;
            Index index = null;
            if (object instanceof IndexDocument)
            {
                primaryKey = ((IndexDocument) object).getPrimaryKey();
                index = ((IndexDocument) object).getIndex();
            }
            else
            {
                primaryKey = SearchBeanUtil.getPrimaryKey(object);
                index = SearchBeanUtil.getDefaultIndex(object.getClass());
            }

            List<Field<?>> primaryKeys = delMap.get(index);
            if (primaryKeys == null)
            {
                primaryKeys = new ArrayList<Field<?>>();
                primaryKeys.add(primaryKey);
                delMap.put(index, primaryKeys);
            }
            else
                primaryKeys.add(primaryKey);
        }

        // send to server
        List<SolrServerWrapper> servers = new ArrayList<SolrServerWrapper>();
        for (Entry<Index, List<Field<?>>> delEntry : delMap.entrySet())
        {
            SolrServerWrapper server = getServerByIndex(delEntry.getKey());
            servers.add(server);
            server.deleteByPrimaryKey(delEntry.getValue());
        }

        doCommit(servers);
    }

    private void doCommit(List<SolrServerWrapper> servers) throws SearchEngineException
    {
        TransactionData transaction = transactions.get();
        for (SolrServerWrapper server : servers)
        {
            if (transaction != null)
            {
                transaction.addServer(server);
            }
            else
            {
                server.commit();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public SearchResult<? extends Document> searchDocuments(SearchRequest request) throws SearchEngineException
    {
        return (SearchResult<? extends Document>) getServerByIndex(request.getIndex()).search(request);
    }

    @SuppressWarnings("unchecked")
    public SearchResult<? extends Object> searchBeans(SearchRequest request) throws SearchEngineException
    {
        if (searchBeanFactory == null)
            throw new MissingSearchBeanFactoryException();

        LOGGER.debug("Executing search");

        // add filter fields according to the type of the search beans
        Set<Class<?>> filterBeans = request.getFilterBeans();
        if (filterBeans != null)
        {
            FieldSet filterQueries = request.getFilterQueries();
            for (Class<?> sbClass : filterBeans)
            {
                String sbType;
                try
                {
                    sbType = SearchBeanUtil.getTypeIdentifier(sbClass);
                }
                catch (SearchBeanException e)
                {
                    throw new SolrSearchEngineException(e);
                }
                if (sbType == null)
                    throw new SearchEngineException("could not retrieve search bean type for search bean: " + sbClass.toString());

                if (filterQueries == null)
                    filterQueries = new SimpleFieldSet();
                filterQueries.add(new SimpleField(SE_TYPE_FIELD_NAME, sbType));
            }

            request.setFilterQueries(filterQueries);
        }

        // do the search
        SimpleSearchResult result = getServerByIndex(request.getIndex()).search(request);

        // now build the search entities from the returned documents by the type
        // field
        for (Object hitObj : result.getHits())
        {
            SimpleSearchHit hit = (SimpleSearchHit) hitObj;
            Document doc = (Document) hit.getData();

            // get the type name from the type hierarchy
            Field<?> type = doc.getFieldByName(SE_TYPE_FIELD_NAME);
            if (type == null)
                throw new MissingTypeFieldException();

            Object typeValue = type.getValue();

            if (!(typeValue instanceof String || typeValue instanceof Collection))
                throw new TypeFieldInvalidTypeException();

            String typeName = "";
            if (typeValue instanceof String)
                typeName = (String) typeValue;
            else if (typeValue instanceof Collection)
            {
                Object[] typeHierarchy = ((Collection) typeValue).toArray();
                Object firstType = typeHierarchy[0];
                if (!(firstType instanceof String))
                    throw new TypeFieldInvalidTypeException();
                typeName = (String) firstType;
            }
            if (typeName.equals(""))
                throw new TypeFieldEmptyException();

            // instantiate the SearchBean based on the type name
            Object searchBean = null;
            try
            {
                searchBean = searchBeanFactory.createSearchBean(typeName, doc);
            }
            catch (SearchBeanFactoryException e)
            {
                throw new SolrSearchEngineException(e);
            }
            catch (SearchBeanException e)
            {
                throw new SolrSearchEngineException(e);
            }

            // replace the document with the bean
            hit.setData(searchBean);
        }

        return (SearchResult<Object>) result;
    }

    public void deleteBean(final Object searchBean) throws SearchEngineException
    {
        try
        {
            doDelete(new ArrayList<Object>(1)
            {
                {
                    add(searchBean);
                }
            });
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void deleteDocument(final IndexDocument indexDocument) throws SearchEngineException
    {
        try
        {
            doDelete(new ArrayList<IndexDocument>(1)
            {
                {
                    add(indexDocument);
                }
            });
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void indexBean(final Object searchBean) throws SearchEngineException, SearchEngineException
    {
        try
        {
            doIndex(new ArrayList<Object>(1)
            {
                {
                    add(searchBean);
                }
            });
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void indexDocument(final IndexDocument indexDocument) throws SearchEngineException
    {
        try
        {
            doIndex(new ArrayList<Object>(1)
            {
                {
                    add(indexDocument);
                }
            });
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void deleteBeans(Collection<? extends Object> searchBeans) throws SearchEngineException
    {
        try
        {
            doDelete(searchBeans);
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void deleteDocuments(Collection<? extends IndexDocument> indexDocuments) throws SearchEngineException
    {
        try
        {
            doDelete(indexDocuments);
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void indexBeans(Collection<? extends Object> searchBeans) throws SearchEngineException
    {
        try
        {
            doIndex(searchBeans);
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    public void indexDocuments(Collection<? extends IndexDocument> indexDocuments) throws SearchEngineException
    {
        try
        {
            doIndex(indexDocuments);
        }
        catch (Exception e)
        {
            throw new SolrSearchEngineException(e);
        }
    }

    @Override
    public void beginTransaction()
    {
        if (transactions.get() == null)
        {
            transactions.set(new TransactionData());
        }
    }

    @Override
    public void commit() throws SearchEngineException
    {
        TransactionData transactionData = transactions.get();
        if (transactionData != null)
        {
            transactions.set(null);
            transactionData.commitAll();
        }
    }

    @Override
    public void rollback() throws SearchEngineException
    {
        TransactionData transactionData = transactions.get();
        if (transactionData != null)
        {
            transactions.set(null);
            transactionData.rollbackAll();
        }
    }

}
