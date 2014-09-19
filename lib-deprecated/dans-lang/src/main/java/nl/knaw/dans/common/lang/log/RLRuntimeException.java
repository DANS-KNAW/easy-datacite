package nl.knaw.dans.common.lang.log;

public class RLRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 2123064994330578096L;

    public RLRuntimeException() {

    }

    public RLRuntimeException(String message) {
        super(message);
    }

    public RLRuntimeException(Throwable cause) {
        super(cause);
    }

    public RLRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
