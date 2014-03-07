package nl.knaw.dans.common.fedora;

import java.util.Iterator;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class to get Repository, Managers and Accessors.
 * 
 * @author ecco Sep 22, 2009
 */
public class Fedora
{

    private static final Logger logger = LoggerFactory.getLogger(Fedora.class);

    private String baseURL;
    private Repository repository;
    private DatastreamAccessor datastreamAccessor;
    private DatastreamManager datastreamManager;
    private DisseminationAccessor disseminationAccessor;
    private ObjectAccessor objectAccesor;
    private ObjectManager objectManager;
    private RepositoryAccessor repositoryAccessor;
    private RelationshipManager relationshipManager;

    /**
     * DO NOT USE - called by application context. The class we are going to proxy using CGLib has to
     * provide a default constructor. Alternatively switch to JDK dynamic proxies (if that's configurable
     * for annotations).
     */
    protected Fedora()
    {

    }

    /**
     * Call once, preferably from an application context.
     * 
     * @param baseURL
     *        the baseURL of the repository i.e. http://localhost:80/fedora
     * @param username
     *        username on the repository
     * @param userpass
     *        userpass on the repository
     */
    public Fedora(String baseURL, String username, String userpass)
    {
        this.baseURL = baseURL;
        repository = new Repository(baseURL, username, userpass);
        logger.info("Constructed new Fedora for baseURL " + baseURL);
    }

    public void setRetryTimeOutSeconds(int seconds)
    {
        repository.setRetryTimeOutSeconds(seconds);
    }

    public void setMaxRetryCount(int count)
    {
        repository.setMaxRetryCount(count);
    }

    public String getBaseURL()
    {
        return baseURL;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public DatastreamAccessor getDatastreamAccessor()
    {
        if (datastreamAccessor == null)
        {
            datastreamAccessor = new DatastreamAccessor(repository);
        }
        return datastreamAccessor;
    }

    public DatastreamManager getDatastreamManager()
    {
        if (datastreamManager == null)
        {
            datastreamManager = new DatastreamManager(repository);
        }
        return datastreamManager;
    }

    public DisseminationAccessor getDisseminationAccessor()
    {
        if (disseminationAccessor == null)
        {
            disseminationAccessor = new DisseminationAccessor(repository);
        }
        return disseminationAccessor;
    }

    public ObjectManager getObjectManager()
    {
        if (objectManager == null)
        {
            objectManager = new ObjectManager(repository);
        }
        return objectManager;
    }

    public ObjectAccessor getObjectAccessor()
    {
        if (objectAccesor == null)
        {
            objectAccesor = new ObjectAccessor(repository);
        }
        return objectAccesor;
    }

    public RepositoryAccessor getRepositoryAccessor()
    {
        if (repositoryAccessor == null)
        {
            repositoryAccessor = new RepositoryAccessor(repository);
        }
        return repositoryAccessor;
    }

    public RelationshipManager getRelationshipManager()
    {
        if (relationshipManager == null)
        {
            relationshipManager = new RelationshipManager(repository);
        }
        return relationshipManager;
    }

    /**
     * Set the common buffer size for sidLists.
     * 
     * @param sidListBufferSize
     *        the common buffer size for sidLists
     * @throws IllegalArgumentException
     *         for sidListBufferSize < 1
     * @throws RepositoryException
     */
    public void setSidListBufferSize(int sidListBufferSize) throws IllegalArgumentException
    {
        getObjectManager().setSidListBufferSize(sidListBufferSize);
    }

    /**
     * Set the buffer size of the sidList for the given namespace.
     * 
     * @param objectNamespace
     *        a namespace that matches one of the "retainPIDs" values configured for the repository
     * @param bufferSize
     *        buffer size of the sidList for the given namespace
     */
    public void setSidListBufferSize(String objectNamespace, int bufferSize)
    {
        getObjectManager().setSidListBufferSize(objectNamespace, bufferSize);
    }

    public void setSidListBufferSizeMap(Map<String, Integer> namespaceBufferSizeMap)
    {
        Iterator<Map.Entry<String, Integer>> iterator = namespaceBufferSizeMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, Integer> entry = iterator.next();
            setSidListBufferSize(entry.getKey(), entry.getValue());
        }
    }

}
