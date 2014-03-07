package nl.knaw.dans.common.fedora;

import java.rmi.RemoteException;

import nl.knaw.dans.common.lang.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryAccessor
{
    private static final Logger logger = LoggerFactory.getLogger(RepositoryAccessor.class);

    private final Repository repository;

    public RepositoryAccessor(Repository repository)
    {
        this.repository = repository;
    }

    public fedora.server.types.gen.RepositoryInfo getRepositoryInfo() throws RepositoryException
    {
        fedora.server.types.gen.RepositoryInfo repositoryInfo = null;
        try
        {
            repositoryInfo = repository.getFedoraAPIA().describeRepository();
        }
        catch (RemoteException e)
        {
            final String msg = "Unable to get repository info: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return repositoryInfo;
    }

    public RepositoryInfo describeRepository() throws RepositoryException
    {
        return new RepositoryInfo(getRepositoryInfo());
    }

}
