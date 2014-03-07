package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class CollectionException extends RepositoryException
{
    private static final long serialVersionUID = 5362920496090834533L;

    public CollectionException()
    {
    }

    public CollectionException(String message)
    {
        super(message);
    }

    public CollectionException(Throwable cause)
    {
        super(cause);
    }

    public CollectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
