package nl.knaw.dans.easy.domain.model.emd;


public interface ValidationReporter
{
    
    void setMetadataValid(boolean valid);
    
    void addInfo(ValidationReport validationReport);
    
    void addWarning(ValidationReport validationReport);
    
    void addError(ValidationReport validationReport);

}
