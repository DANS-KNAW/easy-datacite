/**
 * 
 */
package nl.knaw.dans.easy.web.wicketutil;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Password policy. This Validator is based on the CompoundValidator. It uses seperate validators to
 * check for all the rules, which each use their own error message. In contrast to the CompoundValidator,
 * this validator keeps checking even if the validatable is already invalid.
 * 
 * @author Herman Suijs
 */
public final class PasswordPolicyValidator implements IValidator<String>
{

    public static final int MIN_PASSWORD_LENGTH = 6;
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordPolicyValidator.class);
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -623135553060122902L;

    /**
     * Singleton instance.
     */
    private static final PasswordPolicyValidator INSTANCE = new PasswordPolicyValidator();

    /**
     * List of validators to check.
     */
    private final List<IValidator<String>> validators = new ArrayList<IValidator<String>>(2);

    /**
     * Validator to check minimum length of password.
     * 
     * @author Herman Suijs
     */
    public static class PasswordMinimumLengthValidator extends PatternValidator
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = -7671962296167933351L;

        /**
         * Default constructor.
         * 
         * @param length
         *        minimum length of the password
         */
        public PasswordMinimumLengthValidator(final int length)
        {
            super("^.{" + length + ",}$");
            LOGGER.debug("Pattern used: ^.{" + length + ",}$");
        }

    }

    /**
     * Validator to check for lower case characters.
     * 
     * @author Herman Suijs
     */
    public static class RequireLowerCaseValidator extends PatternValidator
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = 3163540731485921150L;

        /**
         * Default constructor.
         */
        public RequireLowerCaseValidator()
        {
            this(1);
        }

        /**
         * Constructor with number of lower case characters expected.
         * 
         * @param numberLowerCase
         *        expected
         */
        public RequireLowerCaseValidator(final int numberLowerCase)
        {
            super("^(?=.*[a-z]{" + numberLowerCase + ",}).*$");
        }

    }

    /**
     * Validator to check for upper case characters.
     * 
     * @author Herman Suijs
     */
    public static class RequireUpperCaseValidator extends PatternValidator
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = -3751307545326049168L;

        /**
         * Default constructor.
         */
        public RequireUpperCaseValidator()
        {
            this(1);
        }

        /**
         * Constructor with number of upper case characters expected.
         * 
         * @param numberUpperCase
         *        expected
         */
        public RequireUpperCaseValidator(final int numberUpperCase)
        {
            super("^(?=.*[A-Z]{" + numberUpperCase + ",}).*$");
        }
    }

    /**
     * Validator to check for digits.
     * 
     * @author Herman Suijs
     */
    public static class RequireDigitValidator extends PatternValidator
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = 5117869330956148055L;

        /**
         * Default constructor.
         */
        public RequireDigitValidator()
        {
            this(1);
        }

        /**
         * Constructor with number of digits expected.
         * 
         * @param numberDigits
         *        expected
         */
        public RequireDigitValidator(final int numberDigits)
        {
            super("^(?=.*\\d{" + numberDigits + ",}).*$");
        }
    }

    /**
     * Validator to check for special characters.
     * 
     * @author Herman Suijs
     */
    public static class RequireSpecialCharacterValidator extends PatternValidator
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = 5473906611894364227L;

        /**
         * Default constructor.
         */
        public RequireSpecialCharacterValidator()
        {
            this(1);
        }

        /**
         * Constructor with number of special characters expected.
         * 
         * @param numberSpecials
         *        expected
         */
        public RequireSpecialCharacterValidator(final int numberSpecials)
        {
            // super("^(?=.*\\W).*$");
            super("^(?=.*\\W{" + numberSpecials + ",}).*$");
        }
    }

    /**
     * Return the PasswordPolicyValidator instance.
     * 
     * @return passwordPolicyValidator instance
     */
    public static PasswordPolicyValidator getInstance()
    {
        return INSTANCE;
    }

    /**
     * Default protected constructor. Defines the pattern to check for the passwordPolicy. <br/>
     * Pattern requires 1 or more digits, 1 or more lower and upper case character and a special
     * character with a minimum of 8. Complete pattern: "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$"
     */
    private PasswordPolicyValidator()
    {
        add(passwordMinimumLengthValidator(MIN_PASSWORD_LENGTH));
        // add(requireLowerCaseValidator(1));
        // add(requireUpperCaseValidator(1));
        // add(requireDigitValidator(1));
        // add(requireSpecialCharValidator(1));
    }

    /**
     * Return a length validator.
     * 
     * @param length
     *        to check
     * @return minimum length validator.
     */
    public static PasswordMinimumLengthValidator passwordMinimumLengthValidator(final int length)
    {
        return new PasswordMinimumLengthValidator(length);
    }

    /**
     * Return lower case validator.
     * 
     * @param numberLowerCase
     *        expected
     * @return lower case validator
     */
    public static RequireLowerCaseValidator requireLowerCaseValidator(final int numberLowerCase)
    {
        return new RequireLowerCaseValidator(numberLowerCase);
    }

    /**
     * Return upper case validator.
     * 
     * @param numberUpperCase
     *        expected
     * @return upper case validator.
     */
    public static RequireUpperCaseValidator requireUpperCaseValidator(final int numberUpperCase)
    {
        return new RequireUpperCaseValidator(numberUpperCase);
    }

    /**
     * Return digit validator.
     * 
     * @param numberDigits
     *        expected
     * @return digit validator.
     */
    public static RequireDigitValidator requireDigitValidator(final int numberDigits)
    {
        return new RequireDigitValidator(numberDigits);
    }

    /**
     * Return special characters validator.
     * 
     * @param numberSpecialChars
     *        expected
     * @return special characters validator.
     */
    public static RequireSpecialCharacterValidator requireSpecialCharValidator(final int numberSpecialChars)
    {
        return new RequireSpecialCharacterValidator(numberSpecialChars);
    }

    /**
     * Adds an <code>IValidator</code> to the chain of validators.
     * 
     * @param validator
     *        an <code>IValidator</code> to be added
     * @return this <code>ValidationError</code> for chaining purposes
     */
    public PasswordPolicyValidator add(final IValidator<String> validator)
    {
        if (validator == null)
        {
            throw new IllegalArgumentException("Argument `validator` cannot be null");
        }
        this.validators.add(validator);

        return this;
    }

    /**
     * Validate the validatable field.
     * 
     * @param validatable
     *        validatable field
     * @see org.apache.wicket.validation.IValidator#validate(org.apache.wicket.validation.IValidatable)
     */
    public void validate(final IValidatable<String> validatable)
    {
        for (IValidator<String> validator : this.validators)
        {
            validator.validate(validatable);
        }

    }
}
