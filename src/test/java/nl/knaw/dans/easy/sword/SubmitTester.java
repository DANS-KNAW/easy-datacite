package nl.knaw.dans.easy.sword;


import nl.knaw.dans.common.lang.mail.Mailer;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.util.EasyHome;

import org.junit.BeforeClass;
import org.junit.Test;

/** Integration test for the configuration. */
public class SubmitTester extends SubmitFixture
// Maven should not run this test because easy.home is required. Therefore the name should not start or end with test.
{

    @BeforeClass
    public static void checkEasyHome() throws Exception
    {
        if (EasyHome.getValue() == null)
            throw new Exception("Please specify the system property '" + EasyHome.EASY_HOME_KEY + "'");
    }

    @Test // TODO test was supposed to touch AbstractNotification.send(...)
    public void submitWithoutMailer() throws Exception
    {
        final Mailer saved = ExternalServices.getMailOffice();
        new ExternalServices().setMailOffice(null);
        execute(false, false, PROPER_ZIP);
        new ExternalServices().setMailOffice(saved);
    }

    @Test
    public void submit() throws Exception
    {
        execute(false, false, PROPER_ZIP);
    }

    @Test
    public void submitVerboseNoOp() throws Exception
    {
        execute(true, true, PROPER_ZIP);
    }

    @Test
    public void submitNoOp() throws Exception
    {
        execute(false, true, PROPER_ZIP);
    }

    @Test 
    public void spatialMetadata() throws Throwable
    {
        execute(false, true, getZip("data-plus-spatial-metadata"));
    }

    @Test 
    public void whiteSpace() throws Throwable
    {
        execute(false, true, getZip("disciplineWithWhiteSpace"));
    }
}
