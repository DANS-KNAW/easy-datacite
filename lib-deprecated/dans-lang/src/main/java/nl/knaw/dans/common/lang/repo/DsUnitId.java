package nl.knaw.dans.common.lang.repo;

import java.util.regex.Pattern;

public class DsUnitId
{

    private static final String REG_EX = "[a-zA-Z0-9-\\._~$()+\\[\\]]*";
    private static final Pattern PATTERN = Pattern.compile(REG_EX);

    private final String unitId;

    public static boolean isValidId(String value)
    {
        if (value == null || "".equals(value))
        {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }

    public DsUnitId(String unitId)
    {
        if (isValidId(unitId))
        {
            this.unitId = unitId;
        }
        else
        {
            throw new IllegalArgumentException("Not a valid unitId: " + unitId);
        }
    }

    public String getUnitId()
    {
        return unitId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof DsUnitId)
        {
            DsUnitId other = (DsUnitId) obj;
            return this.unitId.equals(other.unitId);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return unitId.length() + unitId.hashCode();
    }

    @Override
    public String toString()
    {
        return unitId;
    }

}
