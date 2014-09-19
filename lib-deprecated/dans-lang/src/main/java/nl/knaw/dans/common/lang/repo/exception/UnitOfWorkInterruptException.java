package nl.knaw.dans.common.lang.repo.exception;

public class UnitOfWorkInterruptException extends Exception {

    private static final long serialVersionUID = -4492368237537399925L;

    public UnitOfWorkInterruptException() {}

    public UnitOfWorkInterruptException(String message) {
        super(message);
    }

    public UnitOfWorkInterruptException(Throwable cause) {
        super(cause);
    }

    public UnitOfWorkInterruptException(String message, Throwable cause) {
        super(message, cause);
    }

}
