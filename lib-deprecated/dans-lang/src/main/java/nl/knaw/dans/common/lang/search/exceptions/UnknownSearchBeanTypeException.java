package nl.knaw.dans.common.lang.search.exceptions;

public class UnknownSearchBeanTypeException extends SearchBeanFactoryException
{

    /**
     * 
     */
    private static final long serialVersionUID = -1290838131484782614L;

    public UnknownSearchBeanTypeException()
    {
    }

    public UnknownSearchBeanTypeException(String message)
    {
        super(message);
    }

    public UnknownSearchBeanTypeException(Throwable cause)
    {
        super(cause);
    }

    public UnknownSearchBeanTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
