package nl.knaw.dans.common.lang.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of a {@link Mailer} instance.
 * 
 * @author joke
 */
public abstract class CommonMailerConfiguration implements MailerConfiguration {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CommonMailer.class);
    private String smtpHost = null;
    private String smtpPassword = null;
    private String smtpUserName = null;
    private String smtpSessionAuthorisation = null;
    private String transportType = null;
    private String fromName = null;
    private String fromAddress = null;
    private Map<String, String> images = new HashMap<String, String>();

    /**
     * Creates a customized instance.
     * 
     * @param input
     *        The customized configuration values.
     * @throws IOException
     *         IOException If an error occurred when reading from the input stream.
     * @throws IllegalArgumentException
     *         If the input stream contains a malformed UniCode escape sequence.
     */
    public CommonMailerConfiguration(final InputStream inputStream) throws IOException {
        final Properties properties = new Properties();
        if (inputStream != null)
            properties.load(inputStream);

        transportType = properties.getProperty(TRANSPORT_TYPE_KEY, TRANSPORT_TYPE_DEFAULT);
        fromName = properties.getProperty(FROM_NAME_KEY);
        fromAddress = properties.getProperty(FROM_ADDRESS_KEY);
        smtpHost = properties.getProperty(SMTP_HOST_KEY);
        smtpPassword = properties.getProperty(SMTP_PASSWORD_KEY);
        smtpUserName = properties.getProperty(SMTP_USERNAME_KEY);
        setSmtpSessionAuthorisation(properties.getProperty(SMTP_SESSION_ATHORISATION_KEY, SMTP_SESSION_ATHORISATION_DEFAULT));
        for (String key : properties.keySet().toArray(new String[properties.size()])) {
            if (key.startsWith(IMAGE_KEY_PREFIX)) {
                images.put(key.replace(IMAGE_KEY_PREFIX, ""), (String) properties.get(key));
            }
        }
    }

    public CommonMailerConfiguration() throws IOException {
        this((InputStream) null);
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public void setSmtpUserName(String smtpUserName) {
        this.smtpUserName = smtpUserName;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public void setSenderName(String fromName) {
        this.fromName = fromName;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setSmtpSessionAuthorisation(String smtpSessionAuthorisation) {
        this.smtpSessionAuthorisation = smtpSessionAuthorisation;
    }

    public void setImageMap(Map<String, String> imageMap) {
        this.images = imageMap;
    }

    public String getSmtpSessionAuthorisation() {
        return smtpSessionAuthorisation;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public String getSmtpUserName() {
        return smtpUserName;
    }

    public String getTransportType() {
        return transportType;
    }

    public String getSenderName() {
        return fromName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public Map<String, String> getImageMap() {
        return images;
    }

}
