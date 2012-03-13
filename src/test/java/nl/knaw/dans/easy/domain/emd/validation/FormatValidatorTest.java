package nl.knaw.dans.easy.domain.emd.validation;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.emd.validation.archaeology.EasSpatialValidator;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.ValidationReport;
import nl.knaw.dans.easy.domain.model.emd.ValidationReporter;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial.Point;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatValidatorTest
{
    private static final Logger logger = LoggerFactory.getLogger(FormatValidatorTest.class);
    
    private boolean verbose = Tester.isVerbose();
    
    
    @Test
    public void testFormatRecognition()
    {
        EasyMetadata emd = new EasyMetadataImpl(null);
        TestValidationReporter reporter = new TestValidationReporter();
        
        FormatValidator.instance().validate(emd, reporter);
        
        assertFalse(reporter.isMetadataValid());
        assertEquals(0, reporter.infoReports.size());
        assertEquals(0, reporter.warningReports.size());
        assertEquals(1, reporter.errorReports.size());
        
        if (verbose)
            reporter.printReports();
    }
    
    @Test
    public void testFormatArchaeologyWithInvalidSpatial()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.ARCHAEOLOGY);
        Spatial spatial = new Spatial("Amsterdam", new Point("RD", "1", "2"));
        emd.getEmdCoverage().getEasSpatial().add(spatial);
        
        TestValidationReporter reporter = new TestValidationReporter();
        
        FormatValidator.instance().validate(emd, reporter);
        
        assertFalse(reporter.isMetadataValid());
        assertEquals(0, reporter.infoReports.size());
        assertEquals(0, reporter.warningReports.size());
        assertEquals(1, reporter.errorReports.size());
        
        if (verbose)
            reporter.printReports();
    }
    
    @Test
    @Ignore
    public void testFormatArchaeologyWithInvalidSpatial2()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.ARCHAEOLOGY);
        Point point = new Point("BLA", "1", "2");
        point.setSchemeId(EasSpatialValidator.LIST_ID);
        Spatial spatial = new Spatial("Amsterdam", point);
        emd.getEmdCoverage().getEasSpatial().add(spatial);
        
        TestValidationReporter reporter = new TestValidationReporter();
        
        FormatValidator.instance().validate(emd, reporter);
        
        assertFalse(reporter.isMetadataValid());
        assertEquals(0, reporter.infoReports.size());
        assertEquals(0, reporter.warningReports.size());
        assertEquals(1, reporter.errorReports.size());
        
        if (verbose)
            reporter.printReports();
    }
    
    @Test
    @Ignore
    public void testFormatArchaeologyWithInvalidSpatial3()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.ARCHAEOLOGY);
        Point point = new Point(null, "1", "2");
        point.setSchemeId(EasSpatialValidator.LIST_ID);
        Spatial spatial = new Spatial("Amsterdam", point);
        emd.getEmdCoverage().getEasSpatial().add(spatial);
        
        TestValidationReporter reporter = new TestValidationReporter();
        
        FormatValidator.instance().validate(emd, reporter);
        
        assertFalse(reporter.isMetadataValid());
        assertEquals(0, reporter.infoReports.size());
        assertEquals(0, reporter.warningReports.size());
        assertEquals(1, reporter.errorReports.size());
        
        if (verbose)
            reporter.printReports();
    }
    
    
    private static class TestValidationReporter implements ValidationReporter
    {
        
        private boolean valid = true;
        private List<ValidationReport> infoReports = new ArrayList<ValidationReport>();
        private List<ValidationReport> warningReports = new ArrayList<ValidationReport>();
        private List<ValidationReport> errorReports = new ArrayList<ValidationReport>();

        @Override
        public void setMetadataValid(boolean valid)
        {
            this.valid &= valid;
        }
        
        public boolean isMetadataValid()
        {
            return valid;
        }

        @Override
        public void addInfo(ValidationReport validationReport)
        {
            infoReports.add(validationReport);
        }

        @Override
        public void addWarning(ValidationReport validationReport)
        {
            warningReports.add(validationReport);
        }

        @Override
        public void addError(ValidationReport validationReport)
        {
            errorReports.add(validationReport);
        }
        
        public void printReports()
        {
            logger.debug(infoReports.size() + " info reports");
            for (ValidationReport report : infoReports)
            {
                logger.debug(report.toString());
            }
            
            logger.debug(warningReports.size() + " warning reports");
            for (ValidationReport report : warningReports)
            {
                logger.debug(report.toString());
            }
            
            logger.debug(errorReports.size() + " error reports");
            for (ValidationReport report : errorReports)
            {
                logger.debug(report.toString());
            }
        }
        
    }

}
