package nl.knaw.dans.easy.domain.exceptions;

public class DomainRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 6336201730454744441L;

    public DomainRuntimeException() {}

    public DomainRuntimeException(String message) {
        super(message);
    }

    public DomainRuntimeException(Throwable cause) {
        super(cause);
    }

    public DomainRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
