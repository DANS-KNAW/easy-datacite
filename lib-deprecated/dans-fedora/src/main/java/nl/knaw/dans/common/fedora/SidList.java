package nl.knaw.dans.common.fedora;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.util.Wait;

import org.apache.axis.types.NonNegativeInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidList {

    private static final Logger logger = LoggerFactory.getLogger(SidList.class);

    private final Repository repository;
    private final String objectNamespace;

    private int bufferSize;

    private List<String> sidList;
    private Iterator<String> sidListIterator;

    public SidList(Repository repository, String objectNameSpace, int bufferSize) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("BufferSize smaller than 1.");
        }
        this.repository = repository;
        this.objectNamespace = objectNameSpace;
        this.bufferSize = bufferSize;
    }

    public String getObjectNamespace() {
        return objectNamespace;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Buffer size cannot be less than 1.");
        }
        this.bufferSize = bufferSize;
    }

    public synchronized String nextSid() throws RepositoryException {
        if (sidList == null || !sidListIterator.hasNext()) {
            try {
                getNextSidWithRetry();

                logger.info("Fetched " + bufferSize + " new SID's, starting with " + sidList.get(0));
                sidListIterator = sidList.iterator();
            }
            catch (RemoteException e) {
                final String msg = "Unable to get next sid(s). objectNamespace=" + objectNamespace;
                logger.debug(msg, e);
                Repository.mapRemoteException(msg, e);
            }
        }
        return sidListIterator.next();
    }

    private void getNextSidWithRetry() throws RepositoryException, RemoteException {
        boolean loaded = false;
        int tryCount = 0;
        while (!loaded) {
            tryCount++;
            try {
                String[] sids = repository.getFedoraAPIM().getNextPID(new NonNegativeInteger("" + bufferSize), objectNamespace);
                sidList = Arrays.asList(sids);
                loaded = true;
            }
            catch (RemoteException e) {
                logger.warn("Caught RemoteException while getting next sid(s). objectNamespace=" + objectNamespace + " tryCount=" + tryCount + " message="
                        + e.getMessage());
                if (tryCount >= repository.getMaxRetryCount()) {
                    throw (e);
                }
                Wait.seconds(repository.getRetryTimeOutSeconds());
            }
        }
    }

}
