package nl.knaw.dans.easy;

import org.apache.commons.lang.StringUtils;

public class DataciteServiceConfiguration {
    private static final String DEFAULT_XSL = "xslt-files/EMD_doi_datacite_v3.xsl";
    private static final String DEFAULT_DATACITE_URL = "https://datacite.tudelft.nl/dataciteapi/v3/resources?testMode=true";

    private String username;
    private String password;
    private String doiRegistrationUri = DEFAULT_DATACITE_URL;
    private String xslEmd2datacite = DEFAULT_XSL;

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
        if (StringUtils.isBlank(doiRegistrationUri))
            throw new IllegalStateException("DataCite URI not configured");
        return doiRegistrationUri;
    }

    /**
     * @param doiRegistrationUri
     *        Defaults to TU-Delft with testMode=true.
     */
    public void setDoiRegistrationUri(String doiRegistrationUri) {
        this.doiRegistrationUri = doiRegistrationUri;
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
}
