package nl.knaw.dans.easy.tools.task.dump;

public class ReaderException extends Exception {
    private static final long serialVersionUID = 827547219212325868L;

    ReaderException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

}
