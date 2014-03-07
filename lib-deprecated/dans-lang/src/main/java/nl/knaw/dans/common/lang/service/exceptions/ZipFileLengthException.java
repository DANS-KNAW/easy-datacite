package nl.knaw.dans.common.lang.service.exceptions;

public class ZipFileLengthException extends FileSizeException
{
    private static final long serialVersionUID = -5031587075131504991L;

    public ZipFileLengthException(String message)
    {
        super(message);
    }

    public ZipFileLengthException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ZipFileLengthException(Throwable cause)
    {
        super(cause);
    }

    /*
     * Note that sizes in MegaBytes
     */
    public ZipFileLengthException(int amount, int limit)
    {
        super("The zip file exceeds the max length of " + limit + "megabytes");
        this.amount = amount;
        this.limit = limit;
    }

}
