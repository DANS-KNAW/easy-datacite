package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Services.class, Security.class, StatisticsLogger.class, EasySession.class})
public class TestRegistrationPage
{
    private WicketTester tester;
    private EasySession easySessionMock;
    private StatisticsLogger statisticsLoggerMock;
    private DepositService depositServiceMock;
    private UserService userServiceMock;
    private ChoiceList choiceList;
    private Capture<Registration> capturedRegistration;

    @Before
    public void setUp() throws Exception
    {
        ApplicationContextMock ctx = new ApplicationContextMock();
        ctx.putBean("editableContentHome", new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable")));
        ctx.putBean("staticContentBaseUrl", "http://develop01.dans.knaw.nl/statics");
        EasyWicketApplication app = new EasyWicketApplication();
        app.setApplicationContext(ctx);
        tester = new WicketTester(app);
        setUpAuthz();
        setUpEasySessionMock();
        setUpStatisticsLoggerMock();
        setUpAnonymousUserMock();
        setUpServicesMocks();
    }

    private void setUpAuthz()
    {
        /*
         * Attention! We are not mocking Authz, but using the CodedAuthz class. This class contains the
         * authorization rules used in production as well.
         */
        mockStatic(Security.class);
        expect(Security.getAuthz()).andReturn(createCodedAuthz()).anyTimes();
    }

    private CodedAuthz createCodedAuthz()
    {
        CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(createSystemReadOnlyBean());
        return codedAuthz;
    }

    private SystemReadOnlyStatus createSystemReadOnlyBean()
    {
        SystemReadOnlyStatus systemReadOnlyStatus = new SystemReadOnlyStatus(new File("target/SystemReadOnlyStatus.properties"));
        return systemReadOnlyStatus;
    }

    private void setUpEasySessionMock()
    {
        mockStatic(EasySession.class);
        easySessionMock = PowerMock.createMock(EasySession.class);
        expect(EasySession.get()).andReturn(easySessionMock).anyTimes();
    }

    private void setUpStatisticsLoggerMock()
    {
        mockStatic(StatisticsLogger.class);
        statisticsLoggerMock = PowerMock.createMock(StatisticsLogger.class);
        expect(StatisticsLogger.getInstance()).andReturn(statisticsLoggerMock).anyTimes();
        statisticsLoggerMock.logEvent(isA(StatisticsEvent.class));
        PowerMock.expectLastCall().anyTimes();
    }

    private void setUpAnonymousUserMock()
    {
        expectUser(EasyUserAnonymous.getInstance());
    }

    private void setUpServicesMocks() throws Exception
    {
        mockStatic(Services.class);
        depositServiceMock = PowerMock.createMock(DepositService.class);
        expect(Services.getDepositService()).andReturn(depositServiceMock).anyTimes();
        setUpChoiceList();
        expect(depositServiceMock.getChoices(isA(String.class), isNull(Locale.class))).andReturn(choiceList).anyTimes();
        userServiceMock = PowerMock.createMock(UserService.class);
        expect(Services.getUserService()).andReturn(userServiceMock).anyTimes();
        final Registration registration = PowerMock.createMock(Registration.class);
        expect(userServiceMock.handleRegistrationRequest(capture(capturedRegistration))).andReturn(registration).anyTimes();
        expect(registration.isCompleted()).andReturn(true).anyTimes();
    }

    private void setUpChoiceList()
    {
        List<KeyValuePair> choices = new ArrayList<KeyValuePair>();
        choices.add(new KeyValuePair("easy-disciplines:1", "DISCIPLINE 1 DISPLAY VALUE"));
        choices.add(new KeyValuePair("easy-disciplines:2", "DISCIPLINE 2 DISPLAY VALUE"));
        choices.add(new KeyValuePair("easy-disciplines:3", "DISCIPLINE 3 DISPLAY VALUE"));
        choices.add(new KeyValuePair("easy-disciplines:4", "DISCIPLINE 4 DISPLAY VALUE"));
        choiceList = new ChoiceList(choices);
    }

    @Test
    public void pageRenderedCorrectly()
    {
        replayAll();
        renderRegistrationPage();
    }

    private void renderRegistrationPage()
    {
        tester.startPage(RegistrationPage.class);
        tester.assertRenderedPage(RegistrationPage.class);
        tester.assertComponent("registrationForm", Form.class);

        // TODO: Andere components asserten!!

        assertDisciplineDropdownsFilledInCorrectly();
    }

    private void assertDisciplineDropdownsFilledInCorrectly()
    {
        DropDownChoice<KeyValuePair> choice = (DropDownChoice<KeyValuePair>) tester.getComponentFromLastRenderedPage("registrationForm:discipline1");
        assertEquals(4, choice.getChoices().size());
        List<KeyValuePair> choices = (List<KeyValuePair>) choice.getChoices();
        assertEquals("easy-disciplines:1", choices.get(0).getKey());
        assertEquals("easy-disciplines:2", choices.get(1).getKey());
        assertEquals("easy-disciplines:3", choices.get(2).getKey());
        assertEquals("easy-disciplines:4", choices.get(3).getKey());
    }

    @Test
    @Ignore
    public void validationTestAllFieldsFilledCorrectly()
    {
        replayAll();
        tester.startPage(RegistrationPage.class);
        tester.assertRenderedPage(RegistrationPage.class);
        FormTester formTester = tester.newFormTester("registrationForm");
        fillAllFieldsWithCorrectData(formTester);
        formTester.setValue("acceptConditions", true);

        /*
         * Hack alert. Cannot seem to get the Register button to become enabled otherwise; setting the
         * value true on acceptConditions does not trigger the event handler that enables the Register
         * button. See also:
         * http://apache-wicket.1842946.n4.nabble.com/Testing-CheckBox-onSelectionChanged
         * -Object-newSelection-using-FormTester-td2272641.html
         */
        tester.getComponentFromLastRenderedPage("registrationForm:register").setEnabled(true);

        formTester.submit("register");
        tester.assertRenderedPage(InfoPage.class);
        assertEquals("jan01", capturedRegistration.getValue().getUser().getId());
    }

    private void fillAllFieldsWithCorrectData(FormTester formTester)
    {
        formTester.setValue("userId", "jan01");
        formTester.setValue("password", "secret");
        formTester.setValue("confirmPassword", "secret");
        formTester.setValue("email", "jan@jansen.com");
        formTester.setValue("title", "Prof. Dr.");
        formTester.setValue("initials", "J.A.N.");
        formTester.setValue("surname", "Jansen");
        formTester.setValue("function", "Onderzoeker");
        formTester.setValue("telephone", "0123456789");
        formTester.setValue("discipline", "Humanities");
        formTester.setValue("dai", "1234567890");
        formTester.setValue("organization", "University");
        formTester.setValue("department", "Department");
        formTester.setValue("address", "Adress Lane 1");
        formTester.setValue("postalCode", "1234 AA");
        formTester.setValue("city", "City01");
        formTester.setValue("country", "Country");
        formTester.setValue("acceptConditions", "true");
    }

    private void expectUser(EasyUser user)
    {
        expect(easySessionMock.getUser()).andReturn(user).anyTimes();
        expect(EasySession.getSessionUser()).andReturn(user).anyTimes();
        expect(easySessionMock.getContextParameters()).andReturn(new ContextParameters(user)).anyTimes();
    }
}
