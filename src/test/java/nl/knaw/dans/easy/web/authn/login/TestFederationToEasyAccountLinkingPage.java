package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.easy.EasyWicketTester;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestFederationToEasyAccountLinkingPage extends Fixture
{
    private static FederationUser federationUser;

    @Before
    public void mockFederationUser() throws Exception
    {
        federationUser = PowerMock.createMock(FederationUser.class);
        EasyMock.expect(federationUser.getUserDescription()).andStubReturn("mocked user description");
        EasyMock.expect(federationUser.getUserId()).andStubReturn("mocked user ID");
        EasyMock.expect(federationUser.getEmail()).andStubReturn("mocked email");
        EasyMock.expect(federationUser.getGivenName()).andStubReturn("mocked given name");
        EasyMock.expect(federationUser.getSurName()).andStubReturn("mocked surname");
        EasyMock.expect(federationUser.getHomeOrg()).andStubReturn("mocked home org");
    }

    @Test
    public void smokeTest() throws Exception
    {
        tester = init();
        tester.dumpPage();
        tester.assertInvisible(COMMON_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertVisible(TOKEN_FIELD);
        tester.assertVisible(USER_ID_FIELD);
        tester.assertVisible(PASSWORD_FIELD);
        tester.assertVisible(REG_LOGIN_SUBMIT);
        tester.assertVisible(FORGOTTEN_LINK);
    }

    @Test
    public void emptyLogin() throws Exception
    {
        tester = init();
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(new ITestPageSource()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Page getTestPage()
            {
                return new FederationToEasyAccountLinkingPage(federationUser);
            }
        });
        return tester;
    }
}
