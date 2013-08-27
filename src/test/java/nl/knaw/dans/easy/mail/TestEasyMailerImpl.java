package nl.knaw.dans.easy.mail;

import javax.activation.DataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EasyMailerImpl.class)
public class TestEasyMailerImpl
{
    private EasyMailerImpl mailer;
    private HtmlEmail htmlEmailMock;

    private Capture<String> hostName = new Capture<String>();
    private Capture<String> from = new Capture<String>();
    private Capture<String> to = new Capture<String>(CaptureType.ALL);
    private Capture<String> subject = new Capture<String>();
    private Capture<String> user = new Capture<String>();
    private Capture<String> password = new Capture<String>();
    private Capture<String> htmlMsg = new Capture<String>();
    private Capture<String> textMsg = new Capture<String>();
    private Capture<String> bcc = new Capture<String>(CaptureType.ALL);
    private Capture<DataSource> attachmentDatasSources = new Capture<DataSource>(CaptureType.ALL);
    private Capture<String> attachmentNames = new Capture<String>(CaptureType.ALL);
    private Capture<String> attachmentDescriptions = new Capture<String>(CaptureType.ALL);

    @Before
    public void setUp()
    {
        mailer = new EasyMailerImpl();
        htmlEmailMock = PowerMock.createMock(HtmlEmail.class);
    }

    @Test(expected = EasyMailerException.class)
    public void exceptionWhenNoSmtpHostSpecified()
    {
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail("No host", recipients, "text", "html");
    }

    @Test
    public void hostNameCorrectlyPassed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();

        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail("Host name correct", recipients, "text", "html");
        assertEquals("Host not correctly passed to HtmlEmail", "myhost.nl", hostName.getValue());
    }

    private void expectConfigureAndSendHtmlEmail() throws Exception, EmailException
    {
        expectNew(HtmlEmail.class).andReturn(htmlEmailMock);
        htmlEmailMock.setHostName(capture(hostName));
        expect(htmlEmailMock.setFrom(capture(from))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.addTo(capture(to))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.setSubject(capture(subject))).andStubReturn(htmlEmailMock);
        htmlEmailMock.setAuthentication(capture(user), capture(password));
        expect(htmlEmailMock.setHtmlMsg(capture(htmlMsg))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.setTextMsg(capture(textMsg))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.addBcc(capture(bcc))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.attach(capture(attachmentDatasSources), capture(attachmentNames), capture(attachmentDescriptions))).andStubReturn(htmlEmailMock);
        expect(htmlEmailMock.send()).andStubReturn("");
    }

    @Test(expected = EasyMailerException.class)
    public void exceptionWhenNoFromAddressSpecified()
    {
        String[] recipients = {"piet@puk.com"};
        mailer.setSmtpHost("myhost.com");
        mailer.sendMail("No host", recipients, "text", "html");
    }

    @Test
    public void fromCorrectlyPassed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();

        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail("Host name correct", recipients, "text", "html");
        assertEquals("From not correctly passed to HtmlEmail", "me@org.com", from.getValue());
    }

    @Test(expected = EasyMailerException.class)
    public void exceptionWhenRecipientsNull()
    {
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        mailer.sendMail("No host", null, "text", "html");
    }

    @Test(expected = EasyMailerException.class)
    public void exceptionWhenRecipientsEmpty()
    {
        String[] recipients = {};
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        mailer.sendMail("No host", recipients, "text", "html");
    }

    @Test
    public void oneRecipientCorrectlyPassed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();

        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail("Host name correct", recipients, "text", "html");
        assertEquals("Not the number of recipients expected", 1, to.getValues().size());
        assertEquals("One recipient not correctly passed to HtmlEmail", "piet@puk.com", to.getValue());
    }

    @Test
    public void twoRecipientsCorrectlyPassed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();

        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com", "repel@steeltje.com"};
        mailer.sendMail("Host name correct", recipients, "text", "html");
        assertEquals("Not the number of recipients expected", 2, to.getValues().size());
        assertEquals("First recipient or two not correctly passed to HtmlEmail", "piet@puk.com", to.getValues().get(0));
        assertEquals("First recipient or two not correctly passed to HtmlEmail", "repel@steeltje.com", to.getValues().get(1));
    }

    @Test
    public void noSubjectPassedIfAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Subject found while none was passed", 0, subject.getValues().size());
    }

    @Test
    public void subjectCorrectlyPassed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail("Test subject", recipients, "text", "html");
        assertEquals("Subject found while none was passed", "Test subject", subject.getValue());
    }

    @Test
    public void noCredentialsPassedIfUserAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setSmtpPassword("secret");
        mailer.setFrom("me@org.com");

        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Password passed to HtmlEmail although not all credentials were specified", 0, password.getValues().size());
        assertEquals("Username passed to HtmlEmail although not all credentials were specified", 0, user.getValues().size());
    }

    @Test
    public void noCredentialsPassedIfPasswordAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setSmtpUserName("user");
        mailer.setFrom("me@org.com");

        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Password passed to HtmlEmail although not all credentials were specified", 0, password.getValues().size());
        assertEquals("Username passed to HtmlEmail although not all credentials were specified", 0, user.getValues().size());
    }

    @Test
    public void credentialsPassedIfBotherUserAndPasswordSet() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setSmtpUserName("user");
        mailer.setSmtpPassword("secret");
        mailer.setFrom("me@org.com");

        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Password not correctly passed", "secret", password.getValue());
        assertEquals("Username not correctly passed", "user", user.getValue());
    }

    @Test
    public void noHtmlMsgPassedIfAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", null);
        assertEquals("HTML message present although not specified", 0, htmlMsg.getValues().size());
    }

    @Test
    public void htmlPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("No HTML message passed although it was specified", "html", htmlMsg.getValue());
    }

    @Test
    public void noTextMsgPassedIfAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, null, "html");
        assertEquals("Text message present although not specified", 0, textMsg.getValues().size());
    }

    @Test
    public void textPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("No text message passed although it was specified", "text", textMsg.getValue());
    }

    @Test
    public void noBccPassedIfAbsent() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, null, "html");
        assertEquals("Bcc set although it was not specified", 0, bcc.getValues().size());
    }

    @Test
    public void oneBccPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        mailer.setBccs("secret.receiver@mail.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Not the expected number of bccs", 1, bcc.getValues().size());
        assertEquals("BCC not correctly passed down", "secret.receiver@mail.com", bcc.getValue());
    }

    @Test
    public void multipleBccsPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        mailer.setBccs("secret.receiver@mail.com,secret.receiver2@mail.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Not the expected number of bccs", 2, bcc.getValues().size());
        assertEquals("BCC not correctly passed down", "secret.receiver@mail.com", bcc.getValues().get(0));
        assertEquals("BCC not correctly passed down", "secret.receiver2@mail.com", bcc.getValues().get(1));
    }

    @Test
    public void bccsTrimmed() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        mailer.setBccs("  secret.receiver@mail.com ,  secret.receiver2@mail.com ");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html");
        assertEquals("Not the expected number of bccs", 2, bcc.getValues().size());
        assertEquals("BCC not correctly passed down", "secret.receiver@mail.com", bcc.getValues().get(0));
        assertEquals("BCC not correctly passed down", "secret.receiver2@mail.com", bcc.getValues().get(1));
    }

    @Test
    public void oneAttachmentPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        final DataSource ds = PowerMock.createMock(DataSource.class);

        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html", new EasyMailerAttachmentImpl(ds, "attachment name", "attachment description"));
        assertEquals("Attachment dataSource not passed to HtmlEmail", ds, attachmentDatasSources.getValue());
        assertEquals("Attachment name not passed to HtmlEmail", "attachment name", attachmentNames.getValue());
        assertEquals("Attachment description not passed to HtmlEmail", "attachment description", attachmentDescriptions.getValue());
    }
    
    @Test
    public void multipleAttachmentPassedIfSpecified() throws Exception
    {
        expectConfigureAndSendHtmlEmail();
        final DataSource ds1 = PowerMock.createMock(DataSource.class);
        final DataSource ds2 = PowerMock.createMock(DataSource.class);

        PowerMock.replayAll();
        mailer.setSmtpHost("myhost.nl");
        mailer.setFrom("me@org.com");
        String[] recipients = {"piet@puk.com"};
        mailer.sendMail(null, recipients, "text", "html", new EasyMailerAttachmentImpl(ds1, "attachment name 1", "attachment description 1"), 
                new EasyMailerAttachmentImpl(ds2, "attachment name 2", "attachment description 2"));
        assertEquals("Attachment 1 dataSource not passed to HtmlEmail", ds1, attachmentDatasSources.getValues().get(0));
        assertEquals("Attachment 1 name not passed to HtmlEmail", "attachment name 1", attachmentNames.getValues().get(0));
        assertEquals("Attachment 1 description not passed to HtmlEmail", "attachment description 1", attachmentDescriptions.getValues().get(0));
        
        assertEquals("Attachment 1 dataSource not passed to HtmlEmail", ds2, attachmentDatasSources.getValues().get(1));
        assertEquals("Attachment 1 name not passed to HtmlEmail", "attachment name 2", attachmentNames.getValues().get(1));
        assertEquals("Attachment 1 description not passed to HtmlEmail", "attachment description 2", attachmentDescriptions.getValues().get(1));
        
    }    
}
