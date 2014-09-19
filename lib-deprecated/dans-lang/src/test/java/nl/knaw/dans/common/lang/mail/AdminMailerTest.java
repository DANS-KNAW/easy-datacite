package nl.knaw.dans.common.lang.mail;

import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.BeforeClass;
import org.junit.Test;

public class AdminMailerTest {

    private static FooMailer mailer;

    @BeforeClass
    public static void beforeClass() {
        mailer = new FooMailer();
        mailer.setAdminMailAddressesCS("foo.bar@dans.knaw.nl,foo.baz@foo.com");
        mailer.setSendOnStarting(true);
        mailer.setSendOnClosing(true);
    }

    @Test
    public void sendErrorReport() throws Exception {
        mailer.sendApplicationStarting();
        checkMail("Starting");

        String check = "These strings are much too long";
        Throwable e = new IndexOutOfBoundsException(check);
        Throwable t = new Exception("test exception", e);
        mailer.sendEmergencyMail("something disastrous happened!", t);
        checkMail(check);

        check = "Ooh! ooh! ooh!";
        mailer.sendEmergencyMail(new Exception(check));
        checkMail(check);

        mailer.sendApplicationClosing();
        checkMail("Closing");
    }

    private void checkMail(String check) {
        if (Tester.isVerbose())
            System.out.println("\n--------- subject --------\n" + mailer.subject);

        if (Tester.isVerbose())
            System.out.println("\n+++++++++ message ++++++++\n" + mailer.text + "\n");

        assertTrue(mailer.text.contains(check));
    }

    static class FooMailer extends AdminMailer {

        String subject;
        String text;
        boolean fail;

        public FooMailer() {
            super(null, "Easy");
        }

        @Override
        protected boolean send(String subject, String text) {
            this.subject = subject;
            this.text = text;
            return !fail;
        }

        public void setFail(boolean fail) {
            this.fail = fail;
        }
    }

}
