package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class InvalidRelationshipException extends RepositoryException {

    /**
     * 
     */
    private static final long serialVersionUID = 7151386013469147816L;

    public InvalidRelationshipException() {}

    public InvalidRelationshipException(String message) {
        super(message);
    }

    public InvalidRelationshipException(Throwable cause) {
        super(cause);
    }

    public InvalidRelationshipException(String message, Throwable cause) {
        super(message, cause);
    }

}
