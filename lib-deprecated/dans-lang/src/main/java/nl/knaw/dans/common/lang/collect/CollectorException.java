package nl.knaw.dans.common.lang.collect;

public class CollectorException extends Exception
{

    private static final long serialVersionUID = -408717047398295794L;

    public CollectorException()
    {
    }

    public CollectorException(String msg)
    {
        super(msg);
    }

    public CollectorException(Throwable e)
    {
        super(e);
    }

    public CollectorException(String msg, Throwable e)
    {
        super(msg, e);
    }

}
