package nl.knaw.dans.easy.data;

public class DataConfigurationException extends RuntimeException
{

    private static final long serialVersionUID = 1705831807560722524L;

    public DataConfigurationException()
    {
    }

    public DataConfigurationException(String msg)
    {
        super(msg);
    }

    public DataConfigurationException(Throwable e)
    {
        super(e);
    }

    public DataConfigurationException(String msg, Throwable e)
    {
        super(msg, e);
    }

}
