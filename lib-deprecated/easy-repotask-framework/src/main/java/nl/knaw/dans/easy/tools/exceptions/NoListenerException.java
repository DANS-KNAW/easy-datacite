package nl.knaw.dans.easy.tools.exceptions;

/**
 * Indicates no searchEngine is listening on the store.
 * 
 * @author ecco
 */
public class NoListenerException extends Exception {

    private static final long serialVersionUID = 2571692185527710671L;

    public NoListenerException() {}

    public NoListenerException(String msg) {
        super(msg);
    }

    public NoListenerException(Throwable e) {
        super(e);
    }

    public NoListenerException(String msg, Throwable e) {
        super(msg, e);
    }

}
