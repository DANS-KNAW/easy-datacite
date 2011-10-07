package nl.knaw.dans.easy.data.ext;

import nl.knaw.dans.common.lang.mail.AdminMailer;
import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.commons.pid.PidGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalServices
{

    private static final Logger logger = LoggerFactory.getLogger(ExternalServices.class);

    private static Mailer       MAIL_OFFICE;
    private static AdminMailer  ADMIN_MAILER;
    private static PidGenerator METADATA_PID_GENERATOR;

    private static boolean      LOCKED;
    private static final String ILLEGAL_METHOD_CALL = "Illegal method call: Constructor and setter methods in the "
                                                            + ExternalServices.class.getName()
                                                            + " class should not be called.";

    public static Mailer getMailOffice()
    {
        return MAIL_OFFICE;
    }
    
    public static AdminMailer getAdminMailer()
    {
        if (ADMIN_MAILER == null)
        {
            ADMIN_MAILER = new DefaultAdminMailer();
        }
        return ADMIN_MAILER;
    }

    public static PidGenerator getPidGenerator()
    {
        return METADATA_PID_GENERATOR;
    }

    public ExternalServices()
    {
        checkLock(); // CGLIB will call the constructor a second time.
        logger.debug("Created " + this);
    }

    public void lock()
    {
        LOCKED = true;
        logger.info(this + " has been locked.");
    }

    void unlock()
    {
        LOCKED = false;
        logger.debug(this + " has been unlocked.");
    }

    private void checkLock()
    {
        if (LOCKED)
        {
            logger.debug(ILLEGAL_METHOD_CALL);
            throw new IllegalStateException(ILLEGAL_METHOD_CALL);
        }
    }

    public void setMailOffice(Mailer mailer)
    {
        checkLock();
        MAIL_OFFICE = mailer;
        logger.debug("Injected dependency mailer: " + mailer);
    }
    
    public void setAdminMailer(AdminMailer adminMailer)
    {
        checkLock();
        ADMIN_MAILER = adminMailer;
        logger.debug("Injected dependency adminMailer: " + adminMailer);
        boolean send = ADMIN_MAILER.sendApplicationStarting();
        logger.info(send ? "Sending admin mail on startup" : "Not sending admin mail on startup");
    }

    public void setMetadataPidGenerator(PidGenerator generator)
    {
        checkLock();
        METADATA_PID_GENERATOR = generator;
        logger.debug("Injected dependency persistent identifier generator: " + generator);
    }
    
    public void close()
    {
        logger.info("Closing " + this.getClass().getSimpleName());
        if (ADMIN_MAILER != null)
        {
            boolean send = ADMIN_MAILER.sendApplicationClosing();
            logger.info(send ? "Sending admin mail on close" : "Not sending admin mail on close");
        }
    }

}
