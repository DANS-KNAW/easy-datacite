package nl.knaw.dans.common.lang.repo.exception;

public class LockAcquireTimeoutException extends Exception
{
    private static final long serialVersionUID = -8124132473122641103L;

    public LockAcquireTimeoutException()
    {
    }

    public LockAcquireTimeoutException(String message)
    {
        super(message);
    }

    public LockAcquireTimeoutException(Throwable cause)
    {
        super(cause);
    }

    public LockAcquireTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
