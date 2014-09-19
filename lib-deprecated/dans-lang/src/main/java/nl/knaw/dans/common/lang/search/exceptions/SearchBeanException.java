package nl.knaw.dans.common.lang.search.exceptions;

public class SearchBeanException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2842086395804167763L;

    public SearchBeanException() {}

    public SearchBeanException(String message) {
        super(message);
    }

    public SearchBeanException(Throwable cause) {
        super(cause);
    }

    public SearchBeanException(String message, Throwable cause) {
        super(message, cause);
    }

}
