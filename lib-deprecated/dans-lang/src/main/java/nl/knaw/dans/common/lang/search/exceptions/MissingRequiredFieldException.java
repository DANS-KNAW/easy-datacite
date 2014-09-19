package nl.knaw.dans.common.lang.search.exceptions;

public class MissingRequiredFieldException extends SearchBeanException {
    private static final long serialVersionUID = -9164700462669695665L;

    public MissingRequiredFieldException() {}

    public MissingRequiredFieldException(String message) {
        super(message);
    }

    public MissingRequiredFieldException(Throwable cause) {
        super(cause);
    }

    public MissingRequiredFieldException(String message, Throwable cause) {
        super(message, cause);
    }

}
