package nl.knaw.dans.easy.ebiu.exceptions;

/**
 * Indicates no searchEngine is listening on the store.
 * 
 * @author ecco
 */
public class NoSearchEngineListeningException extends Exception {

    private static final long serialVersionUID = 2571692185527710671L;

    public NoSearchEngineListeningException() {}

    public NoSearchEngineListeningException(String msg) {
        super(msg);
    }

    public NoSearchEngineListeningException(Throwable e) {
        super(e);
    }

    public NoSearchEngineListeningException(String msg, Throwable e) {
        super(msg, e);
    }

}
