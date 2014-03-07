package nl.knaw.dans.common.lang.search.exceptions;

public class SearchEngineException extends Exception
{
    private static final long serialVersionUID = 4046935885090647574L;

    public SearchEngineException()
    {
    }

    public SearchEngineException(String message)
    {
        super(message);
    }

    public SearchEngineException(Throwable cause)
    {
        super(cause);
    }

    public SearchEngineException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
