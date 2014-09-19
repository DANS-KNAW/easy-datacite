package nl.knaw.dans.easy.db.exceptions;

public class CouldNotConnectToDatabaseException extends DbException {
    private static final long serialVersionUID = 3830758612973401281L;

    private static final String MSG = "Could not connect to database. ";

    public CouldNotConnectToDatabaseException(String message) {
        super(MSG + message);
    }

    public CouldNotConnectToDatabaseException(Throwable t) {
        super(MSG, t);
    }

    public CouldNotConnectToDatabaseException(String message, Throwable cause) {
        super(MSG + message, cause);
    }

}
