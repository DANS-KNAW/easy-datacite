package nl.knaw.dans.common.wicket.util;

import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TelephoneNumberValidatorTest {
    private final String telephone;
    private final boolean valid;

    public TelephoneNumberValidatorTest(final String telephone, final boolean valid) {
        super();
        this.telephone = telephone;
        this.valid = valid;
    }

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { //
                //
                        {"+20 123 45 67", true}, //
                        {"020-1234567", true}, //
                        {"0", true}, //
                        {"+------------", true}, //
                        {"+1-3-5--6 7-8  ", true}, //
                        {"0.0", false}, //
                        {"0,0", false}, //
                        {"0+1", false}, //
                        {"++---", false}, //
                        {"++", false}, //
                        {null, false}, //
                        {"", false}, //
                        {"+", false} //
                });
    }

    @Test
    public void valid() {
        @SuppressWarnings("unchecked")
        IValidatable<String> validatable = EasyMock.createMock(IValidatable.class);
        EasyMock.expect(validatable.getValue()).andReturn(this.telephone).anyTimes();

        if (!this.valid) {
            validatable.error((IValidationError) EasyMock.anyObject());
            EasyMock.expectLastCall().anyTimes();
        }
        EasyMock.replay(validatable);

        TelephoneNumberValidator tnv = TelephoneNumberValidator.instance();
        tnv.validate(validatable);

        EasyMock.verify(validatable);
    }
}
