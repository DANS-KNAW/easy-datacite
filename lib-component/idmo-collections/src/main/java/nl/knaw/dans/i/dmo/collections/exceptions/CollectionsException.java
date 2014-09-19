package nl.knaw.dans.i.dmo.collections.exceptions;

/**
 * General exception on the component dmo.collections.
 * 
 * @author henkb
 */
public class CollectionsException extends Exception {

    private static final long serialVersionUID = 4110777766554873369L;

    public CollectionsException() {

    }

    public CollectionsException(String msg) {
        super(msg);
    }

    public CollectionsException(Throwable e) {
        super(e);
    }

    public CollectionsException(String msg, Throwable e) {
        super(msg, e);
    }

}
