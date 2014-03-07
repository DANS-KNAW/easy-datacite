package nl.knaw.dans.easy.data.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyslogAuditTrail implements AuditTrail
{
    private static final Logger logger = LoggerFactory.getLogger(SyslogAuditTrail.class);

    @Override
    public void store(AuditRecord<?> auditRecord)
    {
        logger.info(auditRecord.getRecord());
    }

    @Override
    public void close()
    {
        // nothing to close

    }

    protected void test(String message)
    {
        logger.info(message);
    }

}
