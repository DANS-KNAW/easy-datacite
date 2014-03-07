package nl.knaw.dans.common.lang.security.authz;

import java.io.Serializable;

import org.joda.time.DateTime;

public class AuthzMessage implements Serializable
{

    private static final long serialVersionUID = 7393025669154646819L;
    private final String messageCode;
    private final DateTime date;

    public AuthzMessage(String messageCode)
    {
        this.messageCode = messageCode;
        this.date = null;
    }

    public AuthzMessage(String messageCode, DateTime date)
    {
        this.messageCode = messageCode;
        this.date = date;
    }

    public boolean hasDate()
    {
        return date != null;
    }

    public String getMessageCode()
    {
        return messageCode;
    }

    public DateTime getDate()
    {
        return date;
    }

    @Override
    public String toString()
    {
        return super.toString() + " messageCode=" + messageCode + " date=" + date;
    }

}
