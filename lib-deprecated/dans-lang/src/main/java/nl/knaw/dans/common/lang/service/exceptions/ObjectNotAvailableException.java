package nl.knaw.dans.common.lang.service.exceptions;

public class ObjectNotAvailableException extends ServiceException {

    private static final long serialVersionUID = 4729330682916794624L;

    public ObjectNotAvailableException(String message) {
        super(message);
    }

    public ObjectNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotAvailableException(Throwable cause) {
        super(cause);
    }

}
