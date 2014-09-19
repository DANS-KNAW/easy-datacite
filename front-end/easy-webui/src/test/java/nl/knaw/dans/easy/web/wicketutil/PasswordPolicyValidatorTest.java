/**
 * 
 */
package nl.knaw.dans.easy.web.wicketutil;

import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for the password policy validator.
 * 
 * @author Herman Suijs
 */
@RunWith(Parameterized.class)
public class PasswordPolicyValidatorTest {
    /**
     * Password to test.
     */
    private final String password;

    /**
     * Validity of the tested password.
     */
    private final boolean valid;

    /**
     * Create a validation test with parameterized passwords.
     * 
     * @param password
     *        to test
     * @param valid
     *        true if password is valid
     */
    public PasswordPolicyValidatorTest(String password, boolean valid) {
        super();
        this.password = password;
        this.valid = valid;

    }

    /**
     * Return a collection of passwords to test.
     * 
     * @return collection of passwords
     */
    @Parameters
    public static Collection<Object[]> getValidPasswords() {
        return Arrays.asList(new Object[][] { {"test1ValidPa$$", true}, {"an0th3rP@ssword", true}, {"invalid", false}, {"invalidpassword", false},
                {"invalidPassword", false}, {"1nvalid", false}, {"noDig1Ts", false}, {"n0Sp3cialChars", false}, {"N0LOWERCASE#", false},
                {"n0uppercase&", false}});

    }

    /**
     * Test valid passwords.
     */
    @Test
    public void testValidPasswords() {
        IValidatable validatable = EasyMock.createMock(IValidatable.class);

        EasyMock.expect(validatable.getValue()).andReturn(this.password).anyTimes();

        if (!this.valid) {
            validatable.error((IValidationError) EasyMock.anyObject());
            EasyMock.expectLastCall().anyTimes();
        }
        EasyMock.replay(validatable);

        PasswordPolicyValidator policy = PasswordPolicyValidator.getInstance();
        policy.validate(validatable);

        EasyMock.verify(validatable);

    }

    /**
     * Test invalid passwords.
     */
    public void testInvalidPasswords() {
        IValidatable validatable = EasyMock.createMock(IValidatable.class);

        EasyMock.expect(validatable.getValue()).andReturn("testPassword@").anyTimes();

        EasyMock.replay(validatable);

        PasswordPolicyValidator policy = PasswordPolicyValidator.getInstance();
        policy.validate(validatable);

        EasyMock.verify(validatable);
    }

}
