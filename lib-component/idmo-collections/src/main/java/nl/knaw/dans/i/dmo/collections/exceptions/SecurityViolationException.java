package nl.knaw.dans.i.dmo.collections.exceptions;

/**
 * Indicates a violation on security restrictions.
 * 
 * @author henkb
 */
public class SecurityViolationException extends CollectionsException {

    private static final long serialVersionUID = -6641268251177810356L;

    public SecurityViolationException() {

    }

    public SecurityViolationException(String msg) {
        super(msg);
    }

    public SecurityViolationException(Throwable e) {
        super(e);
    }

    public SecurityViolationException(String msg, Throwable e) {
        super(msg, e);
    }

}
