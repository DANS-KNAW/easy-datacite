/**
 *
 */
package nl.knaw.dans.common.lang.mail;

import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapAlternativeParts;
import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapAttachementPart;
import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapBodyPart;
import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapHtmlBodyPart;
import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapRelatedParts;
import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapTextBodyPart;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author joke
 */
public class CommonMailer implements Mailer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonMailer.class);

    private final String smtpHost;
    private final String smtpPassword;
    private final String smtpUserName;
    private final String transportType;
    private final String sessionAuthorisation;

    /** derived from the configuration */
    private final InternetAddress sender;

    /** derived from the configuration */
    private final Map<String, BodyPart> images;

    /** allows a JUnit test to prevent spam caused by nightly builds */
    protected boolean skipSend;

    /**
     * Creates an instance with a customized configuration.
     *
     * @param configuration
     * @throws Mailer.MailerException in case of problems creating an address from
     *             {@link MailerConfiguration#getSmtpUserName()} and
     *             {@link MailerConfiguration#getSmtpPassword()}
     */
    public CommonMailer(final MailerConfiguration configuration) throws MailerException
    {
        sessionAuthorisation = configuration.getSmtpSessionAuthorisation();
        smtpHost = configuration.getSmtpHost();
        smtpPassword = configuration.getSmtpPassword();
        smtpUserName = configuration.getSmtpUserName();
        transportType = configuration.getTransportType();
        final String senderAddress = configuration.getFromAddress();
        final String senderName = configuration.getSenderName();
        checkMandatory(MailerConfiguration.SMTP_HOST_KEY, smtpHost);
        checkMandatory(MailerConfiguration.FROM_ADDRESS_KEY, senderAddress);
        checkMandatory(MailerConfiguration.FROM_NAME_KEY, senderName);
        sender = wrapSender(senderAddress, senderName);
        images = wrapImages(configuration.getImageMap());
    }

    private void checkMandatory(final String key, final String value) throws IllegalArgumentException
    {
        if (value == null || value.equals(""))
        {
            throw new IllegalArgumentException(key + " is not defined");
        }
    }

    /**
     * To be overridden in JUnit tests to prevent flooding mail boxes by nightly builds.
     *
     * @return false The default value for the test flag.
     */
    boolean skipSend()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see nl.knaw.dans.common.lang.mail.Mailer#sendSimpleMail(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public final void sendSimpleMail(final String subject, final String textContent, final String... receivers) throws MailerException
    {
        sendMail(subject, textContent, null, null, receivers);
    }

    @Override
    public void sendSimpleMail(String subject, String textContent, List<String> receivers) throws MailerException
    {
        sendSimpleMail(subject, textContent, receivers.toArray(new String[] {}));
    }

    /*
     * (non-Javadoc)
     *
     * @see nl.knaw.dans.common.lang.mail.Mailer#sendSimpleAttachments(java.lang.String,
     *      java.lang.String, nl.knaw.dans.common.lang.mail.Attachement[], java.lang.String)
     */
    public final void sendSimpleMail(final String subject, final String textContent, final Attachement[] attachments, final String... receivers)
            throws MailerException
    {
        sendMail(subject, textContent, null, attachments, receivers);
    }

    /*
     * (non-Javadoc)
     *
     * @see nl.knaw.dans.common.lang.mail.Mailer#sendHtml(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void sendMail(final String subject, final String textContent, final String htmlContent, final String... receivers) throws MailerException
    {
        sendMail(subject, textContent, htmlContent, null, receivers);
    }

    /*
     * (non-Javadoc)
     *
     * @see nl.knaw.dans.common.lang.mail.Mailer#sendMail(java.lang.String, java.lang.String,
     *      java.lang.String, nl.knaw.dans.common.lang.mail.Attachement[], java.lang.String)
     */
    public final void sendMail(final String subject, final String textContent, final String htmlContent, final Attachement[] attachments,
            final String... receivers) throws Mailer.MailerException
    {
        send(subject, textContent, htmlContent, attachments, receivers);
    }

    /** Peels a layer of the onion: encode recipient, catching and logging. */
    private void send(final String subject, final String textContent, final String htmlContent, final Attachement[] attachments, final String... receivers)
            throws IllegalArgumentException, MailerException
    {
        final String logLine = createLogLine(subject, textContent, receivers);
        final List<String> invalid = new ArrayList<String>();
        final InternetAddress[] recipients = wrapRecipients(invalid, receivers);

        if (recipients.length > 0)
        {
            try
            {
                send(subject, textContent, htmlContent, attachments, recipients);
            }
            catch (final MessagingException exception)
            {
                final String string = "could not send " + logLine;
                LOGGER.debug(string, exception);
                throw new MailerException(string, exception);
            }
        }
        if (invalid.size() > 0)
        {
            final String string = "invalid address(es): " + Arrays.toString(invalid.toArray()) + " for " + logLine;
            LOGGER.error(string);
            throw new MailerException(string, null);
        }
        LOGGER.debug("end " + logLine);
    }

    /** Peels another layer of the onion: encode the message */
    private void send(final String subject, final String textContent, final String htmlContent, final Attachement[] attachments,
            final InternetAddress[] recipients) throws MessagingException, AddressException, NoSuchProviderException, MailerException
    {
        final Session mailSession = getSession();
        // mailSession.setDebug(debug); // niet noodzakelijk
        final Message message = wrapMandatoryMessageParts(subject, recipients, mailSession);
        message.setSentDate(new Date());

        if (attachments == null || attachments.length == 0)
        {
            if (htmlContent == null)
                message.setText(textContent);
            else
                message.setContent(wrapHtmlWithPlainContent(textContent, htmlContent));
        }
        else
        {
            final Multipart mixedpart = new MimeMultipart("mixed");
            if (htmlContent == null)
                mixedpart.addBodyPart(wrapTextBodyPart(textContent));
            else
                mixedpart.addBodyPart(wrapBodyPart(wrapHtmlWithPlainContent(textContent, htmlContent)));
            for (final Attachement attachment : attachments)
                mixedpart.addBodyPart(wrapAttachementPart(attachment));
            message.setContent(mixedpart);
        }
        message.saveChanges();
        send(mailSession, message);
    }

    /**
     * Peels the last layer of the onion: send the message. Don't bother about exceptions except for
     * closing.
     */
    private void send(final Session mailSession, final Message message) throws NoSuchProviderException, MessagingException
    {
        final Transport transport = mailSession.getTransport(transportType);
        transport.connect(smtpHost, smtpUserName, smtpPassword);
        try
        {
            if (skipSend)
            {
                final String format = "Message with subject [%s] is not sent to [%s] because the test flag is raised.";
                final String recipients = Arrays.deepToString(message.getAllRecipients());
                final String logLine = String.format(format, message.getSubject(), recipients);
                LOGGER.warn(logLine);
            }
            else
            {
                transport.sendMessage(message, message.getAllRecipients());
            }
        }
        finally
        {
            transport.close();
        }
    }

    private Message wrapMandatoryMessageParts(final String subject, final InternetAddress[] recipients, final Session mailSession) throws MessagingException
    {
        final Message message = new MimeMessage(mailSession);
        message.setFrom(sender);
        message.setRecipients(Message.RecipientType.TO, recipients);
        message.setSubject(subject);
        message.setSentDate(new Date());
        return message;
    }

    private Multipart wrapHtmlWithPlainContent(final String textContent, final String htmlContent) throws MessagingException
    {
        final BodyPart bodyPart;
        if (images.size() == 0)
            bodyPart = wrapHtmlBodyPart(htmlContent);
        else
            bodyPart = wrapBodyPart(wrapRelatedParts(htmlContent, images));
        final BodyPart textPart = wrapTextBodyPart(textContent);
        return wrapAlternativeParts(textPart, bodyPart);
    }

    private InternetAddress wrapSender(final String address, final String name) throws Mailer.MailerException
    {
        try
        {
            return new InternetAddress(address, name);
        }
        catch (final UnsupportedEncodingException exception)
        {
            final String format = "Error encoding sender. Adress=[%s] Name=[%s] ";
            final String logLine = String.format(format, address, name);
            LOGGER.error(logLine);
            throw new CommonMailer.MailerException(logLine, exception);
        }
    }

    protected Map<String, BodyPart> wrapImages(final Map<String, String> images) throws Mailer.MailerException
    {
        final Map<String, BodyPart> wrappedImages = new HashMap<String, BodyPart>();
        for (final String key : images.keySet())
        {
            final String fileName = images.get(key);
            final File file = new File(fileName);
            final String ContentId = key.replace(MailerConfiguration.IMAGE_KEY_PREFIX, "");
            try
            {
                wrappedImages.put(key, wrapBodyPart(ContentId, file));
            }
            catch (final MessagingException exception)
            {
                final String logLine = String.format("Could not wrap configured image. Key=%s; File=%s", key, fileName);
                LOGGER.error(logLine, exception);
                throw new CommonMailer.MailerException(logLine, exception);
            }
        }
        return wrappedImages;
    }

    private InternetAddress[] wrapRecipients(final List<String> invalid, final String... receivers) throws Mailer.MailerException
    {

        final ArrayList<InternetAddress> recipients = new ArrayList<InternetAddress>();
        for (final String reciever : receivers)
        {
            try
            {
                recipients.add(new InternetAddress(reciever));
            }
            catch (final AddressException exception)
            {
                invalid.add(reciever);
            }
        }
        return recipients.toArray(new InternetAddress[recipients.size()]);
    }

    private Session getSession()
    {
        final Properties systemProperties = System.getProperties();
        systemProperties.put("mail.smtp.auth", sessionAuthorisation);
        return Session.getDefaultInstance(systemProperties, null);
    }

    private String createLogLine(final String subject, final String messageText, final String... receivers) throws IllegalArgumentException
    {
        if (subject == null || messageText == null || receivers == null || receivers.length == 0)
        {
            final String format = "no null arguments or length zero allowed. subject [%s] messageText [%s] receivers [%s]";
            final String errorMessage = String.format(format, subject, messageText, receivers);
            throw new IllegalArgumentException(errorMessage);
        }
        final String format = "message to %s with subject [%s]";
        final String logLine = String.format(format, Arrays.toString(receivers), subject);
        LOGGER.debug("start " + logLine);
        return logLine;
    }
}
