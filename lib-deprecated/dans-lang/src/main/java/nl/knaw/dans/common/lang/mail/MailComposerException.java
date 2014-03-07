package nl.knaw.dans.common.lang.mail;

public class MailComposerException extends Exception
{

    private static final long serialVersionUID = -6731973062664215412L;

    public MailComposerException()
    {
    }

    public MailComposerException(String message)
    {
        super(message);
    }

    public MailComposerException(Throwable cause)
    {
        super(cause);
    }

    public MailComposerException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
