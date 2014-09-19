package nl.knaw.dans.i.dmo.collections.exceptions;

/**
 * Indicates that a given namespace for a new collection to be created is already in use.
 * 
 * @author henkb
 */
public class NamespaceNotUniqueException extends CollectionsException {

    private static final long serialVersionUID = 5175196971332011717L;

    public NamespaceNotUniqueException() {

    }

    public NamespaceNotUniqueException(String message) {
        super(message);
    }

    public NamespaceNotUniqueException(Throwable cause) {
        super(cause);
    }

    public NamespaceNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

}
