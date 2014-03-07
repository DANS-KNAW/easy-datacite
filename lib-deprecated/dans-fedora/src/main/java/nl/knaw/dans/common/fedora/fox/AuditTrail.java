package nl.knaw.dans.common.fedora.fox;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class AuditTrail extends AbstractJiBXObject<AuditTrail>
{

    public static final String STREAM_ID = "AUDIT";

    private static final long serialVersionUID = -2736481819133933023L;

    private List<AuditRecord> records = new ArrayList<AuditRecord>();

    public List<AuditRecord> getRecords()
    {
        return records;
    }

}
