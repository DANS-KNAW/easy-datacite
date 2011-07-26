package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.lang.repo.AbstractBinaryUnit;

public class EasyFile extends AbstractBinaryUnit
{ 
    
    public static final String UNIT_ID = "EASY_FILE";

    private static final long serialVersionUID = -3279358975072149658L;
    
    /**
     * Creates a new EasyFile under the control group managed content.
     */
    public EasyFile()
    {
        super(UnitControlGroup.ManagedContent);
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

}
