package nl.knaw.dans.easy.data.audit;

import nl.knaw.dans.easy.data.ext.ExternalServices;

public class AdminMailerAuditTrail implements AuditTrail
{

    public AdminMailerAuditTrail()
    {

    }

    @Override
    public void store(AuditRecord<?> auditRecord)
    {
        String name = auditRecord.getSessionUser() == null ? "unknown" : auditRecord.getSessionUser().getDisplayName();
        ExternalServices.getAdminMailer().sendInfoMail(
                "Data mutation by " + name + "\n\n"
                + auditRecord.getRecord());
    }

    @Override
    public void close()
    {
        // nothing to close
    }

}
