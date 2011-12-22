package nl.knaw.dans.easy.domain.download;

import java.util.Set;

import nl.knaw.dans.common.lang.repo.exception.InvalidRelationshipException;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

public class DownloadHistoryRelations extends AbstractRelations<DownloadHistory>
{

    private static final long serialVersionUID = -6811813052690582306L;
    
    private String objectId;
    private String period;
    
    public DownloadHistoryRelations(DownloadHistory dlhSubject)
    {
        super(dlhSubject);
    }

    public DownloadHistoryRelations(DownloadHistory dlhSubject, String objectId)
    {
        super(dlhSubject);
        this.objectId = objectId;
        this.period = dlhSubject.getDownloadList().printPeriod();
        addRelation(RelsConstants.DANS_NS.IS_SUBORDINATE_TO.stringValue(), this.objectId);
        addRelation(RelsConstants.DANS_NS.HAS_DOWNLOAD_HISTORY_OF.stringValue(), this.objectId);
        Relation periodRelation = 
            new Relation(dlhSubject.getStoreId(), 
                    RelsConstants.DANS_NS.HAS_PERIOD.stringValue(), 
                    period, 
                    true, 
                    RelsConstants.RDF_LITERAL);
        try
        {
            addRelation(periodRelation);
        }
        catch (InvalidRelationshipException e)
        {
            throw new ApplicationException("This is a program error.", e);
        }
    }
    
    public String getObjectId()
    {
        if (objectId == null)
        {
            Set<Relation> relations = getRelation(RelsConstants.DANS_NS.HAS_DOWNLOAD_HISTORY_OF.stringValue(), null);
            objectId = (String) relations.iterator().next().object;
        }
        return objectId;
    }

}
