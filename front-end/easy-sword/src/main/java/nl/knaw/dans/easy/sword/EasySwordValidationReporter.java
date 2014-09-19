package nl.knaw.dans.easy.sword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReport;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;

public class EasySwordValidationReporter implements ValidationReporter {

    private static Logger logger = LoggerFactory.getLogger(EasySwordValidationReporter.class);

    private boolean valid = true;
    private List<ValidationReport> infoReports = new ArrayList<ValidationReport>();
    private List<ValidationReport> warningReports = new ArrayList<ValidationReport>();
    private List<ValidationReport> errorReports = new ArrayList<ValidationReport>();

    @Override
    public void setMetadataValid(boolean valid) {
        this.valid &= valid;
    }

    public boolean isMetadataValid() {
        logger.debug(getMsgs());
        return valid;
    }

    public String getMessages() {
        return getMsgs();
    }

    private String getMsgs() {
        return infoReports.size() + " info reports " + Arrays.toString(infoReports.toArray()) + "\n"//
                + warningReports.size() + " warning reports " + Arrays.toString(warningReports.toArray()) + "\n" //
                + errorReports.size() + " error reports " + Arrays.toString(errorReports.toArray());
    }

    @Override
    public void addInfo(ValidationReport validationReport) {
        logger.debug("INFO" + validationReport.toString());
        infoReports.add(validationReport);
    }

    @Override
    public void addWarning(ValidationReport validationReport) {
        logger.debug("WARNING" + validationReport.toString());
        warningReports.add(validationReport);
    }

    @Override
    public void addError(ValidationReport validationReport) {
        logger.debug("ERROR" + validationReport.toString());
        errorReports.add(validationReport);
    }
}
