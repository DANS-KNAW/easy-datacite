package nl.knaw.dans.common.lang.service.exceptions;

import java.util.List;

public class TemporaryUnAvailableException extends CommonSecurityException
{

    private static final long serialVersionUID = -3983666001841428586L;

    public TemporaryUnAvailableException(String message)
    {
        super(message);
    }

    public TemporaryUnAvailableException(String message, List<Object> hints)
    {
        super(message, hints);
    }

    public TemporaryUnAvailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TemporaryUnAvailableException(Throwable cause)
    {
        super(cause);
    }

}
