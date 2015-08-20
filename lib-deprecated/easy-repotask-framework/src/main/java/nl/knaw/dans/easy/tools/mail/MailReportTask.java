package nl.knaw.dans.easy.tools.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.FileDataSource;

import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.mail.EasyMailer;
import nl.knaw.dans.easy.mail.EasyMailerAttachment;
import nl.knaw.dans.easy.mail.EasyMailerAttachmentImpl;
import nl.knaw.dans.easy.mail.EasyMailerException;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailReportTask extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(MailReportTask.class);
    private String[] recipients;
    private String reportTitle;
    private String textContent;
    private String htmlContent;
    private List<String> excludedReportPrefixes;
    private EasyMailer mailer;

    @Override
    public void run(final JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        log.info("Trying to send report to {}", Arrays.toString(recipients));
        final File reportDir = RL.getReportLocation();
        log.debug("Report directory is {}", reportDir.getAbsoluteFile());
        final ArrayList<EasyMailerAttachment> attachments = new ArrayList<EasyMailerAttachment>();
        for (final File file : reportDir.listFiles()) {
            if (fileNameMatchesExcludedReportPrefix(file)) {
                continue;
            }
            attachments.add(new EasyMailerAttachmentImpl(new FileDataSource(file), file.getName(), ""));
            log.debug("Attached file {} ...", file.getAbsolutePath());
        }
        sendReportMail(attachments.toArray(new EasyMailerAttachment[attachments.size()]));
        log.info("Report sent successfully.");
    }

    private boolean fileNameMatchesExcludedReportPrefix(File file) {
        if (excludedReportPrefixes == null)
            return false;
        for (String prefix : excludedReportPrefixes) {
            if (file.getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void sendReportMail(final EasyMailerAttachment[] attachments) throws TaskException {
        try {
            mailer.sendMail(String.format("New report available: %s", reportTitle), recipients, textContent, htmlContent, attachments);
        }
        catch (final EasyMailerException e) {
            log.error("Could not send report through mail: {}", e.getMessage(), e);
            throw new TaskException(this);
        }
    }

    public void setRecipients(final String recipients) {
        this.recipients = recipients.split("[,\\s]");
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(final String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(final String textContent) {
        this.textContent = textContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(final String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public List<String> getExcludedReportPrefixes() {
        return excludedReportPrefixes;
    }

    public void setExcludedReportPrefixes(List<String> excludedReportPrefixes) {
        this.excludedReportPrefixes = excludedReportPrefixes;
    }

    public EasyMailer getMailer() {
        return mailer;
    }

    public void setMailer(EasyMailer mailer) {
        this.mailer = mailer;
    }

}
