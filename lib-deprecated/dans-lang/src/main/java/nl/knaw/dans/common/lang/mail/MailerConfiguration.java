package nl.knaw.dans.common.lang.mail;

import java.util.Map;

/**
 * Configuration of a {@link Mailer} instance.
 * 
 * @author joke
 */
public interface MailerConfiguration {
    /** anyone can catch, only subclasses can throw. */
    public final static class Exception extends java.lang.Exception {
        private static final long serialVersionUID = 1L;

        // TODO others classes in the same package can throw they but should not
        protected Exception(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public static final String SMTP_HOST_KEY = "mailer.smtp.host";
    public static final String SMTP_PASSWORD_KEY = "mailer.smtp.password";
    public static final String SMTP_USERNAME_KEY = "mailer.smtp.user";
    public static final String TRANSPORT_TYPE_KEY = "mailer.transporttype";
    public static final String SMTP_SESSION_ATHORISATION_KEY = "mailer.smtp.session.athorisation";
    public static final String FROM_NAME_KEY = "mailer.sender.name";
    public static final String FROM_ADDRESS_KEY = "mailer.sender.address";

    /**
     * Prefix for property keys specifying images for HTML messages. The key-portion after the prefix should equal the content-id in the src attribute of the
     * HTML message part. Images are only attached if the HTML message contains the content-ID. Example: </ul> <li>In the HTML variant of a message:
     * <code>&lt;img src="cid:logo"></code></li> <li>In a configuration file: <code>mailer.sender.image.logo=image.gif</code> <li>In a hard coded configuration:
     * <code>MailerConfiguration.IMAGE_KEY_PREFIX + "logo=image.gif\n"</code> </ul>
     */
    public static final String IMAGE_KEY_PREFIX = "mailer.sender.image.";

    /** Default value for the transport type. Always applied if omitted. */
    public static final String TRANSPORT_TYPE_DEFAULT = "smtp";

    /** Default value for the environment variable transport type. Always applied if omitted. */
    public static final String SMTP_SESSION_ATHORISATION_DEFAULT = "false";

    public abstract void setSmtpHost(String smtpHost);

    public abstract void setSmtpPassword(String smtpPassword);

    public abstract void setSmtpUserName(String smtpUserName);

    public abstract void setTransportType(String transportType);

    public abstract void setSenderName(String fromName);

    public abstract void setFromAddress(String fromAddress);

    public abstract void setSmtpSessionAuthorisation(String smtpSessionAuthorisation);

    void setImageMap(Map<String, String> imageMap);

    public abstract String getSmtpSessionAuthorisation();

    public abstract String getSmtpHost();

    public abstract String getSmtpPassword();

    public abstract String getSmtpUserName();

    public abstract String getTransportType();

    public abstract String getSenderName();

    public abstract String getFromAddress();

    public abstract Map<String, String> getImageMap();

}
