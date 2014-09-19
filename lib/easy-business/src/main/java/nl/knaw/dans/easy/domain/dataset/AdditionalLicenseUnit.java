package nl.knaw.dans.easy.domain.dataset;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.repo.AbstractBinaryUnit;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

public class AdditionalLicenseUnit extends AbstractBinaryUnit {

    public static final String UNIT_LABEL = "additional_license.pdf";

    public static final String UNIT_ID = "ADDITIONAL_LICENSE";

    private static final long serialVersionUID = 7132858587281181036L;

    public AdditionalLicenseUnit(File file) {
        super(UnitControlGroup.ManagedContent);
        try {
            setFile(file);
        }
        catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isVersionable() {
        return true;
    }

    public String getUnitId() {
        return UNIT_ID;
    }

}
