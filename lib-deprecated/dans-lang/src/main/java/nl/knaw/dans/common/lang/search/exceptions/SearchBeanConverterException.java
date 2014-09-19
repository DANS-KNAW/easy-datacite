package nl.knaw.dans.common.lang.search.exceptions;

public class SearchBeanConverterException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7068713361979783309L;

    public SearchBeanConverterException() {}

    public SearchBeanConverterException(String message) {
        super(message);
    }

    public SearchBeanConverterException(Throwable cause) {
        super(cause);
    }

    public SearchBeanConverterException(String message, Throwable cause) {
        super(message, cause);
    }

}
