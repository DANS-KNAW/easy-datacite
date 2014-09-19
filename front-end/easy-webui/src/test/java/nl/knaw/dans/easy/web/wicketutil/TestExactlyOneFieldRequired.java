package nl.knaw.dans.easy.web.wicketutil;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.replayAll;
import nl.knaw.dans.common.wicket.util.RequireExactlyOneValidator;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

// only required to mock final and static methods
@RunWith(PowerMockRunner.class)
@PrepareForTest({FormComponent.class})
public class TestExactlyOneFieldRequired {
    private boolean errorCalled = false;

    @Test
    public void twoComponentsOneFilledInIsCorrect() throws Exception {
        FormComponent comp1 = PowerMock.createMock(FormComponent.class);
        FormComponent comp2 = PowerMock.createMock(FormComponent.class);
        expect(comp1.getValue()).andReturn("kalle").anyTimes();
        expect(comp2.getValue()).andReturn("").anyTimes();
        Form form = PowerMock.createMock(Form.class);
        replayAll();
        RequireExactlyOneValidator requireExactlyOneValidator = new RequireExactlyOneValidator(new FormComponent[] {comp1, comp2});
        requireExactlyOneValidator.validate(form);
    }

    @Test
    public void twoComponentsTwoFilledInIsNotCorrect() throws Exception {
        FormComponent comp1 = PowerMock.createMock(FormComponent.class);
        FormComponent comp2 = PowerMock.createMock(FormComponent.class);
        expect(comp1.getValue()).andReturn("kalle").anyTimes();
        expect(comp2.getValue()).andReturn("kalle.kivi@kallio.com").anyTimes();
        Form form = PowerMock.createMock(Form.class);
        replayAll();
        RequireExactlyOneValidator requireExactlyOneValidator = new RequireExactlyOneValidator(comp1, comp2) {
            public void error(org.apache.wicket.markup.html.form.FormComponent<?> fc) {
                errorCalled = true;
            };
        };
        requireExactlyOneValidator.validate(form);
        assertTrue("Validator did not detect that too many fields were filled in", errorCalled);
    }

    @After
    public void reset() {
        PowerMock.resetAll();
    }
}
