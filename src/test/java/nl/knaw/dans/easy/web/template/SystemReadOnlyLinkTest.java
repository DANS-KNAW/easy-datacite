package nl.knaw.dans.easy.web.template;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class SystemReadOnlyLinkTest
{
    public static class TestPanel extends Panel
    {
        private static final long serialVersionUID = 1L;

        public TestPanel(String id)
        {
            super(id);
            add(new SystemReadonlyLink());
        }
    }

    public static class TestNestedPage extends WebPage
    {
        public TestNestedPage()
        {
            add(new TestPanel("panel"));
        }
    }

    public static class TestPage extends WebPage
    {
        public TestPage()
        {
            add(new SystemReadonlyLink());
        }
    }

    @Test
    public void linkOnPanelNotAllowed()
    {
        WicketTester tester = createTester();
        try
        {
            tester.startPage(TestNestedPage.class);
        }
        catch (WicketRuntimeException e)
        {
            // assertThat(e.getCause().getClass(), isThrowable(SecurityException.class));
            String message = "expected cause " + SecurityException.class.getName() + " but got " + e.getCause().getClass().getName();
            assertTrue(message, e.getCause() instanceof SecurityException);

            assertThat(e.getCause().getMessage(), containsString("WebPage"));
        }
    }

    @Test
    public void linkInvisibleForAnonymous()
    {
        WicketTester tester = createTester();
        tester.startPage(TestPage.class);
        tester.assertInvisible(CodedAuthz.SYSTEM_IS_READ_ONLY);
    }

    private WicketTester createTester()
    {
        new Security(new CodedAuthz()); // sets the static variable
        EasyWicketApplication app = new EasyWicketApplication();
        app.setApplicationContext(new ApplicationContextMock());
        WicketTester tester = new WicketTester(app);
        return tester;
    }
}
