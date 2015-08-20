package nl.knaw.dans.easy.ebiu;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.data.Data;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class OnlineTest {
    private static final String INSTRUCTION_FILE_PATH = "videos/interview01.mpeg.properties";
    private static final String EMD_FILE_NAME = "easymetadata.xml";
    private static final String RMD_FILE_NAME = "resource-metadata-list.xml";
    private static final String AMD_FILE_NAME = "administrative-metadata.xml";
    private static final String USER_HOME = "user.home";
    private static final File BATCH_FOLDER = new File("target/batch");
    private static final File INGEST_FOLDER = new File(BATCH_FOLDER, "ingest");
    private static final File FILE_DATA_FOLDER = new File(INGEST_FOLDER, "dataset/filedata");
    private static final File META_DATA_FOLDER = new File(INGEST_FOLDER, "dataset/metadata");
    private static final String[] jobFolders = {"ingest", "update-files", "update-metadata"};

    @BeforeClass
    public static void cleanBatchFolder() throws Exception {
        BATCH_FOLDER.mkdirs();
        FileUtils.cleanDirectory(BATCH_FOLDER);
        for (final String folder : jobFolders)
            new File(BATCH_FOLDER, folder).mkdirs();
        System.setProperty(USER_HOME, BATCH_FOLDER.getParentFile().getCanonicalPath());
    }

    @BeforeClass
    public static void verifyConfiguration() throws Exception {
        verifyHomeVariable();
        verifyUser();
    }

    private static void verifyHomeVariable() {
        if (System.getProperty("EASY_EBIU_HOME") == null) {
            // the folder should contain a copy of (or link to)
            // res/lic/*/* and cfg/application.properties in src/main/assembly/dist
            // the tests make hard coded use of cfg/spring/*
            final StringBuffer message = new StringBuffer();
            message.append("Please specify the JVM argument '-DEASY_EBIU_HOME=xxx'\n");
            message.append("xxx can be the same as your EASY_WEBUI_HOME, just add the properties:\n");
            message.append("  easy.ebiu.proxyUserAllowed=true\n");
            message.append("  easy.ebiu.needsAuthentication=false\n");
            message.append("  easy.ebiu.archivistRequired=false\n");
            throw new IllegalArgumentException(message.toString());
        }
    }

    public static void verifyUser() throws Exception {
        configure("ingest");
        try {
            Data.getUserRepo().findById(System.getProperty("user.name"));
        }
        catch (ObjectNotInStoreException e) {
            throw new IllegalStateException("Your EVM should have an easy-user with an ID equal to your mac-user.\n");
        }
    }

    @Before
    public void resetStatics() throws Exception {
        Whitebox.setInternalState(Application.class, "INSTANCE", (Application) null);
        Whitebox.setInternalState(Application.class, "TASKRUNNER", (Application) null);
        Whitebox.setInternalState(Application.class, "BASE_DIRECTORY", (Application) null);
        Whitebox.setInternalState(Application.class, "APPLICATION_USER", (Application) null);
        Whitebox.setInternalState(RL.class, "INSTANCE", (Application) null);
    }

    @After
    /**
     * Clear folders to make the tests independent but keep the logging
     * @throws Exception
     */
    public void cleanJobFolders() throws Exception {
        for (final String folder : jobFolders)
            FileUtils.cleanDirectory(new File(BATCH_FOLDER, folder));
    }

    @AfterClass
    public static void clearSystemProperties() {
        System.getProperties().remove(USER_HOME);
    }

    @Test
    public void ingestWithoutAnyFiles() throws Exception {
        // just create the folder of a dataset to ingest
        FILE_DATA_FOLDER.getParentFile().mkdirs();

        mockDateTimeNow("2014-01-01");
        runApplication("ingest");
        assertDetailsReportContains("No easymetadata", "No resource metadata", "No administrative metadata");
    }

    @Test
    public void ingestWithOneFile() throws Exception {
        writeFile(new File(FILE_DATA_FOLDER, "some.txt"), "hello world");
        createDefaultMetadata();

        mockDateTimeNow("2014-01-01");
        runApplication("ingest");
        // used to assert for "original/some.txt", but that is only found if the user is not an archivist
        assertDetailsReportContains("Collected easymetadata", "Collected administrative metadata", "Ingested datasetItem");
        // so far this is not much more than a smoke test
    }

    @Test
    public void ingestAVProperties() throws Exception {
        final String springfieldPath = "domain/DANS/user/Gemeente_Schiedam/collection/Getuigenverhalen/presentation/interview01";
        final String now = "2014-05-02";
        createInstructionsFile(now, springfieldPath);
        createMetadataForAV();

        mockDateTimeNow(now);
        runApplication("ingest");
        assertDetailsReportContains("original/videos/interview01.mpeg.properties");
        // to complete restoration of this test
        // see commit Ie0759385a72ff26f5164dc742f8dffa3997afcec 2015-02-02 15:34:16
    }

    @Test
    public void ingestPrepared() throws Exception {
        // use the result of a "prepare for ingest" command
        FileUtils.copyDirectory(new File("src/test/resources/prepared"), INGEST_FOLDER);
        patchDepositorID(INGEST_FOLDER.listFiles());

        final String now = "2014-05-04";
        mockDateTimeNow(now);

        runApplication("ingest");

        // Main.main(createCmdArgs("ingest"));
        assertTrue(new File("target/batch/reports/noname/" + now + "_00.00.00/details/dataset-1.txt").isFile());
        assertTrue(new File("target/batch/reports/noname/" + now + "_00.00.00/details/dataset-2.txt").isFile());
    }

    /**
     * creates metadata files for the one and only ingested dataset
     * 
     * @throws IOException
     */
    private void createDefaultMetadata() throws IOException {
        writeFile(new File(META_DATA_FOLDER, EMD_FILE_NAME), readResource("emd.xml"));
        writeFile(new File(META_DATA_FOLDER, RMD_FILE_NAME), readResource("rmd.xml"));
        writeFile(new File(META_DATA_FOLDER, AMD_FILE_NAME), readResource("amd.xml"));
        patchDepositorID(INGEST_FOLDER.listFiles());
    }

    /**
     * creates metadata files for the one and only ingested dataset
     * 
     * @throws IOException
     */
    private void createMetadataForAV() throws IOException {
        // without the specific format no audio/video tab appears in the webui
        final String patchedEMD = readResource("emd.xml").replace("<emd:format>", "<emd:format><dc:format>video</dc:format>");
        writeFile(new File(META_DATA_FOLDER, EMD_FILE_NAME), patchedEMD);
        writeFile(new File(META_DATA_FOLDER, RMD_FILE_NAME), readResource("rmd.xml"));
        writeFile(new File(META_DATA_FOLDER, AMD_FILE_NAME), readResource("amd.xml"));
        patchDepositorID(INGEST_FOLDER.listFiles());
    }

    /**
     * @param now
     *        becomes part of the sip-path in the instruction file
     * @param springfieldPath
     *        value for the instruction file
     * @throws IOException
     */
    private void createInstructionsFile(final String now, final String springfieldPath) throws IOException {
        final StringBuffer properties1 = new StringBuffer();
        properties1.append("audio-video.ebiu-instructions=yes\n");
        properties1.append("audio-video.springfield-path=" + springfieldPath + "\n");
        properties1.append("audio-video.sip-path=sip-" + now + "/" + INSTRUCTION_FILE_PATH + "\n");
        properties1.append("audio-video.file-md5=12345\n");
        final String properties = properties1.toString();
        writeFile(new File(FILE_DATA_FOLDER, INSTRUCTION_FILE_PATH), properties.toString());
    }

    /**
     * Replaces the user "depositorid" in the administrative metadata of the datasets by the user used to run the test
     * 
     * @param datasets
     * @throws IOException
     */
    private void patchDepositorID(File[] datasets) throws IOException {
        for (final File dataset : datasets)
            if (!dataset.isHidden()) {
                final File file = new File(dataset, "metadata/" + AMD_FILE_NAME);
                final String tag = "<depositorId>";
                final String actualID = System.getProperty("user.name");
                writeFile(file, FileUtils.readFileToString(file).replace(tag + "depositorid", tag + actualID));
            }
    }

    /**
     * Asserts that the report details contains the expected strings.
     * 
     * @param expectedSubstrings
     *        expected (parts of) lines
     * @throws IOException
     */
    private void assertDetailsReportContains(final String... expectedSubstrings) throws IOException {
        final String reportDetails = readFileToString(createReportDetailsFileName());
        for (final String subString : expectedSubstrings)
            Assert.assertThat(reportDetails, containsString(subString));
    }

    /** @return the filename that is expected as a result from the test */
    private File createReportDetailsFileName() {
        final String dateTime = DateTime.now().toString("yyyy-MM-dd") + "_00.00.00";
        return new File(BATCH_FOLDER + "/reports/noname/" + dateTime + "/details/dataset.txt");
    }

    /**
     * Read a file.
     * 
     * @param string
     *        file name in the resource folder
     * @return
     * @throws IOException
     */
    private String readResource(final String string) throws IOException {
        return readFileToString(new File("src/test/resources/" + string));
    }

    /**
     * Writes data to a file and creates parent folders if they don't yet exist.
     * 
     * @param file
     * @param data
     * @throws IOException
     */
    private void writeFile(final File file, final String data) throws IOException {
        file.getParentFile().mkdirs();
        FileUtils.write(file, data);
    }

    private void runApplication(String type) throws ConfigurationException {
        // the junit framework doesn't like System.exit(..)
        // so we have to duplicate the essential code of the main class
        configure(type);
        Application.run();
    }

    private static void configure(String type) throws ConfigurationException {
        // mimic command line arguments of src/main/assembly/dist/bin/*.sh
        // "process.name=<type>" is omitted because it would suppress the asserted report files
        final String springConfig = "application.context=/src/main/assembly/dist/cfg/spring/" + type + "-context.xml";
        Args args = new Args(new String[] {springConfig, "log.console=true"});
        new MultiUserNixConfiguration(args).configure();
    }

    /**
     * Causes DateTime.now() to return a predefined value.
     * 
     * @param value
     *        a value used in assertions
     */
    private void mockDateTimeNow(final String value) {
        DateTimeUtils.setCurrentMillisFixed(new DateTime(value).getMillis());
    }
}
