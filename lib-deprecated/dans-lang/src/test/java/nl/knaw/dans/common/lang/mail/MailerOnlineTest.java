/**
 *
 */
package nl.knaw.dans.common.lang.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the classes {@link CommonMailer} and {@link DansMailerConfiguration}.
 *
 * @author Joke Pol
 */
public class MailerOnlineTest
{
    private static boolean skipSend = true;

    /** The one that receives the test messages if skipSend is false */
    private static final String GUINEAPIG = "joke.pol@dans.knaw.nl";

    private static final String PLAIN_TEXT_CONTENT = "test mail content\n- aap\n- noot\n- mies";
    private static final String HTML_CONTENT = "<ul><li>aap</li><li>noot</li><li>mies</li></ul>";
    private static final String HTML_WITH_LOGO = "<img src='cid:logo'>" + HTML_CONTENT;
    private static final String INVALID_RECIPIENT = "somebody@@dans.knaw.nl";
    private static final String UNKNOWN_RECIPIENT = "nobody@dans.knaw.nl";
    private static final Attachement[] ATTACHMENTS = {new Attachement("someFile.txt", new File("src/test/resources/log4j.xml"))};

    /** Images supposed to be included in messages sent by a mailer instance */
    private static final String LOGO = MailerConfiguration.IMAGE_KEY_PREFIX + "logo=src/test/resources/test-files/mail/easy_logo.gif\n";

    private Mailer getDefaultMailer() throws Exception
    {
        CommonMailer mailer = (CommonMailer) DansMailer.getDefaultInstance();
        mailer.skipSend = skipSend;
        return mailer;
    }

    private Mailer getCustomMailer() throws Exception
    {
        CommonMailer mailer = new DansMailer(DansMailerConfiguration.createCustomized(LOGO));
        mailer.skipSend = skipSend;
        return mailer;
    }

    @Ignore("Environment dependent test")
    @Test()
    public void sendSimple() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        final String subject = "Simple test mail from unittest";
        mailer.sendSimpleMail(subject, PLAIN_TEXT_CONTENT, GUINEAPIG);
    }

    @Ignore("Environment dependent test")
    @Test()
    public void sendSimpleHtml() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        final String subject = "Simple html test mail from unittest";
        mailer.sendMail(subject, PLAIN_TEXT_CONTENT, HTML_CONTENT, ATTACHMENTS, GUINEAPIG);
    }

    @Ignore("Environment dependent test")
    @Test()
    public void sendHtmlWithImage() throws Exception
    {
        final Mailer mailer = getCustomMailer();
        final String subject = "Html with logo test mail from unittest";
        mailer.sendMail(subject, PLAIN_TEXT_CONTENT, HTML_WITH_LOGO, ATTACHMENTS, GUINEAPIG);
    }

    @Ignore("Environment dependent test")
    @Test()
    public void sendMissingLogo() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        final String subject = "Html with broken logo test mail from unittest";
        mailer.sendMail(subject, PLAIN_TEXT_CONTENT, HTML_WITH_LOGO, ATTACHMENTS, GUINEAPIG);
    }

    @Test(expected = CommonMailer.MailerException.class)
    public void sendToInvalid() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        final String subject = "Test mail from unittest";
        final String receipient = INVALID_RECIPIENT;
        mailer.sendSimpleMail(subject, PLAIN_TEXT_CONTENT, receipient);
    }

    @Ignore("Environment dependent test")
    @Test()
    public void sendToUnknown() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        final String subject = "Test mail from unittest";
        final String receipient = UNKNOWN_RECIPIENT;
        mailer.sendSimpleMail(subject, PLAIN_TEXT_CONTENT, receipient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noReceipient() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        mailer.sendSimpleMail("Test mail from unittest", PLAIN_TEXT_CONTENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noReceipient2() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        mailer.sendSimpleMail("Test mail from unittest", PLAIN_TEXT_CONTENT, (String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noReceipient3() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        mailer.sendSimpleMail("Test mail from unittest", PLAIN_TEXT_CONTENT, new String[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void noContent() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        mailer.sendSimpleMail("Test mail from unittest", null, INVALID_RECIPIENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSubject() throws Exception
    {
        final Mailer mailer = getDefaultMailer();
        mailer.sendSimpleMail(null, PLAIN_TEXT_CONTENT, INVALID_RECIPIENT);
    }

    @Test()
    public void invalidConfiguration() throws Exception
    {
        DansMailerConfiguration.createCustomized("\\u11000001");
    }

    @Test(expected = IOException.class)
    public void noConfigurationFile() throws Exception, IOException
    {
        new DansMailerConfiguration(new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException();
            }
        });
    }
}
