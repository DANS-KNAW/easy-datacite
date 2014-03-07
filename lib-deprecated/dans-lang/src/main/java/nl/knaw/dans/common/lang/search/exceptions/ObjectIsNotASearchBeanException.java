package nl.knaw.dans.common.lang.search.exceptions;

public class ObjectIsNotASearchBeanException extends SearchBeanException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1788888384898458025L;

    public ObjectIsNotASearchBeanException()
    {
    }

    public ObjectIsNotASearchBeanException(String message)
    {
        super(message);
    }

    public ObjectIsNotASearchBeanException(Throwable cause)
    {
        super(cause);
    }

    public ObjectIsNotASearchBeanException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
