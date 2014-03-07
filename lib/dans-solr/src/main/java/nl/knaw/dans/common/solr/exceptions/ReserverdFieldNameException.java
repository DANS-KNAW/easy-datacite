package nl.knaw.dans.common.solr.exceptions;

public class ReserverdFieldNameException extends SolrSearchEngineException
{
    private static final long serialVersionUID = -3719582200220723687L;

    public ReserverdFieldNameException()
    {
    }

    public ReserverdFieldNameException(String message)
    {
        super(message);
    }

    public ReserverdFieldNameException(Throwable cause)
    {
        super(cause);
    }

    public ReserverdFieldNameException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
