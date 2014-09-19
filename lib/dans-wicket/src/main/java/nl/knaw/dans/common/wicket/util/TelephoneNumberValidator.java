package nl.knaw.dans.common.wicket.util;

import org.apache.wicket.validation.validator.PatternValidator;

public class TelephoneNumberValidator extends PatternValidator {

    public static final String PATTERN_TELEPHONE = "[+]?((\\([0-9- ]+\\))|[0-9- ]+)+";

    private static final long serialVersionUID = 8803635130499334848L;

    private static TelephoneNumberValidator instance;

    public static TelephoneNumberValidator instance() {
        if (instance == null) {
            instance = new TelephoneNumberValidator();
        }
        return instance;
    }

    private TelephoneNumberValidator() {
        super(PATTERN_TELEPHONE);
    }

}
