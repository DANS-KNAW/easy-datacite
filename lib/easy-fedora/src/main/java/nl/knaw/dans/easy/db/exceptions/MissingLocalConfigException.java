package nl.knaw.dans.easy.db.exceptions;

public class MissingLocalConfigException extends DbException {
    private static final long serialVersionUID = 6131498667087789619L;

    public MissingLocalConfigException(String message) {
        super(message);
    }

    public MissingLocalConfigException(Throwable cause) {
        super(cause);
    }

    public MissingLocalConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
