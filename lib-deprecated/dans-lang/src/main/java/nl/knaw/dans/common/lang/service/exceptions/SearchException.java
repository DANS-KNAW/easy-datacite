package nl.knaw.dans.common.lang.service.exceptions;

public class SearchException extends ServiceException {

    /**
     * 
     */
    private static final long serialVersionUID = -8114092987408612624L;

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(Throwable cause) {
        super(cause);
    }

}
