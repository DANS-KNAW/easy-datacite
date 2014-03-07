package nl.knaw.dans.easy.mail;

public interface EasyMailer
{
    /**
     * Sends an E-mail to one or more recipients. Throws an {@link EasyMailerException} if an error
     * occurs in the attempt.
     * 
     * @param subject
     *        subject of the E-mail
     * @param recipients
     *        array of recipients
     * @param text
     *        text content of E-mail, or <code>null</code> if no text version should be sent
     * @param html
     *        HTML content of E-mail or <code>null</code> if no HTML version should be sent
     * @param attachments
     *        optional list of attachments to send with E-mail
     */
    void sendMail(String subject, String[] recipients, String text, String html, EasyMailerAttachment... attachments);

}
