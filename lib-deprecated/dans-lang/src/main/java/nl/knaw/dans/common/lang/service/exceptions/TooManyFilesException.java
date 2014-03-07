package nl.knaw.dans.common.lang.service.exceptions;

public class TooManyFilesException extends FileSizeException
{
    private static final long serialVersionUID = -5031587075131504991L;

    public TooManyFilesException(String message)
    {
        super(message);
    }

    public TooManyFilesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TooManyFilesException(Throwable cause)
    {
        super(cause);
    }

    public TooManyFilesException(int amount, int limit)
    {
        super("The zip file exceeds the max number (" + limit + ") of files.");
        this.amount = amount;
        this.limit = limit;
    }

}
