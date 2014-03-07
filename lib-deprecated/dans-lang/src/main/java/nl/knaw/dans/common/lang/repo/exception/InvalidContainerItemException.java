package nl.knaw.dans.common.lang.repo.exception;

public class InvalidContainerItemException extends CollectionException
{
    private static final long serialVersionUID = 1804186327994659219L;

    public InvalidContainerItemException()
    {
    }

    public InvalidContainerItemException(String message)
    {
        super(message);
    }

    public InvalidContainerItemException(Throwable cause)
    {
        super(cause);
    }

    public InvalidContainerItemException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
