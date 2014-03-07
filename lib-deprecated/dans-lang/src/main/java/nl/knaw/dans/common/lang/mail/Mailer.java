package nl.knaw.dans.common.lang.mail;

import java.util.List;

/**
 * @author joke
 */
public interface Mailer
{

    /** anyone can catch, only subclasses can throw. */
    public final static class MailerException extends java.lang.Exception
    {
        private static final long serialVersionUID = 1L;

        // TODO others classes in the same package can throw they but should not
        protected MailerException(final String message, final Throwable cause)
        {
            super(message, cause);
        }
    }

    /**
     * Send a plain text message to one or more receivers. Same as
     * {@link #sendHtmlMail(String, String, String, String...)} with null as htmlContent.
     * 
     * @param subject
     *        Subject of the mail.
     * @param textContent
     *        Message to send.
     * @param receivers
     *        Receivers of the mail.
     * @throws Mailer.MailerException
     *         when a failure to encode or send mail.
     * @throws IllegalArgumentException
     *         if any of the arguments is null or has length zero
     */
    public abstract void sendSimpleMail(final String subject, final String textContent, final String... receivers) throws MailerException;

    public abstract void sendSimpleMail(final String subject, final String textContent, final List<String> recievers) throws MailerException;

    /**
     * Send a plain text message with zero or more attachments to one or more receivers. Same as
     * {@link #sendHtmlMail(String, String, String, String...)} with null as htmlContent.
     * 
     * @param subject
     *        Subject of the mail.
     * @param textContent
     *        Plain text body to send.
     * @param receivers
     *        Receivers of the mail.
     * @param attachments
     *        Optional attachments.
     * @throws Mailer.MailerException
     *         when a failure to encode or send mail.
     * @throws IllegalArgumentException
     *         if any of the arguments is null or has length zero
     */
    public abstract void sendSimpleMail(final String subject, final String textContent, final Attachement[] attachments, final String... receivers)
            throws MailerException;

    /**
     * Send a HTML message to one or more receivers.
     * 
     * @param subject
     *        Subject of the mail.
     * @param textContent
     *        Plain text body to send.
     * @param htmlContent
     *        Optional HTML body variant.
     * @param attachments
     *        Optional attachments.
     * @param receivers
     *        Receivers of the mail.
     * @throws Mailer.MailerException
     *         when a failure to encode or send mail.
     * @throws IllegalArgumentException
     *         if any of the arguments is null or has length zero
     */
    public abstract void sendMail(final String subject, final String textContent, final String htmlContent, final String... receivers)
            throws Mailer.MailerException;

    /**
     * Send a HTML message to one or more receivers with attachments.
     * 
     * @param subject
     *        Subject of the mail.
     * @param textContent
     *        Plain text body to send.
     * @param htmlContent
     *        Optional HTML body variant.
     * @param attachments
     *        Optional attachments.
     * @param receivers
     *        Receivers of the mail.
     * @throws Mailer.MailerException
     *         when a failure to encode or send mail.
     * @throws IllegalArgumentException
     *         if any of the arguments is null or has length zero
     */
    public abstract void sendMail(final String subject, final String textContent, final String htmlContent, final Attachement[] attachments,
            final String... receivers) throws MailerException;

}
