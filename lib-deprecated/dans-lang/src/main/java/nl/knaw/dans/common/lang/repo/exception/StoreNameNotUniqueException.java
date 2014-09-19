package nl.knaw.dans.common.lang.repo.exception;

public class StoreNameNotUniqueException extends RuntimeException {
    private static final long serialVersionUID = -5994329151109565580L;

    public StoreNameNotUniqueException(String name) {
        super("Store " + name + " is not a unique store name.");
    }

}
