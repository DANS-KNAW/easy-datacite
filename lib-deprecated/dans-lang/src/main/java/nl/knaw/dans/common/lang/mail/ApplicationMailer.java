package nl.knaw.dans.common.lang.mail;

import static nl.knaw.dans.common.lang.mail.MessageWrapper.wrapBodyPart;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationMailer extends CommonMailer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMailer.class);

    public ApplicationMailer(MailerConfiguration configuration) throws MailerException {
        super(configuration);
    }

    protected Map<String, BodyPart> wrapImages(final Map<String, String> images) throws Mailer.MailerException {
        final Map<String, BodyPart> wrappedImages = new HashMap<String, BodyPart>();
        for (final String key : images.keySet()) {
            final String location = images.get(key);

            try {
                final File file = ResourceLocator.getFile(location);
                wrappedImages.put(key, wrapBodyPart(key, file));
            }
            catch (final MessagingException exception) {
                final String logLine = String.format("Could not wrap configured image. Key=%s; Location=%s", key, location);
                logger.error(logLine, exception);
                throw new ApplicationMailer.MailerException(logLine, exception);
            }
            catch (ResourceNotFoundException e) {
                throw new ApplicationMailer.MailerException(e.getMessage(), e);
            }
        }
        return wrappedImages;
    }

}
