package nl.knaw.dans.common.lang.exception;

public class ConfigurationException extends Exception {

    private static final long serialVersionUID = -3520097921864444210L;

    public ConfigurationException() {

    }

    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
