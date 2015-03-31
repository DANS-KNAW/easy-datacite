package nl.knaw.dans.easy;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

public class DataciteServiceTest {

    private static Logger savedLogger;
    private Logger loggerMock;

    @BeforeClass
    public static void beforeClass() {
        savedLogger = (Logger) getInternalState(DataciteService.class, "logger");
    }

    @Before
    public void before() {
        resetAll();
        loggerMock = createMock(Logger.class);
        setInternalState(DataciteService.class, "logger", loggerMock);
        setInternalState(DataciteResourcesBuilder.class, "logger", loggerMock);
    }

    @AfterClass
    public static void afterClass() {
        setInternalState(DataciteResourcesBuilder.class, "logger", savedLogger);
        setInternalState(DataciteService.class, "logger", savedLogger);
        resetAll();
    }

    @Test
    public void urlDoesNotExist() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        config.setDoiRegistrationUri("http://failing-url");
        DataciteService service = new DataciteService(config);
        try {
            replayAll();
            service.create(new EmdBuilder().build());
            fail("expecting an exception");
        }
        catch (DataciteServiceException e) {
            assertThat(e.getMessage(), containsString("UnknownHost"));
            verifyAll();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void noPasswordConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        replayAll();
        new DataciteService(config).create(new EmdBuilder().build());
        verifyAll();
    }

    @Test(expected = IllegalStateException.class)
    public void noUriConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        config.setDoiRegistrationUri("\t ");
        replayAll();
        new DataciteService(config).create(new EmdBuilder().build());
        verifyAll();
    }

    @Test
    public void userDoesNotExist() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        DataciteService service = new DataciteService(config);
        try {
            replayAll();
            service.create(new EmdBuilder().build());
            fail("expecting an exception");
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            assertThat(e.getMessage(), containsString("403"));
            verifyAll();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void noCredentialsConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        replayAll();
        new DataciteService(config).create(new EmdBuilder().build());
        verifyAll();
    }

    @Test
    public void xslNotFound() {
        // tested here (not only in the ResourcesBuilderTest) because
        // - now we cover more code of the configuration class
        // - here we have the before/after methods to assert the logging anyway
        final String xslFileName = "notFound.xsl";
        Capture<String> capturedMessage = expectLoggedError();
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setXslEmd2datacite(xslFileName);
        try {
            replayAll();
            new DataciteService(config);
            fail();
        }
        catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString(xslFileName));
            assertThat(capturedMessage.getValue(), containsString(xslFileName));
            assertThat(capturedMessage.getValue(), containsString("Stylesheet not found on classpath"));
            verifyAll();
        }
    }

    /**
     * Note that you could use curl on the commandline: curl -u fill-in-username:fill-in-password -i -X POST --data-binary "@datacite_valid_v3.xml"
     * https://datacite.tudelft.nl/dataciteapi/v3/resources?testMode=true -H "Content-Type: application/xml"
     */
    @Test
    public void createFailureDoiWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        Capture<String> capturedMessage = expectLoggedError();
        DataciteServiceConfiguration config = getConfigWithCredentialsFromJVM();
        EasyMetadata emd1 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-123").build();
        EasyMetadata emd2 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build();
        try {
            replayAll();
            new DataciteService(config).create(emd1, emd2);
            fail();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            // DataCite updated while we expected to create
            // could be publish/republish confusion, but worse: also the same DOI for two objects
            assertThat(capturedMessage.getValue(), containsString("created: 0"));
            assertThat(capturedMessage.getValue(), containsString("updated: 2"));
            assertThat(capturedMessage.getValue(), containsString("Creating 2"));
            verifyAll();
        }
    }

    @Test
    public void createSuccessCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        DataciteServiceConfiguration config = getConfigWithCredentialsFromJVM();

        Capture<String> capturedMessage = expectLoggedInfo();
        EasyMetadata emd = new EmdBuilder().replaceAll("dans-test-123", "dans-test-" + UUID.randomUUID()).build();
        // with a UUID we this test doesn't run into trouble if testMode is accidently false
        try {
            replayAll();
            new DataciteService(config).create(emd);
            // DataCite created while an update was expected
            // for example a republish while the publish somehow wasn't sent to DataCite
            assertThat(capturedMessage.getValue(), containsString("created: 1"));
            assertThat(capturedMessage.getValue(), containsString("updated: 0"));
            assertThat(capturedMessage.getValue(), containsString("Creating 1"));
            verifyAll();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            fail();
        }
    }

    @Ignore
    @Test
    public void inconsistenCounts() throws Exception {
        Capture<String> capturedMessage = expectLoggedError();
        // this test requires mocking the service
        // new DataciteService(config).update(emds);
        assertThat(capturedMessage.getValue(), containsString("created: 1"));
        assertThat(capturedMessage.getValue(), containsString("updated: 2"));
        assertThat(capturedMessage.getValue(), containsString("Updating 4"));
        verifyAll();
    }

    @Test
    public void updateSuccessWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        DataciteServiceConfiguration config = getConfigWithCredentialsFromJVM();

        Capture<String> capturedMessage = expectLoggedInfo();
        EasyMetadata[] emds = {new EmdBuilder().replaceAll("dans-test-123", "dans-test-123").build(),
                new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build()};
        try {
            replayAll();
            new DataciteService(config).update(emds);
            // DataCite did what was expected
            assertThat(capturedMessage.getValue(), containsString("created: 0"));
            assertThat(capturedMessage.getValue(), containsString("updated: 2"));
            assertThat(capturedMessage.getValue(), containsString("Updating 2"));
            verifyAll();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            fail();
        }
    }

    @Test
    public void updateWarningWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        DataciteServiceConfiguration config = getConfigWithCredentialsFromJVM();

        Capture<String> capturedMessage = expectLoggedWarn();
        EasyMetadata emd = new EmdBuilder().replaceAll("dans-test-123", "dans-test-" + UUID.randomUUID()).build();
        // with a UUID we this test doesn't run into trouble if testMode is accidently false
        try {
            replayAll();
            new DataciteService(config).update(emd);
            // DataCite created while an update was expected
            // for example a republish while the publish somehow wasn't sent to DataCite
            assertThat(capturedMessage.getValue(), containsString("created: 1"));
            assertThat(capturedMessage.getValue(), containsString("updated: 0"));
            assertThat(capturedMessage.getValue(), containsString("Updating 1"));
            verifyAll();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            fail();
        }
    }

    @Test
    public void maxiEmdWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        DataciteService dataciteService = new DataciteService(getConfigWithCredentialsFromJVM());

        EasyMetadata emd = new EmdBuilder("maxi-emd.xml").build();
        try {
            replayAll();
            dataciteService.create(emd);
            fail();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            // TODO fix EMD_doi_datacite_v3.xsl
            // TODO let scala generate EMD's
            Assert.assertThat(e.getMessage(), org.hamcrest.core.StringContains.containsString("HTTP error code : 400"));
            Assert.assertThat(e.getMessage(), org.hamcrest.core.StringContains.containsString("The attribute 'relationType' is required but missing."));
            verifyAll();
        }
    }

    @Test
    public void hackedStyleSheetWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        DataciteServiceConfiguration configWithCredentialsFromJVM = getConfigWithCredentialsFromJVM();
        DataciteService dataciteService = new DataciteService(configWithCredentialsFromJVM);

        hackXsl(dataciteService);
        EasyMetadata[] emds = {new EmdBuilder().build(), new EmdBuilder().build()};
        try {
            replayAll();
            dataciteService.update(emds);
            fail();
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            assertThat(e.getMessage(), containsString("HTTP error code : 400"));
            assertThat(e.getMessage(), containsString("Expected is one of ( {http://datacite.org/schema/kernel-3}titles"));
            verifyAll();
        }
    }

    private Capture<String> expectLoggedError() {
        Capture<String> capturedMessage = new Capture<String>();
        loggerMock.error((capture(capturedMessage)));
        expectLastCall().once();
        return capturedMessage;
    }

    private Capture<String> expectLoggedWarn() {
        Capture<String> capturedMessage = new Capture<String>();
        loggerMock.warn((capture(capturedMessage)));
        expectLastCall().once();
        return capturedMessage;
    }

    private Capture<String> expectLoggedInfo() {
        Capture<String> capturedMessage = new Capture<String>();
        loggerMock.info((capture(capturedMessage)));
        expectLastCall().once();
        return capturedMessage;
    }

    /** Hacks the configured style sheet that does not produce any title though required by DataCite */
    private void hackXsl(DataciteService dataciteService) throws Exception {
        String xsl = FileUtils.readFileToString(new File("src/main/resources/" + new DataciteServiceConfiguration().getXslEmd2datacite()));
        File hackedFile = new File("target/tmp.xsl");
        FileUtils.write(hackedFile, xsl.replace("<xsl:apply-templates select=\"emd:title\"/>", ""));
        URL url = new URL("FILE://" + hackedFile.getAbsolutePath());
        DataciteResourcesBuilder resourcesBuilder = getInternalState(dataciteService, DataciteResourcesBuilder.class);
        setInternalState(resourcesBuilder, "styleSheetURL", url);
    }

    private DataciteServiceConfiguration getConfigWithCredentialsFromJVM() {
        Properties systemProperties = System.getProperties();
        String userName = systemProperties.getProperty("datacite.user", "");
        String password = systemProperties.getProperty("datacite.password", "");
        ignoreIfJVMArgumentsMissing(userName, password);

        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername(userName.toString());
        config.setPassword(password.toString());
        return config;
    }

    private void ignoreIfNoWebAccess(DataciteServiceException e) {
        assumeThat(e.getMessage(), not(containsString("UnknownHost")));
    }

    private void ignoreIfJVMArgumentsMissing(String userName, String password) {
        assumeThat(userName, not(equalTo("")));
        assumeThat(password, not(equalTo("")));
    }
}
