package nl.knaw.dans.easy.domain.model.emd;


public interface Validator
{
    
    void validate(EasyMetadata emd, ValidationReporter reporter);

}
