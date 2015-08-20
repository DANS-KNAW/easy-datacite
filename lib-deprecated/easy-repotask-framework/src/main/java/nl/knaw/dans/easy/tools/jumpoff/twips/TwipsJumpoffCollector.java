package nl.knaw.dans.easy.tools.jumpoff.twips;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalRuntimeException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stroll through
 */
public class TwipsJumpoffCollector extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(TwipsJumpoffCollector.class);

    public static final String MGMDATA_PATH = "mgmdata/mgmdata.xml";
    public static final String START_ELEMENT = "<appHTML>";
    public static final String END_ELEMENT = "</appHTML>";

    private final String webLocation;
    private final String dataLocation;

    private final MediaPlayerConverter mediaPlayerConverter = new MediaPlayerConverter();

    private FileFilter aipFileFilter;

    private String currentAipId;

    public TwipsJumpoffCollector(String dataLocation, String webLocation) {
        if (!new File(dataLocation).exists()) {
            throw new FatalRuntimeException("File not found: " + dataLocation);
        }
        this.dataLocation = dataLocation;

        if (!new File(webLocation).exists()) {
            throw new FatalRuntimeException("File not found: " + webLocation);
        }
        this.webLocation = webLocation;
    }

    public FileFilter getAipFileFilter() {
        if (aipFileFilter == null) {
            aipFileFilter = new FileFilter() {

                @Override
                public boolean accept(File aipFile) {
                    return aipFile.isDirectory();
                }
            };
        }
        return aipFileFilter;
    }

    public void setAipFileFilter(FileFilter aipFileFilter) {
        this.aipFileFilter = aipFileFilter;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        File dataDir = new File(dataLocation);
        for (File aipDir : dataDir.listFiles(getAipFileFilter())) {
            if (aipDir.isDirectory()) {
                try {
                    currentAipId = aipDir.getName();
                    processAipDir(aipDir);
                }
                catch (TaskExecutionException e) {
                    logger.error("Exception: " + currentAipId, e);
                    RL.error(new Event(currentAipId, e));
                }
            } else {
                RL.warn(new Event("Not a directory", aipDir.getName()));
            }
        }

    }

    private void processAipDir(File aipDir) throws TaskExecutionException {
        File mgmdataFile = new File(aipDir, MGMDATA_PATH);
        if (mgmdataFile.exists() && mgmdataFile.isFile()) {
            processMgmdataFile(mgmdataFile);
        } else {
            RL.warn(new Event("No mgmdata", currentAipId));
        }
    }

    private void processMgmdataFile(File mgmdataFile) throws TaskExecutionException {
        try {
            byte[] bytes = FileUtil.readFile(mgmdataFile);
            String mgmdata = new String(bytes);
            int start = mgmdata.indexOf(START_ELEMENT);
            int end = mgmdata.indexOf(END_ELEMENT);
            if (start > -1 && end > -1) {
                String content = mgmdata.substring(start + 9, end).trim();
                removeOldJumpoff();
                processContent(content);
            }
        }
        catch (IOException e) {
            RL.error(new Event("Could not read file", e, currentAipId));
            throw new TaskExecutionException("While reading file: " + mgmdataFile.getAbsolutePath(), e);
        }

    }

    private void removeOldJumpoff() throws TaskExecutionException {
        try {
            List<IdMap> idMapList = Data.getMigrationRepo().findByAipId(currentAipId);
            for (IdMap idMap : idMapList) {
                String datasetId = idMap.getStoreId();
                try {
                    JumpoffDmo joDmo = Data.getEasyStore().findJumpoffDmoFor(new DmoStoreId(datasetId));
                    RL.info(new Event("Purging", currentAipId, joDmo.getStoreId()));
                    Data.getEasyStore().purge(joDmo, false, "replacing jumpoff");

                }
                catch (ObjectNotInStoreException e) {
                    //
                }
            }
        }
        catch (RepositoryException e) {
            throw new TaskExecutionException("While purging old jumpoffs", e);
        }

    }

    private void processContent(String content) {
        if (content.length() > 0) {
            logger.info("Now processing html of " + currentAipId);
            RL.info(new Event("Found content", currentAipId));

            mediaPlayerConverter.convertPaths(content);

        } else {
            RL.info(new Event("No content", currentAipId));
        }

    }

}
