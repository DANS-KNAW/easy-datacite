package nl.knaw.dans.common.lang.mail;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationMailerConfiguration implements MailerConfiguration
{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMailerConfiguration.class);

    private String smtpHost;
    private String smtpUserName;
    private String smtpPassword;
    private String smtpSessionAuthorisation;
    private String transportType;
    private String senderName;
    private String fromAddress;
    private Map<String, String> imageMap = Collections.emptyMap();

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost)
    {
        this.smtpHost = smtpHost;
        logger.info("smtpHost=" + smtpHost);
    }

    public String getSmtpUserName()
    {
        return smtpUserName;
    }

    public void setSmtpUserName(String smtpUserName)
    {
        this.smtpUserName = smtpUserName;
        logger.info("smtpUserName=" + smtpHost);
    }

    public String getSmtpPassword()
    {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword)
    {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpSessionAuthorisation()
    {
        return smtpSessionAuthorisation == null ? SMTP_SESSION_ATHORISATION_DEFAULT : smtpSessionAuthorisation;
    }

    public void setSmtpSessionAuthorisation(String smtpSessionAuthorisation)
    {
        this.smtpSessionAuthorisation = smtpSessionAuthorisation;
    }

    public String getTransportType()
    {
        return transportType == null ? TRANSPORT_TYPE_DEFAULT : transportType;
    }

    public void setTransportType(String transportType)
    {
        this.transportType = transportType;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }

    public Map<String, String> getImageMap()
    {
        return imageMap;
    }

    public void setImageMap(Map<String, String> images)
    {
        this.imageMap = images;
    }

}
