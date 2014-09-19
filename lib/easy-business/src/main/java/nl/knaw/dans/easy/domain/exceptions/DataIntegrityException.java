package nl.knaw.dans.easy.domain.exceptions;

import java.util.ArrayList;
import java.util.List;

public class DataIntegrityException extends DomainException {

    private static final long serialVersionUID = 4632587095875268367L;

    private List<String> errorMessages = new ArrayList<String>();

    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIntegrityException(Throwable cause) {
        super(cause);
    }

    public DataIntegrityException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public String printErrorMessages() {
        StringBuilder sb = new StringBuilder();
        for (String msg : errorMessages) {
            sb.append("\n\t");
            sb.append(msg);
            sb.append(";");
        }
        return sb.toString();
    }

}
