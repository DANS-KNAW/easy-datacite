package nl.knaw.dans.common.lang;

/**
 * Signals an exception due to programming errors or invalid configuration.
 * @author ecco May 4, 2010
 *
 */
public class ApplicationException extends RuntimeException
{

    private static final long serialVersionUID = 415697671242884786L;

    public ApplicationException()
    {
    }

    public ApplicationException(String arg0)
    {
        super(arg0);
    }

    public ApplicationException(Throwable arg0)
    {
        super(arg0);
    }

    public ApplicationException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

}
