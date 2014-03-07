package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class CouldNotGetStoreException extends RepositoryException
{
    private static final long serialVersionUID = 4539566881871115488L;

    public CouldNotGetStoreException(String name)
    {
        super("Store with name '" + name + "' does not exist.");
    }

}
