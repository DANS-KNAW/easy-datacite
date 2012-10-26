package nl.knaw.dans.easy.domain.dataset;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.repo.AbstractBinaryUnit;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

public class LicenseUnit extends AbstractBinaryUnit
{

    public static final String UNIT_LABEL = "license.pdf";

    public static final String UNIT_ID = "DATASET_LICENSE";

    public static final String MIME_TYPE = "application/pdf";

    private static final long serialVersionUID = 7132858587281181036L;

    public LicenseUnit(byte[] licenseContent)
    {
        super(UnitControlGroup.ManagedContent);
        try
        {
            setFileContent(licenseContent, UNIT_LABEL, MIME_TYPE);
        }
        catch (IOException e)
        {
            throw new ApplicationException(e);
        }
    }

    public LicenseUnit(File file)
    {
        super(UnitControlGroup.ManagedContent);
        try
        {
            setFile(file);
        }
        catch (IOException e)
        {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isVersionable()
    {
        return true;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

}
