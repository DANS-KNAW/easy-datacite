package nl.knaw.dans.easy.servicelayer;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.mail.ApplicationMailer;
import nl.knaw.dans.common.lang.mail.ApplicationMailerConfiguration;
import nl.knaw.dans.common.lang.mail.MailerConfiguration;
import nl.knaw.dans.common.lang.mail.Mailer.MailerException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.util.EasyHome;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMailFixture
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractMailFixture.class);

    protected boolean verbose = Tester.isVerbose();
    protected boolean online = "true".equals(Tester.getString("mail.onlinetest"));

    protected static boolean online()
    {
        return "true".equals(Tester.getString("mail.onlinetest"));
    }

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
    }

    protected static ApplicationMailer getMailer() throws MailerException
    {
        MailerConfiguration config = new ApplicationMailerConfiguration();
        config.setSmtpHost(Tester.getString("mail.smtp.host"));
        config.setSenderName("DANS Team");
        config.setFromAddress(getGuineaPig());
        Map<String, String> imageMap = new HashMap<String, String>();
        imageMap.put("easy-logo", EasyHome.getValue() + "/mail/images/easy_logo.png");
        config.setImageMap(imageMap);

        ApplicationMailer appMailer = new ApplicationMailer(config);
        return appMailer;
    }

    protected static String getGuineaPig()
    {
        return Tester.getString("mail.guineapig");
    }

    protected void print(String text)
    {
        logger.debug("\n" + text + "\n");
    }

    protected void sendMail(String text) throws MailerException
    {
        getMailer().sendSimpleMail(this.getClass().getSimpleName(), text, getGuineaPig());
    }

    protected void sendMail(String text, String html) throws MailerException
    {
        getMailer().sendMail(this.getClass().getSimpleName(), text, html, getGuineaPig());
    }

}
