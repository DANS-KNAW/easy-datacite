package nl.knaw.dans.easy.servicelayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import nl.knaw.dans.common.lang.mail.Attachement;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.common.lang.mail.Mailer.MailerException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.ext.EasyMailComposer;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.util.EasyHome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNotification
{

    static final Logger logger = LoggerFactory.getLogger(AbstractNotification.class);

    public static final String TEMPLATE_BASE_LOCATION = EasyHome.getValue() + "/mail/templates/";
    public static final String SUBJECT_PROPERTIES = TEMPLATE_BASE_LOCATION + "subject.properties";
    private static final String SUBJECT_ERROR = "could not load notification subjects";
    private static final Properties SUBJECT_TEMPLATES = loadSubjectTemplates();

    private final List<Attachement> attachements = new ArrayList<Attachement>();

    private final EasyMailComposer composer;

    private final EasyUser receiver;

    /** only for testing purposes */
    final Object[] placeholderSuppliers;

    /**
     * @return Represents the following templates:
     *         <ul>
     *         <li>a file: {@link #TEMPLATE_BASE_LOCATION}+getTemplateLocation()+".txt"</li>
     *         <li>a file: {@link #TEMPLATE_BASE_LOCATION}+getTemplateLocation()+".html"</li>
     *         <li>an entry: {@link #getTemplateLocation()} (slashes replaced by dots)<br>
     *         in the properties file {@link #SUBJECT_PROPERTIES}</li>
     *         </ul>
     *         The templates can contain place holders like ~PlaceHolderSupplier.getXx~ (see constructor) or ~Notification.getYy~ (see subclasses).
     */
    abstract String getTemplateLocation();

    /**
     * @param placeHolderSuppliers
     *        The types should be unique.
     */
    public AbstractNotification(final EasyUser receiver, final Object... placeHolderSuppliers)
    {
        this.receiver = receiver;
        this.placeholderSuppliers = concat(receiver, this, placeHolderSuppliers);
        this.composer = new EasyMailComposer(placeholderSuppliers);
    }

    /**
     * @return the last argument concatenated with the others at its front
     */
    static Object[] concat(final Object... source)
    {
        final Object[] last = (Object[]) source[source.length - 1];
        final Object[] destination = new Object[source.length - 1 + last.length];
        System.arraycopy(source, 0, destination, 0, source.length - 1);
        System.arraycopy(last, 0, destination, source.length - 1, last.length);
        return destination;
    }

    private static Properties loadSubjectTemplates()
    {
        try
        {
            final Properties properties = new Properties();
            final InputStream resourceAsStream = new FileInputStream(SUBJECT_PROPERTIES);
            properties.load(resourceAsStream);
            return properties;
        }
        catch (final IOException e)
        {
            logger.error(SUBJECT_ERROR, e);
        }
        return null;
    }

    List<Attachement> getAttachements()
    {
        return attachements;
    }

    private Attachement[] getAttachementsAsArray()
    {
        if (attachements.size() == 0)
            return null;
        final Attachement[] attachementArray = {};
        return attachements.toArray(attachementArray);
    }

    final public void send() throws ServiceException
    {
        final Mailer mailOffice = ExternalServices.getMailOffice();
        if (mailOffice == null)
            throw new ServiceException(new NullPointerException("no mail office available"));
        try
        {
            mailOffice.sendMail(getSubject(), getText(), getHtml(), getAttachementsAsArray(), getReceiverEmail());
        }
        catch (final MailerException e)
        {
            throw new ServiceException(e);
        }
    }

    final public boolean sendMail()
    {
        try
        {
            send();
            return true;
        }
        catch (final ServiceException e)
        {
            final String format = "could not send [%s] to [%s]";
            final String message = String.format(format, getSubjectTemplate(), getReceiverEmail());
            logger.error(message, e);
        }
        return false;
    }

    private final String getSubjectTemplate()
    {
        return SUBJECT_TEMPLATES.getProperty(getPropertiesEntry());
    }

    private String getPropertiesEntry()
    {
        return getTemplateLocation().replaceAll("/", ".");
    }

    final String getSubject() throws ServiceException
    {
        // this method is not private for testing purposes
        final byte[] template = getSubjectTemplate().getBytes();
        final InputStream inputStream = new ByteArrayInputStream(template);
        try
        {
            return composer.compose(inputStream, false);
        }
        catch (final MailComposerException e)
        {
            throw wrapSubjectException(e);
        }
    }

    String getReceiverEmail()
    {
        return receiver.getEmail();
    }

    final String getHtml() throws ServiceException
    {
        // this method is not private for testing purposes
        final String templateLocation = TEMPLATE_BASE_LOCATION + getTemplateLocation() + ".html";
        try
        {
            return composer.composeHtml(templateLocation);
        }
        catch (final MailComposerException e)
        {
            throw wrapBodyException(templateLocation, e);
        }
    }

    final String getText() throws ServiceException
    {
        // this method is not private for testing purposes
        final String templateLocation = TEMPLATE_BASE_LOCATION + getTemplateLocation() + ".txt";
        try
        {
            return composer.composeText(templateLocation);
        }
        catch (final MailComposerException e)
        {
            throw wrapBodyException(templateLocation, e);
        }
    }

    private ServiceException wrapSubjectException(final MailComposerException e) throws ServiceException
    {
        final File file = new File(SUBJECT_PROPERTIES);
        return new ServiceException(e.getMessage() + "\ntemplate: " + file.getAbsolutePath() + "/" + file.getName() + "(" + getPropertiesEntry() + ")", e);
    }

    private ServiceException wrapBodyException(final String templateLocation, final MailComposerException e)
    {
        final File file = new File(templateLocation);
        return new ServiceException(e.getMessage() + "\ntemplate: " + file.getAbsolutePath() + "/" + file.getName(), e);
    }
}
