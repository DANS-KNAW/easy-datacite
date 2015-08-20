package nl.knaw.dans.easy.tools.util;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.common.lang.reposearch.RepoSearchListener;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.fedora.db.FileStoreSyncListener;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepoUtil {

    private static final Logger logger = LoggerFactory.getLogger(RepoUtil.class);

    private RepoUtil() {
        // static class
    }

    public static void checkListenersActive() throws NoListenerException {
        List<DmoStoreEventListener> listeners = Data.getEasyStore().getListeners();
        boolean searchEngineActive = false;
        boolean fileStoreSyncListener = false;
        for (DmoStoreEventListener listener : listeners) {
            logger.info("Events will be fired at " + listener);
            if (listener instanceof RepoSearchListener) {
                searchEngineActive = true;
            }
            if (listener instanceof FileStoreSyncListener) {
                fileStoreSyncListener = true;
            }
        }
        if (!fileStoreSyncListener) {
            throw new NoListenerException("Events will not be fired at fileStoreSyncListener");
        }
        if (!searchEngineActive) {
            String msg = "Events will not be fired at searchEngine.";
            throw new NoListenerException(msg);
        }
    }

}
