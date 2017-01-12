package nl.knaw.dans.easy.domain.emd.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.easy.domain.emd.validation.other.OtherFormatValidator;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

public class FormatValidator implements Validator {

    private static FormatValidator INSTANCE;

    private static Map<MetadataFormat, Validator> VALIDATOR_MAP = Collections.synchronizedMap(new HashMap<MetadataFormat, Validator>());

    private FormatValidator() {
        VALIDATOR_MAP.put(MetadataFormat.DEFAULT, OtherFormatValidator.instance());
    }

    public static FormatValidator instance() {
        synchronized (VALIDATOR_MAP) {
            if (INSTANCE == null) {
                INSTANCE = new FormatValidator();
            }
        }
        return INSTANCE;
    }

    @Override
    public void validate(EasyMetadata emd, ValidationReporter reporter) {
        Validator formatValidator = VALIDATOR_MAP.get(MetadataFormat.DEFAULT);
        formatValidator.validate(emd, reporter);
    }
}
