package nl.knaw.dans.easy.ebiu;

import java.io.File;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.log.Reporter;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.collections.DmoCollectionsAccess;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.search.DatasetSearch;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.data.userrepo.GroupRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.ebiu.exceptions.AbortException;
import nl.knaw.dans.easy.ebiu.exceptions.FatalRuntimeException;
import nl.knaw.dans.easy.ebiu.util.Dialogue;
import nl.knaw.dans.easy.ebiu.util.Printer;
import nl.knaw.dans.easy.security.Security;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String BN_FEDORA = "fedora";

    private static final String BN_EASY_STORE = "easyStore";

    private static final String BN_FILE_STORE_ACCESS = "fileStoreAccess";

    private static final String BN_DATASET_SEARCH = "datasetSearch";

    private static final String BN_SEARCH_ENGINE = "searchEngine";

    private static final String BN_USER_REPO = "userRepo";

    private static final String BN_GROUP_REPO = "groupRepo";

    private static final String BN_MIGRATION_REPO = "migrationRepo";

    private static final String BN_COLLECTION_ACCESS = "collectionAccess";

    private static final String BN_TASKRUNNER = "taskRunner";

    private static final String BN_REPORTER = "reporter";

    private static Application INSTANCE;

    private static EasyUser APPLICATION_USER;

    private static File BASE_DIRECTORY;

    private static TaskRunner TASKRUNNER;

    private final ApplicationContext context;

    private final Args prArgs;

    public static void initialize(Args prArgs) throws ConfigurationException {
        if (INSTANCE != null) {
            throw new IllegalStateException("Application already initialized.");
        }
        INSTANCE = new Application(prArgs);
    }

    private Application(Args prArgs) throws ConfigurationException {
        this.prArgs = prArgs;
        String contextFilename = prArgs.getApplicationContext();
        if (contextFilename == null) {
            context = new ClassPathXmlApplicationContext("application-context.xml");
        } else {
            context = new FileSystemXmlApplicationContext(contextFilename);
        }
        ((AbstractApplicationContext) context).registerShutdownHook();
        configureData(context);

        logger.info("Application started. Arguments:\n" + prArgs.printArguments());
    }

    private static Application getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Application not initialized.");
        }
        return INSTANCE;
    }

    public static void run() {
        try {
            RL.info(new Event(RL.GLOBAL, "starting TaskRunner"));
            getTaskRunner().execute(new JointMap());
            RL.info(new Event(RL.GLOBAL, "closing application"));
        }
        finally {
            RL.close();
        }

    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    public static Args getProgramArgs() {
        return getInstance().prArgs;
    }

    public static DateTime getStartDate() {
        return getProgramArgs().getStartDate();
    }

    public static File getBaseDirectory() {
        if (BASE_DIRECTORY == null) {
            BASE_DIRECTORY = new File(".");
        }
        return BASE_DIRECTORY;
    }

    protected static void setBaseDirectory(File baseDirectory) {
        BASE_DIRECTORY = baseDirectory;
        logger.info("Base directory set at " + BASE_DIRECTORY.getPath());
    }

    public static Fedora getFedora() {
        return (Fedora) getInstance().context.getBean(BN_FEDORA);
    }

    public static TaskRunner getTaskRunner() {
        if (TASKRUNNER == null) {
            if (getInstance().context.containsBean(BN_TASKRUNNER)) {
                TASKRUNNER = (TaskRunner) getInstance().context.getBean(BN_TASKRUNNER);
            } else {
                TASKRUNNER = new TaskRunner();
            }
        }
        return TASKRUNNER;
    }

    public static Reporter getReporter() {
        Reporter reporter;
        if (getInstance().context.containsBean(BN_REPORTER)) {
            reporter = (Reporter) getInstance().context.getBean(BN_REPORTER);
        } else {
            reporter = new TaskRunnerReporter(getTaskRunner());
        }
        return reporter;
    }

    private void configureData(ApplicationContext context) {
        Data data = new Data();
        if (context.containsBean(BN_EASY_STORE)) {
            data.setEasyStore((EasyStore) context.getBean(BN_EASY_STORE));
        }
        if (context.containsBean(BN_FILE_STORE_ACCESS)) {
            data.setFileStoreAccess((FileStoreAccess) context.getBean(BN_FILE_STORE_ACCESS));
        }
        if (context.containsBean(BN_DATASET_SEARCH)) {
            data.setDatasetSearch((DatasetSearch) context.getBean(BN_DATASET_SEARCH));
        }
        if (context.containsBean(BN_SEARCH_ENGINE)) {
            data.setSearchEngine((SearchEngine) context.getBean(BN_SEARCH_ENGINE));
        }
        if (context.containsBean(BN_USER_REPO)) {
            data.setUserRepo((EasyUserRepo) context.getBean(BN_USER_REPO));
        }
        if (context.containsBean(BN_GROUP_REPO)) {
            data.setGroupRepo((GroupRepo) context.getBean(BN_GROUP_REPO));
        }
        if (context.containsBean(BN_MIGRATION_REPO)) {
            data.setMigrationRepo((MigrationRepo) context.getBean(BN_MIGRATION_REPO));
        }
        if (context.containsBean(BN_COLLECTION_ACCESS)) {
            data.setCollectionAccess((DmoCollectionsAccess) context.getBean(BN_COLLECTION_ACCESS));
        }
    }

    public static EasyUser authenticate() {
        if (APPLICATION_USER == null) {
            Printer.println("Authentication required");

            try {
                boolean authenticated = false;
                int tryCount = 0;
                String username = null;
                String pass;
                while (!authenticated && tryCount < 3) {
                    tryCount++;
                    username = Dialogue.getInput("(easy) username:");
                    pass = Dialogue.readPass("(easy) password:");
                    authenticated = Data.getUserRepo().authenticate(username, pass);
                    if (!authenticated) {
                        System.out.println("Invalid username or pass.");
                    }

                }
                if (!authenticated) {
                    throw new FatalRuntimeException("Invalid username or pass.");
                } else {
                    APPLICATION_USER = Data.getUserRepo().findById(username);
                }
                checkSecurityConfirmation();
            }
            catch (RepositoryException e) {
                throw new FatalRuntimeException("Unable to authenticate", e);
            }
        }
        return APPLICATION_USER;
    }

    public static EasyUser getApplicationUser() {
        return APPLICATION_USER;
    }

    private static void checkSecurityConfirmation() {
        if (Security.getAuthz() instanceof NoAuthz) {
            boolean confirmed = Dialogue.confirm("Warn! Security implemented by " + Security.getAuthz() + "\nDo you want to continue?");
            if (!confirmed) {
                throw new AbortException("Aborted by user.");
            }
        }
    }

}
