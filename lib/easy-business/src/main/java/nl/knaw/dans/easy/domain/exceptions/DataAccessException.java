package nl.knaw.dans.easy.domain.exceptions;

import nl.knaw.dans.common.lang.RepositoryException;

/**
 * General Exception that signals an exception in accessing data from a repository.
 * 
 * @author ecco Feb 2, 2009
 */
public class DataAccessException extends RepositoryException {

    /**
     * 
     */
    private static final long serialVersionUID = -467292464615438666L;

    public DataAccessException() {}

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
