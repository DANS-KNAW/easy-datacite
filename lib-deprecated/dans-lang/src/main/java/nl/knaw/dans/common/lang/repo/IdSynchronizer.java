package nl.knaw.dans.common.lang.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import nl.knaw.dans.common.lang.repo.exception.LockAcquireTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object aids in synchronization of lists of objects by their ID. This
 * is especially handy for implementing thread-safe transaction handling. A
 * transaction involving several objects cannot start until another transaction
 * that is busy with one or more of the same objects. 
 * 
 * Example:
 * If one thread has several objects with the IDs A, B and C and wants to do
 * some operation on them, but another thread wants to do an operation on 
 * object C and D then the second thread cannot start until the first thread
 * finishes and if a third thread wants to do some operation on D then it has
 * to wait for the second thread to finish first, etc.   
 * 
 * @author lobo
 */
public class IdSynchronizer<T>
{
    /**
     * key = ID
     * value = the lock
     */
    private Map<T, IdLock> idLocks = new HashMap<T, IdLock>();

    /**
     * key = ID
     * value = use count = how often a thread acquire a lock for this ID
     */
    private ThreadLocal<Map<T, Integer>> threadLocalIds = new ThreadLocal<Map<T, Integer>>();

    private int defaultLockTimeout;

    private boolean useFairPolicy;

    private static final Logger LOGGER = LoggerFactory.getLogger(IdSynchronizer.class);

    private ThreadLocal<Boolean> logEnabled = new ThreadLocal<Boolean>();

    private class IdLock
    {
        public ReentrantLock lock;
        public int threadCount;
        public T id;

        public IdLock(T id, boolean useFairPolicy)
        {
            lock = new ReentrantLock(useFairPolicy);
            threadCount = 1;
            this.id = id;
        }

        public void lock(int timeout) throws LockAcquireTimeoutException, InterruptedException
        {
            if (!lock.tryLock(0, TimeUnit.MILLISECONDS))
            {
                LOGGER.debug("Thread " + Thread.currentThread().toString() + " blocking for " + id);

                if (timeout >= 0)
                {
                    if (!lock.tryLock(timeout, TimeUnit.MILLISECONDS))
                    {
                        LOGGER.debug("Thread " + Thread.currentThread().toString() + " timedout trying to get lock for " + id);

                        throw new LockAcquireTimeoutException();
                    }
                }
                else
                {
                    try
                    {
                        // now only god can stop us, so better say your prayers :)
                        lock.lock();
                    }
                    catch (Throwable t)
                    {
                        // god did not listen to your prayers 
                        if (t instanceof InterruptedException)
                            throw (InterruptedException) t;
                        else
                            throw new InterruptedException("Wait for lock on " + this.id + " interrupted. " + t.getMessage() != null ? t.getMessage() : "");
                    }
                }

                LOGGER.debug("Thread " + Thread.currentThread().toString() + " got lock for " + id);
            }
        }

        public void unlock()
        {
            try
            {
                lock.unlock();
            }
            catch (IllegalMonitorStateException e)
            {
                warn("Unlock called on " + this.id + " for thread that did not hold that lock", e);
            }
        }
    }

    public IdSynchronizer()
    {
        this(-1, false);
    }

    /**
     * @param defaultLockTimeout the default timeout value for trying to acquire a lock in milliseconds.
     * Set to -1 for no timeout, but possible infinite wait.
     * @param useFairPolicy see {@see ReentrantLock}. It's basically that the first thread to try
     * to acquire a lock gets it first (fifo).
     */
    public IdSynchronizer(int defaultLockTimeout, boolean useFairPolicy)
    {
        this.setDefaultLockTimeout(defaultLockTimeout);
        this.useFairPolicy = useFairPolicy;
    }

    private void warn(String msg)
    {
        warn(msg, null);
    }

    private void warn(String msg, Throwable t)
    {
        if (isLoggingEnabled())
            LOGGER.warn(msg, t);
    }

    private boolean isLoggingEnabled()
    {
        return logEnabled.get() == null || logEnabled.get().equals(Boolean.TRUE);
    }

    private void disableLogging()
    {
        logEnabled.set(Boolean.FALSE);
    }

    private void enableLogging()
    {
        logEnabled.set(Boolean.TRUE);
    }

    /**
     * Convenience method. 
     * 
     * Beware not to call this in a loop for multiple sids as that could 
     * lead to dead locks. Think of thread 1 asking for a lock on A and
     * B while thread 2 asking for a lock on B and A asking for one lock
     * at a time, they might get stuck waiting on each other.
     * 
     * @param id
     * @throws InterruptedException
     * @throws LockAcquireTimeoutException 
     */
    public void acquireLock(T id) throws InterruptedException, LockAcquireTimeoutException
    {
        List<T> ids = new ArrayList<T>(1);
        ids.add(id);
        acquireLock(ids);
    }

    /**
     * Locks on a list of IDs. If one of the IDs was already locked then
     * this method will block until those locks are unlocked.
     * 
     * If an exception is thrown it can be assumed that no locks are held anymore. So
     * either this method returns and all locks have been acquired or this method 
     * throws an exception and no locks are acquired.
     * 
     * @param ids a list of IDs
     * @throws InterruptedException thrown if the blocking thread got interrupted and had to abort
     * trying to acquire the lock
     * @throws LockAcquireTimeoutException thrown if the thread could not acquire the lock within
     * the defaultLockTimeout time. 
     */
    public void acquireLock(final Collection<T> ids) throws InterruptedException, LockAcquireTimeoutException
    {
        List<IdLock> lockList = null;
        synchronized (idLocks)
        {
            Map<T, Integer> localIds = threadLocalIds.get();
            if (localIds == null)
                localIds = new HashMap<T, Integer>();

            for (T id : ids)
            {
                Integer useCount = localIds.get(id);
                if (useCount == null)
                {
                    useCount = new Integer(1);
                    localIds.put(id, useCount);
                }
                else
                {
                    useCount++;
                    localIds.put(id, useCount);
                    // skip locking: thread is already locked
                    continue;
                }

                IdLock idLock = idLocks.get(id);
                if (idLock == null)
                {
                    // new lock
                    idLock = new IdLock(id, useFairPolicy);
                    idLock.lock(defaultLockTimeout); // first time lock
                    idLocks.put(id, idLock);
                }
                else
                {
                    // existing lock
                    idLock.threadCount += 1;
                    if (lockList == null)
                        lockList = new ArrayList<IdLock>(ids.size());
                    lockList.add(idLock);
                }
            }

            threadLocalIds.set(localIds);
        }

        // do the locking outside the synchronized block
        // otherwise the blocking of the lock will block
        // the entire synchronized block, which will then
        // block every other call to aquireLock or releaseLock
        if (lockList != null)
        {
            try
            {
                for (IdLock idLock : lockList)
                {
                    idLock.lock(defaultLockTimeout);
                }
            }
            catch (InterruptedException e)
            {
                disableLogging();
                releaseLock(ids);
                enableLogging();

                warn("IdSynchronizer.acquireLock() interrupted", e);
                if (e instanceof InterruptedException)
                    throw (InterruptedException) e;
                else
                    throw new InterruptedException();
            }
        }
    }

    public void releaseLock(T id)
    {
        List<T> ids = new ArrayList<T>(1);
        ids.add(id);
        releaseLock(ids);
    }

    /**
     * Releases the lock or locks acquired by called acquireLock.
     */
    public void releaseLock(Collection<T> ids)
    {
        synchronized (idLocks)
        {
            Map<T, Integer> localIds = threadLocalIds.get();
            if (localIds == null)
            {
                warn("releaseLock called by thread that does not own " + "any locks.");
                return;
            }

            for (T id : ids)
            {
                Integer useCount = localIds.get(id);
                if (useCount == null)
                {
                    warn("releaseLock called on invalid ID '" + id.toString() + "'. " + "This ID was not locked by this thread.");
                    continue;
                }
                useCount--;

                if (useCount == 0)
                {
                    IdLock idLock = idLocks.get(id);
                    if (idLock != null)
                    {
                        idLock.unlock();
                        idLock.threadCount--;
                        if (idLock.threadCount == 0)
                            idLocks.remove(id);
                    }
                    else
                    {
                        // log this case but do no interrupt runtime
                        LOGGER.error("lock in thread list, but not found: " + "this should not happen. Programming mistake in " + this.getClass() + " class.");
                    }

                    localIds.remove(id);
                }
                else
                {
                    localIds.put(id, useCount);
                }
            }

            if (localIds.size() == 0)
                threadLocalIds.set(null);
        }
    }

    /**
     * @return the number of id's that are currently locked
     */
    protected int getLockIdCount()
    {
        synchronized (idLocks)
        {
            return idLocks.size();
        }
    }

    /**
     * @return the number of threads waiting for a lock on a id
     */
    protected int getLockThreadCount(T id)
    {
        synchronized (idLocks)
        {
            IdLock idLock = idLocks.get(id);
            if (idLock != null)
                return idLock.threadCount;
            else
                return 0;
        }
    }

    /**
     * @return the number of times a thread acquired a lock for a single ID
     */
    protected int getLockThreadUseCount(T id)
    {
        synchronized (idLocks)
        {
            Map<T, Integer> localIds = threadLocalIds.get();
            if (localIds == null)
            {
                return 0;
            }

            Integer useCount = localIds.get(id);
            if (useCount == null)
            {
                return 0;
            }
            else
            {
                return useCount;
            }
        }
    }

    /**
     * Sets the default timeout value in milliseconds for this class to acquire a lock.
     */
    public void setDefaultLockTimeout(int defaultLockTimeout)
    {
        this.defaultLockTimeout = defaultLockTimeout;
    }

    /**
     * Gets the default timeout value in milliseconds for this class to acquire a lock.
     */
    public int getDefaultLockTimeout()
    {
        return defaultLockTimeout;
    }

    /**
     * @return true if the attempt to get the lock will be based on a fair use policy
     * (fifo).
     */
    public boolean isUsingFairPolicy()
    {
        return useFairPolicy;
    }

}
