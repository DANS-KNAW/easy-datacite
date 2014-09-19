package nl.knaw.dans.easy.domain.exceptions;

public class UnknownItemFilterField extends DomainException {

    /**
     * 
     */
    private static final long serialVersionUID = -4999178493554717763L;

    public UnknownItemFilterField() {}

    public UnknownItemFilterField(String message) {
        super(message);
    }

    public UnknownItemFilterField(Throwable cause) {
        super(cause);
    }

    public UnknownItemFilterField(String message, Throwable cause) {
        super(message, cause);
    }

}
