package nl.knaw.dans.easy.data.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.mail.AdminMailer;

public class DefaultAdminMailer extends AdminMailer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAdminMailer.class);

    public DefaultAdminMailer() {
        super(null, "Easy");
    }

    @Override
    protected boolean send(String subject, String text) {
        logger.warn("Not sending mail. subject=" + subject);
        return false;
    }

}
