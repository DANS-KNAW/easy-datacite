package nl.knaw.dans.common.lang.search.exceptions;

public class CannotInstantiateIndexException extends SearchBeanException
{

    /**
     * 
     */
    private static final long serialVersionUID = -2869997092033175444L;

    public CannotInstantiateIndexException()
    {
    }

    public CannotInstantiateIndexException(String message)
    {
        super(message);
    }

    public CannotInstantiateIndexException(Throwable cause)
    {
        super(cause);
    }

    public CannotInstantiateIndexException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
