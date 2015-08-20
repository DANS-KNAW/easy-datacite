package nl.knaw.dans.easy.ebiu;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.os.OS;
import nl.knaw.dans.common.lang.util.Args;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for using tools with multiple users on a Linux system.
 * <p/>
 * The expected folder structure is:
 * 
 * <pre>
 *   /home/{username}/batch/
 * </pre>
 * 
 * The <code>batch</code> folder is expected to have permission status a=rwx.
 * <p/>
 * This class may create:
 * 
 * <pre>
 *   -batch |
 *          |-logs----|
 *          |         |-{processname}-|
 *          |                         |-{date}-|
 *          |                                  |-std.log
 *          |                                  |-err.log
 *          |
 *          |-reports |
 *                    |-{processname}-|
 *                                    |-{date}
 * </pre>
 */
public class MultiUserNixConfiguration extends DefaultConfiguration {

    private static final String DEFAULT_PROCESS_NAME = "noname";

    private static final String DIR_HOME = "/home";
    private static final String DIR_BATCH = "batch";
    private static final String DIR_LOGS = "logs";
    private static final String DIR_REPORTS = "reports";

    private static final String FILE_STD_LOG = "std.log";
    private static final String FILE_ERR_LOG = "err.log";

    private static final String PATTERN_STD_FILE = "%p %d{HH:mm:ss,SSS} %m [%M] %C.(%F:%L)%n";
    private static final String PATTERN_ERR_FILE = "%p %d{HH:mm:ss,SSS} %m [%M] %C.(%F:%L)%n";
    private static final String PATTERN_STD_CONSOLE = "%p %d{HH:mm:ss,SSS} %m [%M] %C.(%F:%L)%n";
    private static final String PATTERN_ERR_CONSOLE = "%p %d{HH:mm:ss,SSS} %m [%M] %C.(%F:%L)%n";

    private static final Level LEVEL_STD_CONSOLE = Level.INFO;

    private static final Logger logger = LoggerFactory.getLogger(MultiUserNixConfiguration.class);

    public MultiUserNixConfiguration(Args prArgs) throws ConfigurationException {
        super(prArgs);
    }

    @Override
    public void configure() throws ConfigurationException {
        configureLogging();
        initializeApplication();
        doPostApplicationStartConfiguration();
    }

    @Override
    protected void configureLogging() throws ConfigurationException {
        try {
            File logDir = createLogDir();
            System.out.println("\nRedirecting log output to directory " + logDir.getPath());

            org.apache.log4j.Logger log4jRoot = org.apache.log4j.Logger.getRootLogger();
            log4jRoot.removeAllAppenders();

            if (getProgramArgs().isLoggingToConsole()) {
                createStandardConsoleAppender(log4jRoot);
                createErrorConsoleAppender(log4jRoot);
            }

            createStandardFileAppender(logDir, log4jRoot);
            createErrorFileAppender(logDir, log4jRoot);
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    protected String getSystemHomeDirectory() {
        return DIR_HOME;
    }

    private File createLogDir() throws IOException {
        File batchDir = getUserBatchDir();
        File logBase = createDirectory(batchDir, DIR_LOGS);
        File processBase = createDirectory(logBase, getProgramArgs().getProcessName(DEFAULT_PROCESS_NAME));
        File logDir = createDirectory(processBase, getProgramArgs().getStartDate().toString("yyyy-MM-dd_HH.mm.ss"));
        return logDir;
    }

    private File getUserBatchDir() throws IOException {
        File userHome = new File(System.getProperty("user.home"));
        if (!userHome.exists()) {
            throw new IOException("The user home directory '" + userHome.getPath() + "' does not exists.");
        }

        File batchDir = new File(userHome, DIR_BATCH);
        if (!batchDir.exists()) {
            throw new IOException("The batch directory '" + batchDir.getPath() + "' does not exists.");
        }
        if (!batchDir.canRead()) {
            throw new IOException("Cannot read from directory '" + batchDir.getPath() + "'");
        }
        if (!batchDir.canWrite()) {
            throw new IOException("Cannot write in directory '" + batchDir.getPath() + "'");
        }
        return batchDir;
    }

    private void createStandardFileAppender(File logDir, org.apache.log4j.Logger log4jRoot) throws IOException {
        String stdLog = logDir.getPath() + File.separator + FILE_STD_LOG;

        EnhancedPatternLayout epLayout = new EnhancedPatternLayout(PATTERN_STD_FILE);
        FileAppender fa = new FileAppender(epLayout, stdLog);
        log4jRoot.addAppender(fa);
        fa.setThreshold(Level.INFO);

        removeRestrictions(stdLog);
        logger.info("Created file appender for standard logging: " + stdLog);
    }

    private void createErrorFileAppender(File logDir, org.apache.log4j.Logger log4jRoot) throws IOException {
        String errLog = logDir.getPath() + File.separator + FILE_ERR_LOG;

        EnhancedPatternLayout epLayout = new EnhancedPatternLayout(PATTERN_ERR_FILE);
        FileAppender fa = new FileAppender(epLayout, errLog);
        fa.setThreshold(Level.ERROR);
        log4jRoot.addAppender(fa);

        removeRestrictions(errLog);
        logger.info("Created file appender for error logging: " + errLog);
    }

    private void createStandardConsoleAppender(org.apache.log4j.Logger log4jRoot) {
        EnhancedPatternLayout epLayout = new EnhancedPatternLayout(PATTERN_STD_CONSOLE);
        ConsoleAppender ca = new ConsoleAppender(epLayout, ConsoleAppender.SYSTEM_OUT);
        ca.setThreshold(LEVEL_STD_CONSOLE);
        log4jRoot.addAppender(ca);
        logger.info("Created console appender for standard logging.");
    }

    private void createErrorConsoleAppender(org.apache.log4j.Logger log4jRoot) {
        EnhancedPatternLayout epLayout = new EnhancedPatternLayout(PATTERN_ERR_CONSOLE);
        ConsoleAppender ca = new ConsoleAppender(epLayout, ConsoleAppender.SYSTEM_ERR);
        ca.setThreshold(Level.ERROR);
        log4jRoot.addAppender(ca);
        logger.info("Created console appender for error logging.");
    }

    @Override
    protected void doPostApplicationStartConfiguration() throws ConfigurationException {
        try {
            File batchDirectory = getUserBatchDir();
            Application.setBaseDirectory(batchDirectory);

            File reportDir = createReportDir();
            RL rl = RL.initialize(reportDir, true);
            rl.setReporter(Application.getReporter());
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }

    }

    private File createReportDir() throws IOException {
        File batchDir = getUserBatchDir();
        File reportBase = createDirectory(batchDir, DIR_REPORTS);
        File processBase = createDirectory(reportBase, getProgramArgs().getProcessName(DEFAULT_PROCESS_NAME));
        File reportDir = createDirectory(processBase, getProgramArgs().getStartDate().toString("yyyy-MM-dd_HH.mm.ss"));
        return reportDir;
    }

    private static File createDirectory(File parentDir, String dirName) throws IOException {
        File newDir = new File(parentDir, dirName);
        newDir.mkdirs();
        int retVal = OS.setAllRWX(newDir, System.out, System.err);
        if (retVal != 0) {
            throw new IOException("Could not remove restrictions on directory " + newDir.getPath());
        }
        return newDir;
    }

    private static void removeRestrictions(String filename) throws IOException {
        int retVal = OS.setAllRWX(filename, System.out, System.err);
        if (retVal != 0) {
            throw new IOException("Could not remove restrictions on file " + filename);
        }
    }

}
