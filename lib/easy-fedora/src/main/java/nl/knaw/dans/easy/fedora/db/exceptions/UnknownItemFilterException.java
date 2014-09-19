package nl.knaw.dans.easy.fedora.db.exceptions;

import nl.knaw.dans.easy.data.store.StoreAccessException;

public class UnknownItemFilterException extends StoreAccessException {
    private static final long serialVersionUID = -5783345063225401904L;

    public UnknownItemFilterException() {}

    public UnknownItemFilterException(String message) {
        super(message);
    }

    public UnknownItemFilterException(Throwable cause) {
        super(cause);
    }

    public UnknownItemFilterException(String message, Throwable cause) {
        super(message, cause);
    }

}
