package nl.knaw.dans.easy.domain.exceptions;

/**
 * Signals that a serialized object normally retrievable by an identifier from a deserializing agent unexpectantly is unknown
 * to or unfindable for the deserializing agent. This is a RuntimeException so it should be used with care.
 * 
 * @see ObjectNotFoundException
 * 
 * @author ecco Aug 28, 2009
 */
public class UnknownIdentifierException extends RuntimeException
{

    private static final long serialVersionUID = -9115933944448732710L;

    public UnknownIdentifierException(Object identifier, Object dataRepo)
    {
        this(identifier, dataRepo, null);
    }

    public UnknownIdentifierException(Object identifier, Object dataRepo, Throwable cause)
    {
        super("The identifier [" + identifier + "] is unknown to the data repository " + dataRepo, cause);
    }

}
