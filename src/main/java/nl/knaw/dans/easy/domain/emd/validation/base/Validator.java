package nl.knaw.dans.easy.domain.emd.validation.base;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

public interface Validator
{

    void validate(EasyMetadata emd, ValidationReporter reporter);

}
