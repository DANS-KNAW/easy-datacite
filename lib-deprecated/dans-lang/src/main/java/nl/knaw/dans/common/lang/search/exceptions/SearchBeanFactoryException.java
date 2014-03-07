package nl.knaw.dans.common.lang.search.exceptions;

public class SearchBeanFactoryException extends Exception
{
    private static final long serialVersionUID = -1762667862874842892L;

    public SearchBeanFactoryException()
    {
    }

    public SearchBeanFactoryException(String message)
    {
        super(message);
    }

    public SearchBeanFactoryException(Throwable cause)
    {
        super(cause);
    }

    public SearchBeanFactoryException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
