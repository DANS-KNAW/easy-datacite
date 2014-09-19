package nl.knaw.dans.common.lang.repo.exception;

/**
 * Signals an exception while deserializing an object.
 * 
 * @author ecco Sep 24, 2009
 */
public class ObjectDeserializationException extends ConverterException {

    private static final long serialVersionUID = -8199208591432800183L;

    // CHECKSTYLE: OFF
    public ObjectDeserializationException() {}

    public ObjectDeserializationException(final String message) {
        super(message);
    }

    public ObjectDeserializationException(final Throwable cause) {
        super(cause);
    }

    public ObjectDeserializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
