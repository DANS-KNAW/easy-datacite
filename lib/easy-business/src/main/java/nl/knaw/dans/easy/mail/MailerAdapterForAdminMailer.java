package nl.knaw.dans.easy.mail;

import java.util.List;

import nl.knaw.dans.common.lang.mail.Attachement;
import nl.knaw.dans.common.lang.mail.Mailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailerAdapterForAdminMailer implements Mailer {
    private final Logger log = LoggerFactory.getLogger(MailerAdapterForAdminMailer.class);

    private final EasyMailer mailer;

    public MailerAdapterForAdminMailer(EasyMailer mailer) {
        this.mailer = mailer;
    }

    @Override
    public void sendSimpleMail(String subject, String textContent, String... receivers) throws MailerException {
        mailer.sendMail(subject, receivers, textContent, null);
    }

    @Override
    public void sendSimpleMail(String subject, String textContent, List<String> recievers) throws MailerException {
        mailer.sendMail(subject, recievers.toArray(new String[0]), textContent, null);
    }

    @Override
    public void sendSimpleMail(String subject, String textContent, Attachement[] attachments, String... receivers) throws MailerException {
        log.warn("CALL IGNORED.  THIS CLASS IS A TEMPORARY ADAPTER UNTIL WE CAN GET RID OF DANS-LANG DEPENDENCY'S");
    }

    @Override
    public void sendMail(String subject, String textContent, String htmlContent, String... receivers) throws MailerException {
        log.warn("CALL IGNORED.  THIS CLASS IS A TEMPORARY ADAPTER UNTIL WE CAN GET RID OF DANS-LANG DEPENDENCY'S");
    }

    @Override
    public void sendMail(String subject, String textContent, String htmlContent, Attachement[] attachments, String... receivers) throws MailerException {
        log.warn("CALL IGNORED.  THIS CLASS IS A TEMPORARY ADAPTER UNTIL WE CAN GET RID OF DANS-LANG DEPENDENCY'S");
    }

}
