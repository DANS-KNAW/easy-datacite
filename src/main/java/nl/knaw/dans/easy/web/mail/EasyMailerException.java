package nl.knaw.dans.easy.web.mail;

public class EasyMailerException extends RuntimeException
{
    public EasyMailerException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
