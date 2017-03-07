package nl.knaw.dans.easy;

import java.net.URL;

import org.apache.commons.lang.StringUtils;

public class DataciteServiceConfiguration {
    private static final String DEFAULT_XSL_VERSION = "4"; // Note that if the datacite version is updated the proai.properties needs to be updated as well!
                                                           // Change the driver.fedora.md.format.oai_datacite.loc and driver.fedora.md.format.oai_datacite.uri
                                                           // values accordingly.
    private static final String DEFAULT_XSL = String.format("xslt-files/EMD_doi_datacite_v%s.xsl", DEFAULT_XSL_VERSION);
    private static final String DEFAULT_DATACITE_DOI_URI = "https://mds.test.datacite.org/doi";
    private static final String DEFAULT_DATACITE_DOI_CONTENT_TYPE = "text/plain;charset=UTF-8";
    private static final String DEFAULT_DATACITE_METADATA_URI = "https://mds.test.datacite.org/metadata";
    private static final String DEFAULT_DATACITE_METADATA_CONTENT_TYPE = "application/xml";

    private String username;
    private String password;
    private String doiRegistrationUri = DEFAULT_DATACITE_DOI_URI;
    private String doiRegistrationContentType = DEFAULT_DATACITE_DOI_CONTENT_TYPE;
    private String metadataRegistrationUri = DEFAULT_DATACITE_METADATA_URI;
    private String metadataRegistrationContentType = DEFAULT_DATACITE_METADATA_CONTENT_TYPE;
    private String xslVersion = DEFAULT_XSL_VERSION;
    private String xslEmd2datacite = DEFAULT_XSL;
    private URL datasetResolver;

    public String getUsername() {
        if (StringUtils.isBlank(username))
            throw new IllegalStateException("DataCite username not configured");
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (StringUtils.isBlank(password))
            throw new IllegalStateException("DataCite password not configured");
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDoiRegistrationUri() {
        if (StringUtils.isBlank(this.doiRegistrationUri))
            throw new IllegalArgumentException("DataCite DOI registration URI not configured");
        return this.doiRegistrationUri;
    }

    public void setDoiRegistrationUri(String doiRegistrationUri) {
        this.doiRegistrationUri = doiRegistrationUri;
    }

    public String getDoiRegistrationContentType() {
        if (StringUtils.isBlank(this.doiRegistrationContentType))
            throw new IllegalArgumentException("DataCite DOI registration content type not configured");
        return this.doiRegistrationContentType;
    }

    public void setDoiRegistrationContentType(String doiRegistrationContentType) {
        this.doiRegistrationContentType = doiRegistrationContentType;
    }

    public String getMetadataRegistrationUri() {
        if (StringUtils.isBlank(this.metadataRegistrationUri))
            throw new IllegalArgumentException("DataCite metadata registration URI not configured");
        return this.metadataRegistrationUri;
    }

    public void setMetadataRegistrationUri(String metadataRegistrationUri) {
        this.metadataRegistrationUri = metadataRegistrationUri;
    }

    public String getMetadataRegistrationContentType() {
        if (StringUtils.isBlank(this.metadataRegistrationContentType))
            throw new IllegalArgumentException("DataCite metadata registration content type not configured");
        return this.metadataRegistrationContentType;
    }

    public void setMetadataRegistrationContentType(String metadataRegistrationContentType) {
        this.metadataRegistrationContentType = metadataRegistrationContentType;
    }

    public String getXslEmd2datacite() {
        if (StringUtils.isBlank(xslEmd2datacite))
            throw new IllegalStateException("Missing XSLT style sheet for EMD to DataCite transformation.");
        return xslEmd2datacite;
    }

    /**
     * @param xslEmd2datacite
     *        Location on the class path of XSL file that converts an EMD to a DataCite resource. NOTE: different from OAI version (EMD_oai_datacite.xsl).
     *        Defaults to the location as packaged in the jar.
     */
    public void setXslEmd2datacite(String xslEmd2datacite) {
        this.xslEmd2datacite = xslEmd2datacite;
    }

    public String getXslVersion() {
        if (StringUtils.isBlank(this.xslVersion))
            throw new IllegalArgumentException("DataCite XSLT style sheet version not configured");
        return xslVersion;
    }

    public void setXslVersion(String xslVersion) {
        this.xslVersion = xslVersion;
    }

    public URL getDatasetResolver() {
        if (datasetResolver == null)
            throw new IllegalStateException("Host to resolve datasets by fedoraID is not configured.");
        return datasetResolver;
    }

    public void setDatasetResolver(URL datasetResolver) {
        this.datasetResolver = datasetResolver;
    }
}
