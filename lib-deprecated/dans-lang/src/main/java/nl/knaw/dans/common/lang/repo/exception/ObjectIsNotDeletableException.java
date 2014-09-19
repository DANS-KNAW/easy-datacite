package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class ObjectIsNotDeletableException extends RepositoryException {
    private static final long serialVersionUID = -6067928260773008318L;

    public ObjectIsNotDeletableException(String msg) {
        super(msg);
    }

}
