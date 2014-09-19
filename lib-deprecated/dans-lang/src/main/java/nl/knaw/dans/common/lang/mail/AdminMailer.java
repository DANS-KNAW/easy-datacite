package nl.knaw.dans.common.lang.mail;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.mail.Mailer.MailerException;
import nl.knaw.dans.common.lang.util.NetUtil;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends mail messages to registered administrators.
 */
public class AdminMailer {
    /**
     * The default time in minutes to wait between sending exception mails.
     */
    public static final int DEFAULT_EXEPTION_MAIL_INTERVAL = 10;

    private static final Logger logger = LoggerFactory.getLogger(AdminMailer.class);

    private final Mailer mailOffice;
    private final String applicationName;
    private List<String> adminMailAddresses = new ArrayList<String>();

    private boolean sendOnStarting;
    private boolean sendOnClosing;
    private int exeptionMailInterval = DEFAULT_EXEPTION_MAIL_INTERVAL;
    private DateTime lastExceptionMailSend;

    public AdminMailer(Mailer mailOffice, String applicationName) {
        this.mailOffice = mailOffice;
        this.applicationName = applicationName;
    }

    public List<String> getAdminMailAddresses() {
        return adminMailAddresses;
    }

    public void setAdminMailAddresses(List<String> adminMailAddresses) {
        this.adminMailAddresses = adminMailAddresses;
        logger.info("Admin mail addresses: " + adminMailAddresses);
    }

    /**
     * Set admin mail addresses as a comma separated string.
     * 
     * @param adminAddresses
     *        a comma separated list of email addresses
     */
    public void setAdminMailAddressesCS(String adminAddresses) {
        if (StringUtils.isNotBlank(adminAddresses)) {
            String[] addresses = adminAddresses.split(",");
            setAdminMailAddresses(Arrays.asList(addresses));
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setSendOnStarting(boolean sendOnStartup) {
        this.sendOnStarting = sendOnStartup;
    }

    public void setSendOnClosing(boolean sendOnClose) {
        this.sendOnClosing = sendOnClose;
    }

    public int getExeptionMailInterval() {
        return exeptionMailInterval;
    }

    public void setExeptionMailInterval(int exeptionMailInterval) {
        this.exeptionMailInterval = exeptionMailInterval;
    }

    public boolean sendEmergencyMail(Throwable cause) {
        return sendEmergencyMail(null, cause);
    }

    public boolean sendEmergencyMail(String msg, Throwable cause) {
        return sendMail("Severe exception in", msg, cause);
    }

    public boolean sendExceptionMail(Throwable cause) {
        return sendExceptionMail(null, cause);
    }

    public boolean sendExceptionMail(String msg, Throwable cause) {
        boolean send = false;
        DateTime pollTime = new DateTime().minusMinutes(exeptionMailInterval);
        if (lastExceptionMailSend == null || pollTime.isAfter(lastExceptionMailSend)) {
            send = sendMail("Exception in", msg, cause);
            if (send)
                lastExceptionMailSend = new DateTime();
        } else {
            logger.info("Not sending mail because last exception mail was send less then " + exeptionMailInterval + " minutes ago.");
        }
        return send;
    }

    public boolean sendApplicationStarting() {
        if (sendOnStarting) {
            return sendMail("Starting", null, null);
        } else {
            return false;
        }
    }

    public boolean sendApplicationClosing() {
        if (sendOnClosing) {
            return sendMail("Closing", null, null);
        } else {
            return false;
        }
    }

    public boolean sendInfoMail(String message) {
        return sendMail("Info message from", message, null);
    }

    private boolean sendMail(String prefix, String message, Throwable cause) {
        String subject = prefix + " application " + applicationName + " on " + NetUtil.getCanonicalHostName();
        String date = new DateTime().toString("yyyy-MM-dd HH:mm:ss.SSSZZ");
        String stacktrace = null;
        if (cause != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            cause.printStackTrace(writer);
            stacktrace = out.toString();
        }
        StringBuilder sb = new StringBuilder() //
                .append(subject) //
                .append("\n\nDate: ") //
                .append(date);
        if (message != null) {
            sb.append("\nMessage: ") //
                    .append(message);
        }
        if (stacktrace != null) {
            sb.append("\n\nStacktrace:\n") //
                    .append(stacktrace);
        }

        return send("[AdminMailer " + applicationName + "] " + subject, sb.toString());
    }

    protected boolean send(String subject, String text) {
        boolean success = false;
        if (adminMailAddresses.size() > 0) {
            try {
                mailOffice.sendSimpleMail(subject, text, adminMailAddresses);
                success = true;
                logger.debug("Admin email send.");
            }
            catch (MailerException e) {
                logger.error("Could not send mail: ", e);
            }
        } else {
            logger.warn("No admin email send because the list of admin email addreses is empty.");
        }
        return success;
    }

}
