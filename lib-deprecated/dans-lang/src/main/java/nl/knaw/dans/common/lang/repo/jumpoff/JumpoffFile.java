package nl.knaw.dans.common.lang.repo.jumpoff;

import nl.knaw.dans.common.lang.repo.AbstractBinaryUnit;

public class JumpoffFile extends AbstractBinaryUnit {

    public static final String UNIT_ID_PREFIX = "JOF_";

    private static final long serialVersionUID = 6206794542355702912L;

    private final String unitId;

    public JumpoffFile(String id) {
        super(UnitControlGroup.ManagedContent);
        this.unitId = UNIT_ID_PREFIX + id;
    }

    @Override
    public String getUnitId() {
        return unitId;
    }

    @Override
    public boolean isVersionable() {
        return false;
    }

}
