package nl.knaw.dans.common.lang.repo.exception;

public class InvalidContainerException extends CollectionException
{
    private static final long serialVersionUID = 180418632567559219L;

    public InvalidContainerException()
    {
    }

    public InvalidContainerException(String message)
    {
        super(message);
    }

    public InvalidContainerException(Throwable cause)
    {
        super(cause);
    }

    public InvalidContainerException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
