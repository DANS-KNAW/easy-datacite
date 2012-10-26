package nl.knaw.dans.easy.domain.emd.validation.base;

import org.joda.time.DateTime;

import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

public class EmbargoValidator implements Validator
{
    @Override
    public void validate(final EasyMetadata emd, final ValidationReporter reporter)
    {
        final DateTime available = emd.getEmdDate().getDateAvailable();
        if (available == null)
            return;
        String message = null;
        if (available.isBeforeNow())
        {
            message = "Embargo should not be in the past: ";
        }
        else if (available.minusYears(2).isAfterNow())
        {
            message = "Embargo should not span more than two years: ";
        }
        if (message != null)
        {
            reporter.setMetadataValid(false);
            reporter.addError(new ValidationReport(message + available, EmdXPath.EMBARGO.getXPath(), this));
        }
    }
}
