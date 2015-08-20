package nl.knaw.dans.easy.ebiu.exceptions;

/**
 * General indication of a fatal exception. The Application should stop after this exception has been thrown.
 */
public class FatalException extends Exception {

    private static final long serialVersionUID = 6727672012197161958L;

    public FatalException() {}

    public FatalException(String msg) {
        super(msg);
    }

    public FatalException(Throwable cause) {
        super(cause);
    }

    public FatalException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
