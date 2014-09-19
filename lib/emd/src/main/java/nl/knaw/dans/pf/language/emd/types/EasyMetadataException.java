package nl.knaw.dans.pf.language.emd.types;

/**
 * Signifies an attempt to modify easymetadata with an illegal argument.
 * 
 * @author ecco
 */
public class EasyMetadataException extends IllegalArgumentException {

    private static final long serialVersionUID = 4837214352995804241L;

    // ecco: CHECKSTYLE: OFF

    EasyMetadataException() {
        super();
    }

    EasyMetadataException(final String message) {
        super(message);
    }

    EasyMetadataException(final Throwable cause) {
        super(cause);
    }

    EasyMetadataException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
