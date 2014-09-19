package nl.knaw.dans.easy.data.audit;

import java.util.ArrayList;
import java.util.List;

public class Audit {

    private static List<AuditTrail> TRAILERS = new ArrayList<AuditTrail>();

    private static boolean ENABLED;

    public Audit() {

    }

    public static void storeAuditRecord(AuditRecord<?> auditRecord) {
        if (ENABLED) {
            for (AuditTrail trail : TRAILERS) {
                trail.store(auditRecord);
            }
        }
    }

    public void setTrailers(List<AuditTrail> trailers) {
        TRAILERS = trailers;
    }

    public void setEnabled(boolean on) {
        ENABLED = on;
    }

}
