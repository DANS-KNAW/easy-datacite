package nl.knaw.dans.easy;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.junit.Test;

public class DataciteServiceTest {

    @Test
    public void urlDoesNotExist() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        config.setDoiRegistrationUri("http://failing-url");
        DataciteService service = new DataciteService(config);
        try {
            service.create(new EmdBuilder().build());
            fail("expecting an exception");
        }
        catch (DataciteServiceException e) {
            assertThat(e.getMessage(), containsString("UnknownHost"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void noPasswordConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        new DataciteService(config).create(new EmdBuilder().build());
    }

    @Test(expected = IllegalStateException.class)
    public void noUriConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        config.setDoiRegistrationUri("\t ");
        new DataciteService(config).create(new EmdBuilder().build());
    }

    @Test
    public void userDoesNotExist() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        config.setUsername("failing-name");
        config.setPassword("failing-passwd");
        DataciteService service = new DataciteService(config);
        try {
            service.create(new EmdBuilder().build());
            fail("expecting an exception");
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
            assertThat(e.getMessage(), containsString("403"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void noCredentialsConfigured() throws Exception {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        new DataciteService(config).create(new EmdBuilder().build());
    }

    @Test
    public void xslNotFound() {
        String fileName = "notFound.xsl";
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        // not tested with the ResourcesBuilder as now we cover more code of the configuration class
        config.setXslEmd2datacite(fileName);
        try {
            new DataciteService(config);
        }
        catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString(fileName));
        }
    }

    /**
     * Note that you could use curl on the commandline: curl -u fill-in-username:fill-in-password -i -X POST --data-binary "@datacite_valid_v3.xml"
     * https://datacite.tudelft.nl/dataciteapi/v3/resources?testMode=true -H "Content-Type: application/xml"
     */
    @Test
    public void createDoiWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        // covering more branches of the private send method requires mocking the service
        DataciteServiceConfiguration config = getCredentialFromJVM();
        EasyMetadata emd1 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-123").build();
        EasyMetadata emd2 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build();
        try {
            new DataciteService(config).create(emd1, emd2);
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
        }
    }

    @Test
    public void updateDoiWithCredentialsFromJvmArguments() throws Exception {
        // the name of the method makes it easy to guess one of the causes why the test is eventually ignored
        // covering more branches of the private send method requires mocking the service
        DataciteServiceConfiguration config = getCredentialFromJVM();
        EasyMetadata emd1 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-123").build();
        EasyMetadata emd2 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build();
        try {
            new DataciteService(config).update(emd1, emd2);
        }
        catch (DataciteServiceException e) {
            ignoreIfNoWebAccess(e);
        }
    }

    private DataciteServiceConfiguration getCredentialFromJVM() {
        DataciteServiceConfiguration config = new DataciteServiceConfiguration();
        Object userName = System.getProperties().get("datacite.user");
        Object password = System.getProperties().get("datacite.password");
        ignoreIfJVMArgumentsMissing(userName, password);
        config.setUsername(userName.toString());
        config.setPassword(password.toString());
        return config;
    }

    private void ignoreIfNoWebAccess(DataciteServiceException e) {
        assumeThat(e.getMessage(), not(containsString("UnknownHost")));
        assumeThat(e.getMessage(), not(containsString("503")));
    }

    private void ignoreIfJVMArgumentsMissing(Object userName, Object password) {
        assumeThat(userName, notNullValue());
        assumeThat(password, notNullValue());
    }
}
