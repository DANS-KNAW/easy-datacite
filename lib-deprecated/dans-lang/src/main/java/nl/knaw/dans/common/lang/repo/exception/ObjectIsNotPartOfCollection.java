package nl.knaw.dans.common.lang.repo.exception;

public class ObjectIsNotPartOfCollection extends CollectionException
{

    /**
     * 
     */
    private static final long serialVersionUID = 2820094258721986261L;

    public ObjectIsNotPartOfCollection()
    {
    }

    public ObjectIsNotPartOfCollection(String message)
    {
        super(message);
    }

    public ObjectIsNotPartOfCollection(Throwable cause)
    {
        super(cause);
    }

    public ObjectIsNotPartOfCollection(String message, Throwable cause)
    {
        super(message, cause);
    }

}
