package nl.knaw.dans.common.solr.exceptions;

import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;

public class SolrSearchEngineException extends SearchEngineException
{
    private static final long serialVersionUID = 6427515541489625025L;

    public SolrSearchEngineException()
    {
    }

    public SolrSearchEngineException(String message)
    {
        super(message);
    }

    public SolrSearchEngineException(Throwable cause)
    {
        super(cause);
    }

    public SolrSearchEngineException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
