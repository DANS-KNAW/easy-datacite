package nl.knaw.dans.easy.web.main;

import static nl.knaw.dans.easy.web.main.SystemReadOnlyLink.WICKET_ID_LABEL;
import static nl.knaw.dans.easy.web.main.SystemReadOnlyLink.WICKET_ID_LINK;
import static org.junit.Assert.assertTrue;

import java.io.File;

import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.panel.Panel;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.Whitebox;

public class SystemReadOnlyLinkTest {
    private static EasyApplicationContextMock applicationContext;
    private static Object initialAnonymousUser;

    public static class TestPanel extends Panel {
        private static final long serialVersionUID = 1L;

        public TestPanel(String id) {
            super(id);
            add(new SystemReadOnlyLink());
        }
    }

    public static class TestNestedPage extends AbstractEasyPage {
        public TestNestedPage() {
            add(new TestPanel("panel"));
        }
    }

    public static class TestPage extends AbstractEasyPage {
        public TestPage() {
            add(new SystemReadOnlyLink());
        }
    }

    @Test
    public void noSecurityOfficerForPanel() {
        try {
            createTester().startPage(TestNestedPage.class);
        }
        catch (WicketRuntimeException e) {
            // TODO rather something like:
            // assertThat(e.getCause().getClass(), isThrowable(SecurityException.class));
            String message = "expected cause " + SecurityException.class.getName() + " but got " + e.getCause().getClass().getName();
            assertTrue(message, e.getCause() instanceof SecurityException);
        }
    }

    @Test
    public void linkInvisibleForAnonymous() {
        EasyWicketTester tester = createTester();
        tester.startPage(TestPage.class);
        tester.assertInvisible(WICKET_ID_LINK);
    }

    @Test
    public void linkInvisibleWithoutAdminRole() {
        EasyWicketTester tester = createTester();
        Whitebox.setInternalState(tester.getWicketSession(), EasyUser.class, mockAnonymousAsActiveUser(false));
        PowerMock.replayAll();

        tester.startPage(TestPage.class);
        tester.assertInvisible(WICKET_ID_LINK);
    }

    @Test
    public void adminClicksLink() {
        EasyWicketTester tester = createTester();
        Whitebox.setInternalState(tester.getWicketSession(), EasyUser.class, mockAnonymousAsActiveUser(true));
        PowerMock.replayAll();

        String linkPath = WICKET_ID_LINK;
        String labelPath = linkPath + ":" + WICKET_ID_LABEL;
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

    private EasyWicketTester createTester() {
        EasyWicketTester tester = EasyWicketTester.create(applicationContext);

        // tell resource locator were to find test HTML
        tester.getApplication().getResourceSettings().addResourceFolder("src/test/java/");

        return tester;
    }

    @BeforeClass
    public static void mockApplicationContext() throws Exception {
        SystemReadOnlyStatus systemReadOnlyStatus = new SystemReadOnlyStatus(new File("target/systemReadonlyStatus.properties"));

        CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.setSystemReadOnlyStatus(systemReadOnlyStatus);
        applicationContext.setAuthz(codedAuthz);
        applicationContext.setSecurity(new Security(codedAuthz));

        initialAnonymousUser = EasyUserAnonymous.getInstance();
    }

    private EasyUserAnonymous mockAnonymousAsActiveUser(boolean isAdmin) {
        EasyUserAnonymous admin = PowerMock.createMock(EasyUserAnonymous.class);
        EasyMock.expect(admin.isAnonymous()).andStubReturn(false);
        EasyMock.expect(admin.isActive()).andStubReturn(true);
        EasyMock.expect(admin.hasRole(Role.ADMIN)).andStubReturn(isAdmin);

        Whitebox.setInternalState(EasyUserAnonymous.class, EasyUserAnonymous.getInstance(), admin);
        return admin;
    }

    @After
    public void resetAnonymousUser() {
        Whitebox.setInternalState(EasyUserAnonymous.class, EasyUserAnonymous.getInstance(), initialAnonymousUser);
    }
}
