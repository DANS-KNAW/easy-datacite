package nl.knaw.dans.i.dmo.collections.exceptions;

/**
 * Indicates that a collection does not exist and therefore cannot be retrieved.
 * 
 * @author henkb
 */
public class NoSuchCollectionException extends CollectionsException
{

    private static final long serialVersionUID = 9094786139689481320L;

    public NoSuchCollectionException()
    {

    }

    public NoSuchCollectionException(String msg)
    {
        super(msg);
    }

    public NoSuchCollectionException(Throwable e)
    {
        super(e);
    }

    public NoSuchCollectionException(String msg, Throwable e)
    {
        super(msg, e);
    }

}
