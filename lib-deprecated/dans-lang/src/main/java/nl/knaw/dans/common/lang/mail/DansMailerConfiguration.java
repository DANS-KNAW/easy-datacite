package nl.knaw.dans.common.lang.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration of a {@link Mailer} instance.
 * 
 * @author Joke Pol
 */
public final class DansMailerConfiguration extends CommonMailerConfiguration implements MailerConfiguration
{
    /** The default value for the SMTP host. */
    public static final String SMTP_HOST_DEFAULT = "mailrelay.knaw.nl";

    /** Default value for the senders e-mail address. */
    public static final String FROM_ADDRESS_DEFAULT = "info@dans.knaw.nl";

    /** Default value for the senders name. */
    public static final String FROM_NAME_DEFAULT = "DANS Team";

    /** Lazy initialization to catch exceptions */
    private static MailerConfiguration defaultInstance = null;

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
    public DansMailerConfiguration(final InputStream inputStream) throws IOException
    {
        super(inputStream);
        if (getSmtpHost() == null)
            setSmtpHost(SMTP_HOST_DEFAULT);
        if (getSenderName() == null)
            setSenderName(FROM_NAME_DEFAULT);
        if (getFromAddress() == null)
            setFromAddress(FROM_ADDRESS_DEFAULT);
    }

    /**
     * Creates a customized instance. Calls {@link #MailerProperties(InputStream)} with a wrapped string.
     * 
     * @param input
     *        The customized configuration values. If {@link #SMTP_HOST_KEY} is not specified, no host
     *        will be set and no mails will be sent.
     * @return A customized instance.
     * @throws MailerConfiguration.Exception
     *         An unexpected {@link IOException} of {@link #MailerProperties(InputStream)} is turned into
     *         a runtime exception.
     */
    public static MailerConfiguration createCustomized(final String input) throws Exception
    {
        try
        {
            final InputStream inputStream = input == null ? (InputStream) null : new ByteArrayInputStream(input.getBytes());
            return new DansMailerConfiguration(inputStream);
        }
        catch (final IOException exception)
        {
            throw new Exception("Unexpected exception", exception);
        }
    }

    /**
     * Gets a default instance. Calls {@link #MailerProperties(InputStream)} with a null argument.
     * 
     * @return A default instance.
     * @throws MailerConfiguration.Exception
     *         An unexpected {@link IOException} of {@link #MailerProperties(InputStream)} is turned into
     *         a runtime exception.
     */
    public static MailerConfiguration getDefaultInstance() throws Exception
    {
        if (defaultInstance == null)
        {
            try
            {
                defaultInstance = new DansMailerConfiguration((InputStream) null);
            }
            catch (final IOException e)
            {
                throw new Exception("Unexpected exception", e);
            }
        }
        return defaultInstance;
    }
}
