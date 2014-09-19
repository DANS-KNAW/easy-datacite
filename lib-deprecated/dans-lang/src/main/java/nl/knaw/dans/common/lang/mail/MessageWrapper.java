package nl.knaw.dans.common.lang.mail;

import java.io.File;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * A utility class for the {@link CommonMailer}.
 * 
 * @author joke
 */
class MessageWrapper {
    private MessageWrapper() {
        // all methods static: no instantiation
    }

    static BodyPart wrapTextBodyPart(final String textContent) throws MessagingException {
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(textContent);
        return bodyPart;
    }

    static BodyPart wrapHtmlBodyPart(final String htmlContent) throws MessagingException {
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(htmlContent, "text/html");
        return bodyPart;
    }

    static BodyPart wrapBodyPart(final String contentId, final File file) throws MessagingException {
        final DataSource dataSource = new FileDataSource(file);
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDataHandler(new DataHandler(dataSource));
        bodyPart.setHeader("Content-ID", contentId);
        bodyPart.setFileName(file.getName());
        return bodyPart;
    }

    static BodyPart wrapAttachementPart(Attachement attachement) throws MessagingException {
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDataHandler(new DataHandler(attachement.dataSource));
        bodyPart.setFileName(attachement.fileName);
        return bodyPart;
    }

    static BodyPart wrapBodyPart(final Multipart multiParts) throws MessagingException {
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(multiParts);
        return bodyPart;
    }

    static Multipart wrapAlternativeParts(final BodyPart... bodyParts) throws MessagingException {
        final Multipart multiPart = new MimeMultipart("alternative");
        for (final BodyPart bodyPart : bodyParts) {
            multiPart.addBodyPart(bodyPart);
        }
        return multiPart;
    }

    /**
     * Wraps the HTML content and images in a MultiPart as far as the images are mentioned in the HTML content.
     * 
     * @param htmlContent
     *        will be wrapped in a BodyPart before it is added to the MultiPart
     * @param images
     *        for each key mentioned as "<code>cid:<i>key</i></code>" in htmlContent, the value is added to the MultiPart
     * @return a MultiPart with HTML and zero or more images
     * @throws MessagingException
     */
    static Multipart wrapRelatedParts(final String htmlContent, final Map<String, BodyPart> images) throws MessagingException {
        final Multipart multiPart = new MimeMultipart("related");
        multiPart.addBodyPart(wrapHtmlBodyPart(htmlContent));
        for (final String key : images.keySet()) {
            if (htmlContent.contains("cid:" + key))
                multiPart.addBodyPart(images.get(key));
        }
        return multiPart;
    }
}
