package nl.knaw.dans.easy.pakbonpreprocess.exceptions;

/**
 * Indicates an exception has occurred while converting.
 * 
 * @author paulboon
 */
public class ConversionException extends Exception {
    private static final long serialVersionUID = -2589961652398888004L;

    public ConversionException() {
        super();
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}
