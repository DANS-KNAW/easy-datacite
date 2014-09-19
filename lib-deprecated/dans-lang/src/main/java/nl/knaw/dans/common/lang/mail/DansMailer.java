package nl.knaw.dans.common.lang.mail;

import java.io.IOException;

public class DansMailer extends CommonMailer {
    /**
     * Creates a {@link Mailer} instance with a customized configuration.
     * 
     * @param configuration
     * @throws Mailer.MailerException
     *         in case of problems creating an address from {@link MailerConfiguration#getSmtpUserName()} and {@link MailerConfiguration#getSmtpPassword()}
     */
    public DansMailer(MailerConfiguration configuration) throws MailerException {
        super(configuration);
    }

    /** Lazy initialization to catch exceptions */
    private static Mailer defaultInstance = null;

    /**
     * Gets a {@link Mailer} instance with a default configuration.
     * 
     * @throws CommonMailer.MailerException
     *         in case of problems creating an address from {@link DansMailerConfiguration#getSmtpUserName()} and
     *         {@link DansMailerConfiguration#getSmtpPassword()}
     * @throws MailerConfiguration.Exception
     *         An unexpected {@link IOException} of {@link #MailerProperties(InputStream)} is turned into a runtime exception.
     */
    public final static Mailer getDefaultInstance() throws MailerException, MailerConfiguration.Exception {
        if (defaultInstance == null)
            defaultInstance = new CommonMailer(DansMailerConfiguration.getDefaultInstance());
        return defaultInstance;
    }

}
