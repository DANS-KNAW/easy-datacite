package nl.knaw.dans.easy.data.ext;

import nl.knaw.dans.common.lang.mail.AdminMailer;
import nl.knaw.dans.commons.pid.PidGenerator;
import nl.knaw.dans.easy.mail.EasyMailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalServices {

    private static final Logger logger = LoggerFactory.getLogger(ExternalServices.class);

    private static EasyMailer MAIL_OFFICE;
    private static AdminMailer ADMIN_MAILER;
    private static PidGenerator METADATA_PID_GENERATOR;

    public static EasyMailer getMailOffice() {
        return MAIL_OFFICE;
    }

    public static AdminMailer getAdminMailer() {
        if (ADMIN_MAILER == null) {
            ADMIN_MAILER = new DefaultAdminMailer();
        }
        return ADMIN_MAILER;
    }

    public static PidGenerator getPidGenerator() {
        return METADATA_PID_GENERATOR;
    }

    public ExternalServices() {
        logger.debug("Created " + this);
    }

    public void setMailOffice(EasyMailer mailer) {
        MAIL_OFFICE = mailer;
        logger.debug("Injected dependency mailer: " + mailer);
    }

    public void setAdminMailer(AdminMailer adminMailer) {
        ADMIN_MAILER = adminMailer;
        logger.debug("Injected dependency adminMailer: " + adminMailer);
        boolean send = ADMIN_MAILER.sendApplicationStarting();
        logger.info(send ? "Sending admin mail on startup" : "Not sending admin mail on startup");
    }

    public void setMetadataPidGenerator(PidGenerator generator) {
        METADATA_PID_GENERATOR = generator;
        logger.debug("Injected dependency persistent identifier generator: " + generator);
    }

    public void close() {
        logger.info("Closing " + this.getClass().getSimpleName());
        if (ADMIN_MAILER != null) {
            boolean send = ADMIN_MAILER.sendApplicationClosing();
            logger.info(send ? "Sending admin mail on close" : "Not sending admin mail on close");
        }
    }

}
