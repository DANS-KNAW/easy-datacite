package nl.knaw.dans.easy.ebiu.task;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.Application;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.Task;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;
import nl.knaw.dans.easy.ebiu.util.Printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls batch processing for ingesting and updating datasets.
 */
public class BatchController extends AbstractTask {
    /**
     * The default directory name that contains resources to ingest.
     */
    public static final String DEFAULT_INGEST_DIR_NAME = "ingest";

    /**
     * Overview of ingest/update directories that are skipped because they contain a file named 'processed'.
     */
    public static final String SKIPPED_REPORT_NAME = "skipped";

    /**
     * Overview of ingest/update directories that are fully processed.
     */
    public static final String PROCESSED_REPORT_NAME = "processed";

    /**
     * Overview of ingest/update directories that are rejected.
     */
    public static final String WARN_REPORT_NAME = "attention";

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    private final String workingDirectoryName;

    private File workingDirectory;

    private List<Task> tasks = new ArrayList<Task>();

    private JointMap joint;
    private int processedCount;
    private String reportLocation;
    private String currentDirectoryName;
    private boolean reportDetails;

    public BatchController() {
        this(DEFAULT_INGEST_DIR_NAME);
    }

    public BatchController(String workingDirectoryName) {
        this.workingDirectoryName = workingDirectoryName;
    }

    @Override
    public boolean needsAuthentication() {
        return super.needsAuthentication() || childrenNeedAuthentication();
    }

    private boolean childrenNeedAuthentication() {
        boolean childrenNeedAuthentication = false;
        for (Task taskStep : getTasks()) {
            childrenNeedAuthentication |= taskStep.needsAuthentication();
        }
        return childrenNeedAuthentication;
    }

    private File getWorkingDirectory() throws FatalTaskException {
        if (workingDirectory == null) {
            workingDirectory = new File(Application.getBaseDirectory(), workingDirectoryName);
            if (!workingDirectory.exists()) {
                throw new FatalTaskException("Working directory not found: " + workingDirectory.getPath(), this);
            }
            if (!workingDirectory.canRead()) {
                throw new FatalTaskException("Cannot read working directory: " + workingDirectory.getPath(), this);
            }
            if (!workingDirectory.canWrite()) {
                throw new FatalTaskException("Cannot write to working directory: " + workingDirectory.getPath(), this);
            }
            RL.info(new Event(RL.GLOBAL, "Working directory is " + workingDirectory.getPath()));
        }
        return workingDirectory;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onEvent(Event event) {
        if (reportDetails && currentDirectoryName != null) {
            event.setDetails("details", currentDirectoryName + ".txt", getReportLocation());

            event.addMessage(0, currentDirectoryName);
        }

        if (joint != null) {
            String storeId = !joint.hasDataset() ? "[-]" : joint.getDataset().getStoreId();
            event.addMessage(0, storeId);
        }
    }

    protected String getReportLocation() {
        if (reportLocation == null) {
            reportLocation = RL.getReportLocation().getAbsolutePath();
        }
        return reportLocation;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        this.joint = joint;

        File[] dirs = getDirectories();
        int dirCount = dirs.length;
        processedCount = 0;
        RL.info(new Event(RL.GLOBAL, "Found " + dirs.length + " directories to process"));
        for (File dir : dirs) {
            joint.clearCycleState();
            currentDirectoryName = dir.getName();
            joint.setCurrentDirectory(dir);
            logger.info(Printer.format("Reading " + currentDirectoryName));
            executeCycleSteps(joint);
        }
        String msg = "Processed " + processedCount + " out of " + dirCount + " resource collections.";
        RL.info(new Event(RL.GLOBAL, msg));
        logger.info(msg);
    }

    private void executeCycleSteps(JointMap joint) throws FatalTaskException {
        RL.info(new Event(RL.GLOBAL, "Executing taskSteps on " + currentDirectoryName));
        reportDetails = true;

        try {
            for (Task taskStep : getTasks()) {
                executeStep(joint, taskStep);
                if (joint.isCycleAbborted()) {
                    RL.info(new Event("Cycle abborted by " + taskStep.getTaskName()));
                    break;
                }
            }
            if (!joint.isCycleAbborted()) {
                processedCount++;
                DirectoryMarker.markAsProcessed(joint);
            }
            if (!joint.isCycleProcessingCompleted()) {
                RL.info(new Event(WARN_REPORT_NAME));
            }
        }
        catch (TaskCycleException e) {
            RL.error(new Event(RL.GLOBAL, e, "Could not process " + currentDirectoryName));
            RL.info(new Event(WARN_REPORT_NAME));
        }
        finally {
            reportDetails = false;
        }
    }

    private void executeStep(JointMap joint, Task taskStep) throws FatalTaskException {
        try {
            taskStep.run(joint);
        }
        catch (TaskException e) {
            joint.setFitForDraft(false);
            RL.error(new Event(RL.GLOBAL, e, "Warnings from cycle " + currentDirectoryName, ". Cycle continues validation, but will not ingest or update."));
        }
    }

    private File[] getDirectories() throws FatalTaskException {
        File[] directories = getWorkingDirectory().listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !isProcessed(file);
            }
        });
        return directories;
    }

    protected boolean isProcessed(File file) {
        boolean processed = false;
        for (File f : file.listFiles()) {
            if (DirectoryMarker.PROCESSED_INDICATOR_FILE_NAME.equals(f.getName()) || DirectoryMarker.INGESTED_INDICATOR_FILE_NAME.equals(f.getName())) {
                processed = true;
                RL.info(new Event(SKIPPED_REPORT_NAME, file.getPath(), "directory contains file named " + f.getName()));
            }
        }
        return processed;
    }

}
