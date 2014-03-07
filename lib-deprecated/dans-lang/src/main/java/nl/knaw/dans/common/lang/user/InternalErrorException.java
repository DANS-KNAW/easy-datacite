package nl.knaw.dans.common.lang.user;

/** Use this when catching exceptions that are result of programming errors
 * and should be handled as runtime exceptions
 */
public class InternalErrorException extends RuntimeException
{
    private static final long serialVersionUID = -3027043667083250893L;

    public InternalErrorException()
    {
    }

    public InternalErrorException(String message)
    {
        super(message);
    }

    public InternalErrorException(Throwable cause)
    {
        super(cause);
    }

    public InternalErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
