package nl.knaw.dans.easy.web.mail;

import java.io.File;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

public class EasyMailerImpl implements EasyMailer
{
    private String[] fixedBccs;
    private String smtpHost;
    private String smtpPassword;
    private String smtpUserName;
    private String from;

    @Override
    public void sendMail(String subject, String[] recipients, String text, String html, File... attachments)
    {
        /*
         * We always send an HTML-email, which is a multi-part email. We can always leave the HTML or
         * text message empty if we wish.
         */
        sendHtmlMail(subject, recipients, text, html, attachments);
    }

    private void sendHtmlMail(String subject, String[] to, String text, String html, File... attachements)
    {
        HtmlEmail mail = new HtmlEmail();
        addSmtpHost(mail);
        addFrom(mail);
        addTo(mail, to);
        maybeAddSubject(mail, subject);
        maybeAddCredentials(mail);
        maybeAddHtmlMsg(mail, html);
        maybeAddTextMsg(mail, text);
        maybeAddFixedBccs(mail);
        maybeAddAttachments(mail, attachements);
        send(mail);
    }

    private void addSmtpHost(Email mail)
    {
        checkSet("smtpHost", smtpHost);
        mail.setHostName(smtpHost);
    }

    private void checkSet(String fieldName, Object field)
    {
        if (field == null || "".equals(field))
        {
            error(fieldName + " is required", null);
        }
    }

    private void error(String msg, Throwable cause)
    {
        throw new EasyMailerException("Cannot send E-mail: " + msg, cause);
    }

    private void addFrom(Email mail)
    {
        checkSet("from", from);
        try
        {
            mail.setFrom(from);
        }
        catch (EmailException e)
        {
            error("Problem adding from-field: " + e.getMessage(), e);
        }
    }

    private void addTo(Email mail, String[] recipients)
    {
        checkSet("to", recipients);
        for (String r : recipients)
        {
            try
            {
                mail.addTo(r);
            }
            catch (EmailException e)
            {
                error("Problem adding recipient: " + r, e);
            }
        }
    }

    private void maybeAddSubject(Email mail, String subject)
    {
        if (subject != null)
        {
            mail.setSubject(subject);
        }
    }

    private void maybeAddCredentials(HtmlEmail mail)
    {
        if (smtpUserName != null && smtpPassword != null)
        {
            mail.setAuthentication(smtpUserName, smtpPassword);
        }
    }

    private void maybeAddHtmlMsg(HtmlEmail mail, String html)
    {
        if (html != null)
        {
            try
            {
                mail.setHtmlMsg(html);
            }
            catch (EmailException e)
            {
                error("Problem adding HTML message: " + e.getMessage() + ". HTML = '" + html + "''", e);
            }
        }
    }

    private void maybeAddTextMsg(HtmlEmail mail, String text)
    {
        if (text != null)
        {
            try
            {
                mail.setTextMsg(text);
            }
            catch (EmailException e)
            {
                error("Problem adding text message: " + e.getMessage() + ". Text = '" + text + "", e);
            }
        }
    }

    private void maybeAddFixedBccs(Email mail)
    {
        if (fixedBccs != null)
        {
            for (String bcc : fixedBccs)
            {
                try
                {
                    mail.addBcc(bcc);
                }
                catch (EmailException e)
                {
                    error(e.getMessage(), e);
                }
            }
        }
    }

    private void maybeAddAttachments(MultiPartEmail mail, File... attachments)
    {
        if (attachments != null)
        {
            for (File f : attachments)
            {
                EmailAttachment a = new EmailAttachment();
                a.setPath(f.getAbsolutePath());
                a.setName(f.getName());
                try
                {
                    mail.attach(a);
                }
                catch (EmailException e)
                {
                    error("Problem attaching file to E-mail: " + f, e);
                }
            }
        }
    }

    private void send(Email mail)
    {
        try
        {
            mail.send();
        }
        catch (EmailException e)
        {
            error(e.getMessage(), e);
        }
    }

    public void setFixedBccs(String[] fixedBccs)
    {
        this.fixedBccs = fixedBccs;
    }

    public void setSmtpHost(String smtpHost)
    {
        this.smtpHost = smtpHost;
    }

    public void setSmtpPassword(String smtpPassword)
    {
        this.smtpPassword = smtpPassword;
    }

    public void setSmtpUserName(String smtpUserName)
    {
        this.smtpUserName = smtpUserName;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }
}
