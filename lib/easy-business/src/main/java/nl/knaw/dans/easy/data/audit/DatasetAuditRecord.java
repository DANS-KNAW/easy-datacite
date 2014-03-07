package nl.knaw.dans.easy.data.audit;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.aspectj.lang.JoinPoint;

public class DatasetAuditRecord extends AbstractAuditRecord<Dataset>
{

    public DatasetAuditRecord(EasyUser sessionUser, Dataset dataset, JoinPoint joinPoint)
    {
        super(sessionUser, dataset, joinPoint);
    }

    @Override
    public String getTracedObjectId()
    {
        return getTracedObject().getStoreId();
    }

}
