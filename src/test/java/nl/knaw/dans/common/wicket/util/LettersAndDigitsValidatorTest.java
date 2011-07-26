package nl.knaw.dans.common.wicket.util;

import java.util.Arrays;
import java.util.Collection;

import nl.knaw.dans.common.wicket.util.LettersAndDigitsValidator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LettersAndDigitsValidatorTest
{
    
    @Parameters
    public static Collection<Object[]> getTestData()
    {
        return Arrays.asList(new Object[][] { //
                //
                        {"123", true}, //
                        {"abc", true}, //
                        {"0", true}, //
                        {"A", true}, //
                        {"123aBc", true}, //
                        {"abC123", true}, //
                        {"#0", false}, //
                        {"+", false}, //
                        {"rrrt!", false}, //
                        {"<target>", false}, //
                        {null, false}, //
                        {"", false}, //
                        {"\\", false} //
                });
    }
    
    private final String input;
    private final boolean valid;
    
    public LettersAndDigitsValidatorTest(String input, boolean valid)
    {
        this.input = input;
        this.valid = valid;
    }
    
    @Test
    public void valid()
    {
        @SuppressWarnings("unchecked")
        IValidatable<String> validatable = EasyMock.createMock(IValidatable.class);
        EasyMock.expect(validatable.getValue()).andReturn(this.input).anyTimes();

        if (!this.valid)
        {
            validatable.error((IValidationError) EasyMock.anyObject());
            EasyMock.expectLastCall().anyTimes();
        }
        EasyMock.replay(validatable);

        LettersAndDigitsValidator.instance().validate(validatable);

        EasyMock.verify(validatable);
    }
}
