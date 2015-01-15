package nl.knaw.dans.easy.web.authn;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.HomePage;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestRegistrationPage {
    private EasyWicketTester tester;

    @Before
    public void setUp() throws Exception {
        EasyApplicationContextMock ctx = new EasyApplicationContextMock();
        ctx.expectDefaultResources();
        ctx.expectStandardSecurity();
        ctx.expectAuthenticatedAsVisitor();
        ctx.expectNoDatasetsInToolBar(new EmptySearchResult<DatasetSB>());
        KeyValuePair kvp1 = new KeyValuePair("easy-disciplines:1", "DISCIPLINE 1 DISPLAY VALUE");
        KeyValuePair kvp2 = new KeyValuePair("easy-disciplines:2", "DISCIPLINE 2 DISPLAY VALUE");
        KeyValuePair kvp3 = new KeyValuePair("easy-disciplines:3", "DISCIPLINE 3 DISPLAY VALUE");
        KeyValuePair kvp4 = new KeyValuePair("easy-disciplines:4", "DISCIPLINE 4 DISPLAY VALUE");
        ctx.expectDisciplineChoices(kvp1, kvp2, kvp3, kvp4);
        tester = EasyWicketTester.create(ctx);
    }

    @After
    public void reset() {
        TestUtil.cleanup();
    }

    @Test
    public void pageRenderedCorrectly() {
        replayAll();
        tester.startPage(RegistrationPage.class);
        tester.assertRenderedPage(RegistrationPage.class);
        tester.assertComponent("registrationForm", Form.class);
        tester.dumpPage();
        tester.debugComponentTrees();

        tester.assertModelValue("registrationForm:optsForNewsletter", true);
        tester.assertModelValue("registrationForm:logMyActions", true);
        tester.assertModelValue("registrationForm:acceptConditions", false);
        tester.assertDisabled("registrationForm:register");
        tester.assertEnabled("registrationForm:cancelLink");

        assertDisciplineDropdownsFilledInCorrectly();
    }

    private void assertDisciplineDropdownsFilledInCorrectly() {
        @SuppressWarnings("unchecked")
        DropDownChoice<KeyValuePair> choice = (DropDownChoice<KeyValuePair>) tester.getComponentFromLastRenderedPage("registrationForm:discipline1");
        assertEquals(4, choice.getChoices().size());
        @SuppressWarnings("unchecked")
        List<KeyValuePair> choices = (List<KeyValuePair>) choice.getChoices();
        assertEquals("easy-disciplines:1", choices.get(0).getKey());
        assertEquals("easy-disciplines:2", choices.get(1).getKey());
        assertEquals("easy-disciplines:3", choices.get(2).getKey());
        assertEquals("easy-disciplines:4", choices.get(3).getKey());
    }

    @Test
    public void validationTestAllFieldsFilledCorrectly() {
        replayAll();
        tester.startPage(RegistrationPage.class);
        FormTester formTester = tester.newFormTester("registrationForm");
        fillAllFieldsWithCorrectData(formTester);
        formTester.setValue("acceptConditions", true);

        /*
         * Hack alert. Cannot seem to get the Register button to become enabled otherwise; setting the value true on acceptConditions does not trigger the event
         * handler that enables the Register button. See also: http://apache-wicket.1842946.n4.nabble.com/Testing-CheckBox-onSelectionChanged
         * -Object-newSelection-using-FormTester-td2272641.html
         */
        tester.getComponentFromLastRenderedPage("registrationForm:register").setEnabled(true);

        formTester.submit("register");
        tester.assertRenderedPage(HomePage.class);
        tester.debugComponentTrees();

        // verify the atLeastOnce expectations actually occurred
        PowerMock.verifyAll();
    }

    private void fillAllFieldsWithCorrectData(FormTester formTester) {
        formTester.setValue("userId", "jan01");
        formTester.setValue("password", "secret");
        formTester.setValue("confirmPassword", "secret");
        formTester.setValue("email", "jan@jansen.com");
        formTester.setValue("title", "Prof. Dr.");
        formTester.setValue("initials", "J.A.N.");
        formTester.setValue("surname", "Jansen");
        formTester.setValue("function", "Onderzoeker");
        formTester.setValue("telephone", "0123456789");
        formTester.setValue("discipline1", "Humanities");
        formTester.setValue("dai", "1234567890");
        formTester.setValue("organization", "University");
        formTester.setValue("department", "Department");
        formTester.setValue("address", "Adress Lane 1");
        formTester.setValue("postalCode", "1234 AA");
        formTester.setValue("city", "City01");
        formTester.setValue("country", "Country");
        formTester.setValue("acceptConditions", "true");
    }
}
