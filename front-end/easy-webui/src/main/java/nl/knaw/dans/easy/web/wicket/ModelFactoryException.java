package nl.knaw.dans.easy.web.wicket;

public class ModelFactoryException extends RuntimeException {

    private static final long serialVersionUID = -3394580684121784899L;

    public ModelFactoryException() {}

    public ModelFactoryException(String message) {
        super(message);
    }

    public ModelFactoryException(Throwable cause) {
        super(cause);
    }

    public ModelFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
