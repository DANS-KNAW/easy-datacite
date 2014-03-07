package nl.knaw.dans.easy.web.download;

public class DownloadException extends RuntimeException
{

    private static final long serialVersionUID = -7974092889200816600L;

    public DownloadException()
    {
    }

    public DownloadException(String message)
    {
        super(message);
    }

    public DownloadException(Throwable cause)
    {
        super(cause);
    }

    public DownloadException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
