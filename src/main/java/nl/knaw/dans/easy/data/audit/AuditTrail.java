package nl.knaw.dans.easy.data.audit;

public interface AuditTrail
{

    void store(AuditRecord<?> auditRecord);

    void close();

}
