package nl.knaw.dans.easy;

public class DataciteServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataciteServiceException(String message, Exception cause) {
        super(message, cause);
    }

    public DataciteServiceException(String message) {
        super(message);
    }
}
