package nl.knaw.dans.easy.domain.emd.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.easy.domain.emd.validation.archaeology.ArchaeologyFormatValidator;
import nl.knaw.dans.easy.domain.emd.validation.history.HistoryFormatValidator;
import nl.knaw.dans.easy.domain.emd.validation.other.OtherFormatValidator;
import nl.knaw.dans.easy.domain.emd.validation.sociology.SociologyFormatValidator;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.ValidationReport;
import nl.knaw.dans.easy.domain.model.emd.ValidationReporter;
import nl.knaw.dans.easy.domain.model.emd.Validator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;

public class FormatValidator implements Validator
{
    
    private static FormatValidator INSTANCE;
    
    private static Map<MetadataFormat, Validator> VALIDATOR_MAP
        = Collections.synchronizedMap(new HashMap<MetadataFormat, Validator>());
    
    private FormatValidator()
    {
        VALIDATOR_MAP.put(MetadataFormat.ARCHAEOLOGY, ArchaeologyFormatValidator.instance());
        VALIDATOR_MAP.put(MetadataFormat.HISTORY, HistoryFormatValidator.instance());
        VALIDATOR_MAP.put(MetadataFormat.SOCIOLOGY, SociologyFormatValidator.instance());
        VALIDATOR_MAP.put(MetadataFormat.UNSPECIFIED, OtherFormatValidator.instance());
    }
    
    public static FormatValidator instance()
    {
        synchronized (VALIDATOR_MAP)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new FormatValidator();
            }
        }
        return INSTANCE;
    }

    @Override
    public void validate(EasyMetadata emd, ValidationReporter reporter)
    {
        MetadataFormat format = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        if (format == null)
        {
            handleNullFormat(reporter);
        }
        else
        {
            Validator formatValidator = VALIDATOR_MAP.get(format);
            if (formatValidator == null)
            {
                throw new ApplicationException("No FormatValidator for format " + format.toString());
            }
            formatValidator.validate(emd, reporter);
        }
    }

    private void handleNullFormat(ValidationReporter reporter)
    {
        reporter.setMetadataValid(false);
        ValidationReport report = new ValidationReport("MetadataFormat is null.", this);
        reporter.addError(report);
    }
    
    
    

}
