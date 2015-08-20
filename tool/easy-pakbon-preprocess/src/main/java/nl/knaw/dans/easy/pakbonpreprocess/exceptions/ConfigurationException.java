package nl.knaw.dans.easy.pakbonpreprocess.exceptions;

/**
 * Indicates an exception has occurred related to the configuration.
 * 
 * @author paulboon
 */
public class ConfigurationException extends Exception {
    private static final long serialVersionUID = -2589961652398888004L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
