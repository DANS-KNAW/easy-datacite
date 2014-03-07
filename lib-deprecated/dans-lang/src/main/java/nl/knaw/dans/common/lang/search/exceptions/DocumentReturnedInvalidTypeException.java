package nl.knaw.dans.common.lang.search.exceptions;

public class DocumentReturnedInvalidTypeException extends SearchBeanFactoryException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1941994740532549508L;

    public DocumentReturnedInvalidTypeException()
    {
    }

    public DocumentReturnedInvalidTypeException(String message)
    {
        super(message);
    }

    public DocumentReturnedInvalidTypeException(Throwable cause)
    {
        super(cause);
    }

    public DocumentReturnedInvalidTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
