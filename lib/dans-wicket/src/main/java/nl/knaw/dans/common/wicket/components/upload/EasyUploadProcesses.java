package nl.knaw.dans.common.wicket.components.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo This class acts as a pool of UploadProcess classes. After creating a new UploadProces others may access it only by getting the process from this
 *         pool. It is a singleton; one needs only one of these pools per WicketApplication. This class also removes UploadProcesses after a certain time-out
 *         period.
 */
public class EasyUploadProcesses {
    // TODO: get from properties
    /**
     * After how many minutes of no contact with the client should an uploadprocess be removed from the list?
     */
    private static final Integer TIMEOUT_MINS = 60;

    /** Log. */
    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadProcesses.class);

    /*------------------------------------------------
     * Singleton code (Initialization on demand holder)
     *------------------------------------------------*/

    protected EasyUploadProcesses() {}

    private static class SingletonHolder {
        private final static EasyUploadProcesses INSTANCE = new EasyUploadProcesses();
    }

    public static EasyUploadProcesses getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /*------------------------------------------------
     * List holding code for the processes
     *------------------------------------------------*/

    private LinkedList<UploadProcessTimed> processList = new LinkedList<UploadProcessTimed>();

    private Integer lastUploadId = 1;

    /**
     * This number is arbitrary, it may even be generated on the client side. As long as it is unique.
     * 
     * @return
     */
    public Integer generateUploadId() {
        return lastUploadId++;
    }

    private UploadProcessTimed getUploadProcessTimedById(Integer uploadId) {
        Iterator<UploadProcessTimed> it = processList.iterator();
        UploadProcessTimed processTimed;
        while (it.hasNext()) {
            processTimed = it.next();
            Integer processUploadId = processTimed.getUploadProcess().getUploadId();
            if (processUploadId.equals(uploadId)) {
                return processTimed;
            }
        }
        return null;
    }

    public EasyUploadProcess getUploadProcessById(Integer uploadId) {
        UploadProcessTimed processTimed = getUploadProcessTimedById(uploadId);
        if (processTimed == null)
            return null;

        // update last used time before returning the process
        processTimed.updateLastAccessed();
        return processTimed.getUploadProcess();
    }

    public void register(EasyUploadProcess process) {
        // cleanup old processes
        cleanupOldProcesses();

        // register new one
        processList.add(new UploadProcessTimed(process));
        LOG.info("Registered upload process with id: " + process.getUploadId());
    }

    public void unregister(EasyUploadProcess process) {
        UploadProcessTimed processTimed = getUploadProcessTimedById(process.getUploadId());
        if (processTimed == null)
            return;

        LOG.info("Unregistered upload process with id: " + process.getUploadId());
        processList.remove(processTimed);
    }

    public void cancelUploadsByEasyUpload(EasyUpload easyUpload) {
        Iterator<UploadProcessTimed> it = processList.iterator();
        UploadProcessTimed processTimed;
        ArrayList<UploadProcessTimed> removeList = new ArrayList<UploadProcessTimed>();
        while (it.hasNext()) {
            processTimed = it.next();
            if (processTimed.getUploadProcess().getEasyUpload() == easyUpload) {
                removeList.add(processTimed);
            }
        }

        removeAndCancelProcesses(removeList);
    }

    /**
     * Throw away all upload process objects that have not been accessed for a certain amount of time (TIMEOUT_MIS)
     */
    private void cleanupOldProcesses() {
        Date now = new Date();
        Iterator<UploadProcessTimed> it = processList.iterator();
        UploadProcessTimed processTimed;
        ArrayList<UploadProcessTimed> removeList = new ArrayList<UploadProcessTimed>();
        while (it.hasNext()) {
            processTimed = it.next();
            long minutesOld = (now.getTime() - processTimed.getLastAccessed().getTime()) / 60000;
            if (minutesOld >= TIMEOUT_MINS) {
                LOG.info("removed old (" + minutesOld + " minutes) upload process with id: " + processTimed.getUploadProcess().getUploadId());
                removeList.add(processTimed);
            }
        }

        removeAndCancelProcesses(removeList);
    }

    private void removeAndCancelProcesses(ArrayList<UploadProcessTimed> removeList) {
        Iterator<UploadProcessTimed> it = removeList.iterator();
        UploadProcessTimed processTimed;
        while (it.hasNext()) {
            processTimed = it.next();
            if (processTimed.getUploadProcess().getStatus().isFinished() == false)
                processTimed.getUploadProcess().cancel();
            processList.remove(processTimed);
        }
    }

    public void cancelAllUploads() {
        Iterator<UploadProcessTimed> it = processList.iterator();
        while (it.hasNext())
            it.next().getUploadProcess().cancel();
    }

    /**
     * @author lobo Wrapper class around UploadProcess that allows one to store a last accessed time with the UploadProcess object. This comes in handy when
     *         trying to determine if an upload process should be removed after considerable amount of idle time from the client's side.
     */
    static class UploadProcessTimed {
        private Date lastAccessed;

        private EasyUploadProcess uploadProcess;

        public UploadProcessTimed(EasyUploadProcess uploadProcess) {
            this.lastAccessed = new Date();
            this.uploadProcess = uploadProcess;
        }

        public void updateLastAccessed() {
            this.lastAccessed = new Date();
        }

        public Date getLastAccessed() {
            return this.lastAccessed;
        }

        public EasyUploadProcess getUploadProcess() {
            return this.uploadProcess;
        }
    }

}
