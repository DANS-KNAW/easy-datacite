package nl.knaw.dans.easy.domain.exceptions;

/**
 * Signals that the data layer is inaccessible. This is a runtime exception so it should be used with
 * care. This exception should only be used under well known conditions, where a failure to access the
 * data layer can only be caused by a failing connection exception or other technical causes.
 * 
 * @see DataAccessException
 * @author ecco Aug 28, 2009
 */
public class DataLayerInAccessableException extends RuntimeException
{

    private static final long serialVersionUID = 8392435427192561710L;

    public DataLayerInAccessableException(Object dataRepo, Throwable cause)
    {
        super("The data repository " + dataRepo + " is not capable of handling a request.", cause);
    }

}
