package nl.knaw.dans.easy.web.template;

import static nl.knaw.dans.easy.web.template.SystemReadonlyLink.WID_LABEL;
import static nl.knaw.dans.easy.web.template.SystemReadonlyLink.WID_LINK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadonlyStatus;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.Whitebox;

public class SystemReadOnlyLinkTest
{
    private static ApplicationContextMock applicationContext;
    private static Object initialAnonymousUser;

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
        try
        {
            createTester().startPage(TestNestedPage.class);
        }
        catch (WicketRuntimeException e)
        {
            // TODO rather something like:
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
        tester.assertInvisible(WID_LINK);
    }

    @Test(expected = IllegalStateException.class)
    public void mustOverride()
    {
        WicketTester tester = createTester();
        tester.startComponent(new SystemReadonlyLink()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onBeforeRender()
            {
                // attempt to override the check for a SecurityOfficer
            }
        });
    }

    @Test
    public void linkInvisibleWithoutAdminRole()
    {
        WicketTester tester = createTester();
        Whitebox.setInternalState(tester.getWicketSession(), EasyUser.class, mockAnonymousAsActiveUser(false));
        PowerMock.replayAll();

        tester.startPage(TestPage.class);
        tester.assertInvisible(WID_LINK);
    }

    @Test
    public void adminClicksLink()
    {
        WicketTester tester = createTester();
        Whitebox.setInternalState(tester.getWicketSession(), EasyUser.class, mockAnonymousAsActiveUser(true));
        PowerMock.replayAll();

        String linkPath = WID_LINK;
        String labelPath = linkPath + ":" + WID_LABEL;
        tester.startPage(TestPage.class);
        tester.assertVisible(linkPath);
        tester.assertEnabled(labelPath);
        tester.assertLabel(labelPath, "system allows read and write");

        tester.clickLink(linkPath);
        tester.assertLabel(labelPath, "SYSTEM IS READ IN ONLY MODE");

        // side effect: restore state for other tests
        tester.clickLink(linkPath);
        tester.assertLabel(labelPath, "system allows read and write");
    }

    public WicketTester createTester()
    {
        EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);
        WicketTester tester = new WicketTester(application);
        
        // tell resource locator were to find test HTML
        tester.getApplication().getResourceSettings().addResourceFolder("src/test/java/");

        return tester;
    }

    @BeforeClass
    public static void init()
    {
        SystemReadonlyStatus systemReadonlyStatus = new SystemReadonlyStatus();
        systemReadonlyStatus.setFile(new File("target/systemReadonlyStatus.propeties"));

        CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadonlyStatus(systemReadonlyStatus);

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("systemReadonlyStatus", systemReadonlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        applicationContext.putBean("security", new Security(codedAuthz));

        initialAnonymousUser = EasyUserAnonymous.getInstance();
    }

    private EasyUserAnonymous mockAnonymousAsActiveUser(boolean isAdmin)
    {
        EasyUserAnonymous admin = PowerMock.createMock(EasyUserAnonymous.class);
        EasyMock.expect(admin.isAnonymous()).andStubReturn(false);
        EasyMock.expect(admin.isActive()).andStubReturn(true);
        EasyMock.expect(admin.hasRole(Role.ADMIN)).andStubReturn(isAdmin);

        Whitebox.setInternalState(EasyUserAnonymous.class, EasyUserAnonymous.getInstance(), admin);
        return admin;
    }

    @After
    public void resetAnonymousUser()
    {
        Whitebox.setInternalState(EasyUserAnonymous.class, EasyUserAnonymous.getInstance(), initialAnonymousUser);
    }
}
