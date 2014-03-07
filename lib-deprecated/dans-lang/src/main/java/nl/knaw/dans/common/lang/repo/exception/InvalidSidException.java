package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class InvalidSidException extends RepositoryException
{
    private static final long serialVersionUID = 2197799726526415187L;

    public InvalidSidException()
    {
    }

    public InvalidSidException(String message)
    {
        super(message);
    }

    public InvalidSidException(Throwable cause)
    {
        super(cause);
    }

    public InvalidSidException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
