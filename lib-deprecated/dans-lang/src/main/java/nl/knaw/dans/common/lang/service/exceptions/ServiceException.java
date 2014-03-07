/**
 *
 */
package nl.knaw.dans.common.lang.service.exceptions;

/**
 * A not recoverable exception from the service layer.
 *
 * @author Herman Suijs
 */
public class ServiceException extends Exception
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message.
     *
     * @param message Message with the exception
     */
    public ServiceException(final String message)
    {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message Message with the exception
     * @param cause Cause of the exception
     */
    public ServiceException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ServiceException(final Throwable cause)
    {
        super(cause);
    }
}
